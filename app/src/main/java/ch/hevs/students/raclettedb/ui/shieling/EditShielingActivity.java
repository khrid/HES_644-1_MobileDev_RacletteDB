package ch.hevs.students.raclettedb.ui.shieling;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.CustomSupportMapFragment;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class EditShielingActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-" + "EditShielingActivity";

    private ShielingEntity shieling = new ShielingEntity();
    private boolean isEditMode;
    private String toastString;
    private EditText etShielingName;
    private EditText etShielingDescription;
    private TextView tvEditShielingTitle;
    private ImageView ivShieling;

    private String currentPhotoPath = BaseActivity.IMAGE_CHEESE_DEFAULT;
    private ShielingViewModel viewModel;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private MediaUtils mediaUtils;
    private Bitmap bitmap;

    private float latitude = 0.0f;
    private float longitude = 0.0f;

    String shielingId;

    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_shieling, frameLayout);
        mediaUtils = new MediaUtils(this);
        activity = this;

        navigationView.setCheckedItem(position);

        tvEditShielingTitle = findViewById(R.id.tvEditShielingTitle);
        etShielingName = findViewById(R.id.etShielingName);
        etShielingName.requestFocus();
        etShielingDescription = findViewById(R.id.etShielingDescription);
        Button btSaveShieling = findViewById(R.id.btSaveShieling);
        btSaveShieling.setOnClickListener(view -> {
            saveChanges(etShielingName.getText().toString(), etShielingDescription.getText().toString(), currentPhotoPath, latitude, longitude);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        shielingId = getIntent().getStringExtra("shielingId");
        Log.d(TAG, "shieling " + shielingId);
        if (shielingId == null || shielingId.isEmpty()) {
            setTitle(getString(R.string.empty));
            tvEditShielingTitle.setText(R.string.shieling_new_title);
            toastString = getString(R.string.shieling_new_created);
            isEditMode = false;
        } else {
            setTitle(getString(R.string.title_activity_edit_shieling));
            btSaveShieling.setText(R.string.update);
            toastString = getString(R.string.shieling_edit_edited);
            isEditMode = true;
        }

        ivShieling = findViewById(R.id.ivEditShielingPhoto);
        ivShieling.setOnClickListener(v -> mediaUtils.selectImage());

        ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                getApplication(), shielingId);
        viewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
        if (isEditMode) {
            viewModel.getShieling().observe(this, shielingEntity -> {
                if (shielingEntity != null) {
                    shieling = shielingEntity;
                    etShielingName.setText(shieling.getName());
                    etShielingDescription.setText(shieling.getDescription());
                    ivShieling.setOnLongClickListener(v -> removePicture());
                    if (!TextUtils.isEmpty(shieling.getImagepath())) {
                        if (!shieling.getImagepath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                            if (BaseApp.CLOUD_ACTIVE) {
                                if (bitmap == null) {
                                    mediaUtils.getFromFirebase(MediaUtils.TARGET_SHIELINGS, shieling.getImagepath(), getApplicationContext(), ivShieling);
                                    currentPhotoPath = shieling.getImagepath();
                                } else {
                                    ivShieling.setImageBitmap(bitmap);
                                }
                            } else {
                                bitmap = BitmapFactory.decodeFile(shieling.getImagepath());
                                bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                                ivShieling.setImageBitmap(bitmap);
                                ivShieling.setTag(shieling.getImagepath());
                            }
                        }
                    } else if (bitmap != null) {
                        ivShieling.setImageBitmap(bitmap);
                    }
                }
            });
        }

        CustomSupportMapFragment mapFragment = (CustomSupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fcvEditShielingMap);
        NestedScrollView nsvEditShieling = findViewById(R.id.nsvEditShieling);
        mapFragment.setListener(() -> nsvEditShieling.requestDisallowInterceptTouchEvent(true));

        mapFragment.getMapAsync(this);
    }


    private boolean removePicture() {
        currentPhotoPath = "";
        ivShieling.setImageResource(R.drawable.placeholder_shieling);
        shieling.setImagepath(BaseActivity.IMAGE_CHEESE_DEFAULT);
        Toast.makeText(this, getString(R.string.cheese_picture_removed), Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                File imageFile = mediaUtils.getImageFile();
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                currentPhotoPath = "";
                ivShieling.setImageResource(R.drawable.placeholder_shieling);
                shieling.setImagepath(BaseActivity.IMAGE_CHEESE_DEFAULT);
                currentPhotoPath = imageFile.getAbsolutePath();
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Log.d(TAG, "Pick from Gallery");

                    File f = mediaUtils.copyToLocalStorage(bitmap);
                    currentPhotoPath = "";
                    ivShieling.setImageResource(R.drawable.placeholder_shieling);
                    currentPhotoPath = f.getAbsolutePath();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "imagepath : " + shieling.getImagepath());
        }
    }

    @Override
    protected void onResume() {
        if (!settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false)) {
            finish();
        }
        super.onResume();
    }

    private void saveChanges(String shielingName, String description, String imagePath, float latitude, float longitude) {
        // si on a pas bougé le marqueur, on va considérer qu'il n'y a pas de localisation => on set à 0 / 0
        if (latitude == BaseApp.NO_LOCATION.latitude && longitude == BaseApp.NO_LOCATION.longitude) {
            latitude = 0.0f;
            longitude = 0.0f;
        }
        if(!shielingName.isEmpty()){
            if (isEditMode) {
                shieling.setOldName(shieling.getName());
                shieling.setName(shielingName);
                shieling.setId(shieling.getName());
                shieling.setDescription(description);
                shieling.setLatitude(latitude);
                shieling.setLongitude(longitude);
                if (BaseApp.CLOUD_ACTIVE) {
                    try {
                        if (!TextUtils.isEmpty(imagePath)) {
                            Log.d(TAG, "shieling getImagePath not empty");
                            if (!imagePath.equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                                if (bitmap != null) {
                                    Log.d(TAG, "shieling getImagePath not equals to default, updating");
                                    shieling.setImagepath(mediaUtils.saveToFirebase(MediaUtils.TARGET_SHIELINGS, bitmap));
                                } else {
                                    Log.d(TAG, "shieling getImagePath not equals to default, but has not changed.");
                                }
                            } else {
                                shieling.setImagepath(null);
                                Log.d(TAG, "shieling getImagePath equals to default");
                            }
                        } else {
                            shieling.setImagepath(null);
                            Log.d(TAG, "shieling getImagePath empty");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    shieling.setImagepath(imagePath);
                }
                viewModel.updateShieling(shieling, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "updateShieling: success");
                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "updateShieling: failure", e);
                        Toast.makeText(getApplicationContext(), getString(R.string.shieling_edit_name_duplicate), Toast.LENGTH_LONG).show();
                        etShielingName.requestFocus();
                    }
                });
            } else {
                ShielingEntity newShieling = new ShielingEntity();
                newShieling.setName(shielingName);
                newShieling.setDescription(description);
                newShieling.setLatitude(latitude);
                newShieling.setLongitude(longitude);
                if (BaseApp.CLOUD_ACTIVE) {
                    try {
                        if (!TextUtils.isEmpty(imagePath)) {
                            Log.d(TAG, "shieling getImagePath not empty");
                            if (!imagePath.equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                                if (bitmap != null) {
                                    Log.d(TAG, "shieling getImagePath not equals to default, updating");
                                    newShieling.setImagepath(mediaUtils.saveToFirebase(MediaUtils.TARGET_SHIELINGS, bitmap));
                                } else {
                                    Log.d(TAG, "shieling getImagePath not equals to default, but has not changed.");
                                }
                            } else {
                                Log.d(TAG, "shieling getImagePath equals to default");
                            }
                        } else {
                            Log.d(TAG, "shieling getImagePath empty");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    newShieling.setImagepath(imagePath);
                }
                viewModel.createShieling(newShieling, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "createShieling: success");
                        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "createShieling: failure", e);
                        Toast.makeText(getApplicationContext(), getString(R.string.shieling_edit_name_duplicate), Toast.LENGTH_LONG).show();
                        etShielingName.requestFocus();
                    }
                });
            }
        }else{
            Toast.makeText(this, getString(R.string.shieling_edit_name_empty), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        viewModel.getShieling().observe(this, shielingEntity -> {
            LatLng loc = BaseApp.NO_LOCATION;
            String title = getString(R.string.shieling_new_title);
            if (shielingEntity != null) {
                if (shielingEntity.getLatitude() != 0.0f && shielingEntity.getLongitude() != 0.0f) {
                    loc = new LatLng(shielingEntity.getLatitude(), shielingEntity.getLongitude());
                    title = shielingEntity.getName();
                    latitude = (float) loc.latitude;
                    longitude = (float) loc.longitude;
                }
            }
            Log.d(TAG, "loc " + loc.latitude + " // " + loc.longitude);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(loc).title(title).draggable(true));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 9.0f));
            googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Log.d(TAG, "Marker dropped at " + marker.getPosition().latitude + "/" + marker.getPosition().longitude);
                    latitude = (float) marker.getPosition().latitude;
                    longitude = (float) marker.getPosition().longitude;
                }
            });
        });
    }
}
