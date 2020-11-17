package ch.hevs.students.raclettedb.ui.cheese;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.adapter.ListAdapter;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingListViewModel;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class EditCheeseActivity extends BaseActivity {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-EditCheeseActivity";

    private Long cheeseId;
    private CheeseEntity cheese = new CheeseEntity();
    private boolean isEditMode;
    private Toast toast;
    private String toastString;
    private EditText etCheeseName;
    private EditText etCheeseDescription;
    private EditText etCheeseType;
    private TextView tvEditCheeseTitle;
    private Spinner spinCheeseShieling;
    private Button btSaveCheese;
    private ImageView ivCheese;
    private File destination = null;
    private String imgPath = null;

    private String currentPhotoPath = BaseActivity.IMAGE_CHEESE_DEFAULT;

    private CheeseViewModel cheeseViewModel;
    private ShielingViewModel shielingViewModel;
    private ShielingListViewModel shielingListViewModel;
    private ListAdapter<ShielingEntity> adapterShieling;


    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    MediaUtils mediaUtils;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_cheese, frameLayout);
        mediaUtils = new MediaUtils(this);

        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();

        navigationView.setCheckedItem(position);

        tvEditCheeseTitle = findViewById(R.id.tvEditCheeseTitle);
        etCheeseName = findViewById(R.id.etCheeseName);
        etCheeseName.requestFocus();
        etCheeseDescription = findViewById(R.id.etCheeseDescription);
        etCheeseType = findViewById(R.id.etCheeseType);

        btSaveCheese = findViewById(R.id.btSaveCheese);
        btSaveCheese.setOnClickListener(view -> {
            if(!etCheeseName.getText().toString().isEmpty()) {
                saveChanges(etCheeseName.getText().toString(), etCheeseDescription.getText().toString(), etCheeseType.getText().toString(), ((ShielingEntity) spinCheeseShieling.getSelectedItem()).getId(), cheese.getImagePath());
                onBackPressed();
                toast = Toast.makeText(this, toastString, Toast.LENGTH_LONG);
            }else{
                toast = Toast.makeText(this, getString(R.string.cheese_edit_name_empty), Toast.LENGTH_LONG);
                etCheeseName.requestFocus();
            }

            toast.show();
        });


        cheeseId = getIntent().getLongExtra("cheeseId", 0L);
        if (cheeseId == 0L) {
            setTitle(R.string.empty);
            tvEditCheeseTitle.setText(R.string.cheese_new_title);
            btSaveCheese.setText(R.string.save);
            toastString = getString(R.string.cheese_new_created);
            isEditMode = false;
        } else {
            setTitle(R.string.empty);
            btSaveCheese.setText(R.string.update);
            toastString = getString(R.string.cheese_edit_edited);
            isEditMode = true;
        }

        ivCheese = findViewById(R.id.ivEditCheesePhoto);
        ivCheese.setOnClickListener(v -> mediaUtils.selectImage());

        ivCheese.setImageResource(R.drawable.placeholder_cheese);

        setupShielingSpinner();
        setupViewModels();
    }

    private boolean removePicture() {
        currentPhotoPath = "";
        ivCheese.setImageResource(R.drawable.placeholder_cheese);
        cheese.setImagePath(BaseActivity.IMAGE_CHEESE_DEFAULT);
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
                currentPhotoPath = imageFile.getAbsolutePath();
                cheese.setImagePath(imageFile.getAbsolutePath());
                ivCheese.setTag(currentPhotoPath);
                ivCheese.setImageBitmap(bitmap);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Log.e(TAG, "Pick from Gallery::>>> ");

                    File f = mediaUtils.copyToLocalStorage(bitmap);

                    cheese.setImagePath(f.getAbsolutePath());
                    ivCheese.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupViewModels() {

        ShielingListViewModel.Factory shielingFactory = new ShielingListViewModel.Factory(
                getApplication());
        shielingListViewModel = ViewModelProviders.of(this, shielingFactory).get(ShielingListViewModel.class);
        shielingListViewModel.getShielings().observe(this, shielingEntities -> {
            if (shielingEntities != null) {

                updateShielingSpinner(shielingEntities);
            }
        });

        CheeseViewModel.Factory cheeseFactory = new CheeseViewModel.Factory(
                getApplication(), cheeseId);
        cheeseViewModel = ViewModelProviders.of(this, cheeseFactory).get(CheeseViewModel.class);
        if (isEditMode) {
            cheeseViewModel.getCheese().observe(this, cheeseEntity -> {
                if (cheeseEntity != null) {
                    cheese = cheeseEntity;
                    etCheeseName.setText(cheese.getName());
                    etCheeseDescription.setText(cheese.getDescription());
                    etCheeseType.setText(cheese.getType());

                    ivCheese.setOnLongClickListener(v -> removePicture());
                    if (!TextUtils.isEmpty(cheese.getImagePath())) {
                        if (!cheese.getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                            if(BaseApp.CLOUD_ACTIVE) {
                                mediaUtils.getFromFirebase(MediaUtils.TARGET_CHEESES, cheese.getImagePath(), getApplicationContext(), ivCheese);
                            } else {
                                bitmap = BitmapFactory.decodeFile(cheese.getImagePath());
                                bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                                ivCheese.setImageBitmap(bitmap);
                                ivCheese.setTag(cheese.getImagePath());
                            }
                        }
                    }

                    ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                            getApplication(), cheese.getShieling());
                    shielingViewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
                    shielingViewModel.getShieling().observe(this, shielingEntity -> {
                        if (shielingEntity != null) {
                            spinCheeseShieling.setSelection(adapterShieling.getPosition(shielingEntity));
                        }
                    });
                }
            });
        }
    }


    @Override
    protected void onResume() {
        if (!settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false)) {
            finish();
        }
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


    private void setupShielingSpinner() {
        spinCheeseShieling = findViewById(R.id.spinCheeseShieling);
        adapterShieling = new ListAdapter<>(this, R.layout.row_shieling, new ArrayList<>());
        spinCheeseShieling.setAdapter(adapterShieling);
    }

    private void updateShielingSpinner(List<ShielingEntity> shielings) {
        adapterShieling.updateData(new ArrayList<>(shielings));
    }


    private void saveChanges(String cheeseName, String description, String cheeseType, Long Shieling, String imagePath) {
        if (isEditMode) {
            if (!"".equals(cheeseName)) {
                cheese.setName(cheeseName);
                cheese.setDescription(description);
                cheese.setType(cheeseType);
                cheese.setShieling(Shieling);
                if(BaseApp.CLOUD_ACTIVE) {
                    try {
                        cheese.setImagePath(mediaUtils.saveToFirebase(MediaUtils.TARGET_CHEESES, bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    cheese.setImagePath(imagePath);
                }
                cheeseViewModel.updateCheese(cheese, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "updateCheese: success");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "updateCheese: failure", e);
                    }
                });
            }
        } else {
            CheeseEntity newCheese = new CheeseEntity();
            newCheese.setName(cheeseName);
            newCheese.setDescription(description);
            newCheese.setType(cheeseType);
            newCheese.setShieling(Shieling);
            if(BaseApp.CLOUD_ACTIVE) {
                try {
                    newCheese.setImagePath(mediaUtils.saveToFirebase(MediaUtils.TARGET_CHEESES, bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                newCheese.setImagePath(imagePath);
            }
            cheeseViewModel.createCheese(newCheese, new OnAsyncEventListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "createCheese: success");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "createCheese: failure", e);
                }
            });
        }
    }

}
