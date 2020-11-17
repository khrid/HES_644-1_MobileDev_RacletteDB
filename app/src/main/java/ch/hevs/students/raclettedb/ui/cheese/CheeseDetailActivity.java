package ch.hevs.students.raclettedb.ui.cheese;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProviders;
import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.LocaleUtils;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class CheeseDetailActivity extends BaseActivity {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-CheeseDetailActivity";

    private static final int EDIT_CHEESE = 1;

    private CheeseEntity cheese;
    private TextView tvCheeseName;
    private TextView tvCheeseShieling;
    private TextView tvCheeseType;
    private TextView tvCheeseDescription;
    private ImageView ivCheesePhoto;


    private CheeseViewModel cheeseViewModel;
    private ShielingViewModel shielingViewModel;

    private boolean isAdmin = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private MediaUtils mediaUtils = new MediaUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cheese, frameLayout);

        navigationView.setCheckedItem(position);

        Long cheeseId = getIntent().getLongExtra("cheeseId", 0L);

        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        initiateView();

        CheeseViewModel.Factory factory = new CheeseViewModel.Factory(
                getApplication(), cheeseId);
        cheeseViewModel = ViewModelProviders.of(this, factory).get(CheeseViewModel.class);
        cheeseViewModel.getCheese().observe(this, cheeseEntity -> {
            if (cheeseEntity != null) {
                cheese = cheeseEntity;
                updateContent();
            }
        });
    }

    @Override
    protected void onResume() {

        Log.d(TAG, "Current locale : "+settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT));
        Log.d(TAG, settings.toString());
        if(settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false)) {
            LocaleUtils.changeLocale(settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT), this);
            editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false);
            editor.apply();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isAdmin) {
            menu.add(0, EDIT_CHEESE, Menu.NONE, getString(R.string.title_activity_edit_cheese))
                    .setIcon(R.drawable.ic_edit_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            super.onCreateOptionsMenu(menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == EDIT_CHEESE) {
            Intent intent = new Intent(this, EditCheeseActivity.class);
            intent.putExtra("cheeseId", cheese.getId());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateView() {
        tvCheeseName = findViewById(R.id.tvCheeseName);
        tvCheeseShieling = findViewById(R.id.tvCheeseShieling);
        tvCheeseType = findViewById(R.id.tvCheeseType);
        tvCheeseDescription = findViewById(R.id.tvCheeseDescription);
        ivCheesePhoto = findViewById(R.id.ivCheesePhoto);
    }

    private void updateContent() {
        if (cheese != null) {
            setTitle(R.string.empty);
            tvCheeseName.setText(cheese.getName());
            tvCheeseType.setText(cheese.getType());
            tvCheeseDescription.setText(cheese.getDescription());

            ivCheesePhoto.setImageResource(R.drawable.placeholder_cheese);
            if(!TextUtils.isEmpty(cheese.getImagePath())) {
                ivCheesePhoto.setVisibility(View.VISIBLE);
                if(!cheese.getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                    if(BaseApp.CLOUD_ACTIVE) {
                        mediaUtils.getFromFirebase(MediaUtils.TARGET_CHEESES, cheese.getImagePath(), getApplicationContext(), ivCheesePhoto);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeFile(cheese.getImagePath());
                        bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                        ivCheesePhoto.setImageBitmap(bitmap);
                    }
                } else {
                    ivCheesePhoto.setVisibility(View.GONE);
                }
            } else {
                ivCheesePhoto.setVisibility(View.GONE);
            }

            ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                    getApplication(), cheese.getShieling());
            shielingViewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
            shielingViewModel.getShieling().observe(this, shielingEntity -> {
                if (shielingEntity != null) {
                    tvCheeseShieling.setText(shielingEntity.getName());
                }
            });

            Log.i(TAG, "Activity populated.");
        }
    }

}
