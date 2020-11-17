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
import androidx.lifecycle.ViewModelProviders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class EditShielingActivity extends BaseActivity {

    private static final String TAG = "EditShielingActivity";

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
                saveChanges(etShielingName.getText().toString(), etShielingDescription.getText().toString(), shieling.getImagePath());
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
                            ivShieling.setOnLongClickListener(v -> removePicture());
                        }
                    }
                }
            });
        }
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

    private void saveChanges(String shielingName, String description, String imagePath) {
        if (isEditMode) {
            if (!"".equals(shielingName)) {
                shieling.setName(shielingName);
                shieling.setDescription(description);
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
    }
}
