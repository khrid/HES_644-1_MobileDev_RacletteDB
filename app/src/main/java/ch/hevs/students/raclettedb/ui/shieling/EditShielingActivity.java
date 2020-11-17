package ch.hevs.students.raclettedb.ui.shieling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+"EditShielingActivity";

    private ShielingEntity shieling = new ShielingEntity();
    private boolean isEditMode;
    private Toast toast;
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
    private float longitude  = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_shieling, frameLayout);
        mediaUtils = new MediaUtils(this);

        navigationView.setCheckedItem(position);

        tvEditShielingTitle = findViewById(R.id.tvEditShielingTitle);
        etShielingName = findViewById(R.id.etShielingName);
        etShielingName.requestFocus();
        etShielingDescription = findViewById(R.id.etShielingDescription);
        Button btSaveShieling = findViewById(R.id.btSaveShieling);
        btSaveShieling.setOnClickListener(view -> {
            if(!etShielingName.getText().toString().isEmpty()) {
                saveChanges(etShielingName.getText().toString(), etShielingDescription.getText().toString(), shieling.getImagePath(), latitude, longitude);
                onBackPressed();
                toast = Toast.makeText(this, toastString, Toast.LENGTH_LONG);
            }else{
                toast = Toast.makeText(this, getString(R.string.shieling_edit_name_empty), Toast.LENGTH_LONG);
                etShielingName.requestFocus();
            }

            toast.show();
        });

        Long shielingId = getIntent().getLongExtra("shielingId", 0L);
        if (shielingId == 0L) {
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
        ivShieling.setOnClickListener(v ->  mediaUtils.selectImage());

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
                    if(!TextUtils.isEmpty(shieling.getImagePath())) {
                        if(!shieling.getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                            if(BaseApp.CLOUD_ACTIVE) {
                                mediaUtils.getFromFirebase(MediaUtils.TARGET_SHIELINGS, shieling.getImagePath(), getApplicationContext(), ivShieling);
                            } else {
                                bitmap = BitmapFactory.decodeFile(shieling.getImagePath());
                                bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                                ivShieling.setImageBitmap(bitmap);
                                ivShieling.setTag(shieling.getImagePath());
                            }
                        }
                    }



                    /*FragmentContainerView fcvMap = findViewById(R.id.fcvEditShielingMap);
                    TextView tvShielingLocation = findViewById(R.id.tvShielingLocation);
                    if (shieling.getLatitude() != 0.0f && shieling.getLongitude() != 0.0f) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.fcvShielingMap);
                        mapFragment.getMapAsync(this);
                        navigationView.setCheckedItem(position);
                        fcvMap.setVisibility(View.VISIBLE);
                        tvShielingLocation.setVisibility(View.VISIBLE);
                    } else {
                        fcvMap.setVisibility(View.GONE);
                        tvShielingLocation.setVisibility(View.GONE);
                    }*/
                }
            });
        }

        CustomSupportMapFragment mapFragment = (CustomSupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fcvEditShielingMap);
        NestedScrollView nsvEditShieling = findViewById(R.id.nsvEditShieling);
        mapFragment.setListener(() -> nsvEditShieling.requestDisallowInterceptTouchEvent(true));

        mapFragment.getMapAsync(this);
        navigationView.setCheckedItem(position);

    }

    private boolean removePicture() {
        currentPhotoPath = "";
        ivShieling.setImageResource(R.drawable.placeholder_shieling);
        shieling.setImagePath(BaseActivity.IMAGE_CHEESE_DEFAULT);
        Toast.makeText(this, getString(R.string.cheese_picture_removed), Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {

                File imageFile = mediaUtils.getImageFile();
                bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                currentPhotoPath = imageFile.getAbsolutePath();
                shieling.setImagePath(imageFile.getAbsolutePath());
                ivShieling.setTag(currentPhotoPath);
                ivShieling.setImageBitmap(bitmap);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Log.e(TAG, "Pick from Gallery");

                    File f = mediaUtils.copyToLocalStorage(bitmap);

                    shieling.setImagePath(f.getAbsolutePath());
                    ivShieling.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        if (isEditMode) {
            if (!"".equals(shielingName)) {
                shieling.setName(shielingName);
                shieling.setDescription(description);
                shieling.setLatitude(latitude);
                shieling.setLongitude(longitude);
                if(BaseApp.CLOUD_ACTIVE) {
                    try {
                        shieling.setImagePath(mediaUtils.saveToFirebase(MediaUtils.TARGET_SHIELINGS, bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    shieling.setImagePath(imagePath);
                }
                viewModel.updateShieling(shieling, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "updateShieling: success");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "updateShieling: failure", e);
                    }
                });
            }
        } else {
            ShielingEntity newShieling = new ShielingEntity();
            newShieling.setName(shielingName);
            newShieling.setDescription(description);
            newShieling.setLatitude(latitude);
            newShieling.setLongitude(longitude);
            if(BaseApp.CLOUD_ACTIVE) {
                try {
                    newShieling.setImagePath(mediaUtils.saveToFirebase(MediaUtils.TARGET_SHIELINGS, bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                newShieling.setImagePath(imagePath);
            }
            viewModel.createShieling(newShieling, new OnAsyncEventListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "createShieling: success");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "createShieling: failure", e);
                }
            });
        }
        CustomSupportMapFragment mapFragment = (CustomSupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fcvEditShielingMap);
        NestedScrollView nsvEditShieling = findViewById(R.id.nsvEditShieling);
        mapFragment.setListener(() -> nsvEditShieling.requestDisallowInterceptTouchEvent(true));

        mapFragment.getMapAsync(this);
        navigationView.setCheckedItem(position);
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
                }
            }
            Marker marker = googleMap.addMarker(new MarkerOptions().position(loc).title(title).draggable(true));
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
                    Log.d(TAG, "Marker dropped at "+marker.getPosition().latitude+"/"+marker.getPosition().longitude);
                    latitude = (float) marker.getPosition().latitude;
                    longitude = (float) marker.getPosition().longitude;
                }
            });
        });
    }
}
