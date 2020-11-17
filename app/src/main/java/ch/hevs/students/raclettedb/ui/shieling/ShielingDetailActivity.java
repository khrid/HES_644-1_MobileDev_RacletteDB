package ch.hevs.students.raclettedb.ui.shieling;

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

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.CustomSupportMapFragment;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class ShielingDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-" + "ShielingDetailActivity";

    private static final int EDIT_SHIELING = 1;

    private ShielingEntity shieling;
    private TextView tvShielingName;
    private TextView tvShielingDescription;
    private ImageView ivShielingPhoto;

    private ShielingViewModel viewModel;
    Long shielingId;

    private boolean isAdmin = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private MediaUtils mediaUtils = new MediaUtils(this);

    public ShielingDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);


        getLayoutInflater().inflate(R.layout.activity_shieling, frameLayout);
        shielingId = getIntent().getLongExtra("shielingId", 0L);
        ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                getApplication(), shielingId);
        viewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
        viewModel.getShieling().observe(this, shielingEntity -> {
            if (shielingEntity != null) {
                shieling = shielingEntity;
                updateContent();
            }
        });


        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        initiateView();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin) {
            menu.add(0, EDIT_SHIELING, Menu.NONE, getString(R.string.title_activity_edit_shieling))
                    .setIcon(R.drawable.ic_edit_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            super.onCreateOptionsMenu(menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == EDIT_SHIELING) {

            Intent intent = new Intent(this, EditShielingActivity.class);
            intent.putExtra("shielingId", shieling.getId());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateView() {
        tvShielingName = findViewById(R.id.tvShielingName);
        tvShielingDescription = findViewById(R.id.tvShielingDescription);
        ivShielingPhoto = findViewById(R.id.ivShielingPhoto);
    }

    private void updateContent() {
        if (shieling != null) {
            setTitle(R.string.empty);
            tvShielingName.setText(shieling.getName());
            tvShielingDescription.setText(shieling.getDescription());

            ivShielingPhoto.setImageResource(R.drawable.placeholder_shieling);
            if (!TextUtils.isEmpty(shieling.getImagePath())) {
                ivShielingPhoto.setVisibility(View.VISIBLE);
                if (!shieling.getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                    if (BaseApp.CLOUD_ACTIVE) {
                        mediaUtils.getFromFirebase(MediaUtils.TARGET_SHIELINGS, shieling.getImagePath(), getApplicationContext(), ivShielingPhoto);
                    } else {
                        Bitmap bitmap = BitmapFactory.decodeFile(shieling.getImagePath());
                        bitmap = mediaUtils.getResizedBitmap(bitmap, 500);
                        ivShielingPhoto.setImageBitmap(bitmap);
                    }
                } else {
                    ivShielingPhoto.setVisibility(View.GONE);
                }
            } else {
                ivShielingPhoto.setVisibility(View.GONE);
            }

            FragmentContainerView fcvMap = findViewById(R.id.fcvShielingMap);
            TextView tvShielingLocation = findViewById(R.id.tvShielingLocation);
            if (shieling.getLatitude() != 0.0f && shieling.getLongitude() != 0.0f) {
                CustomSupportMapFragment mapFragment = (CustomSupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fcvShielingMap);
                NestedScrollView nsvEditShieling = findViewById(R.id.nsvShielingDetail);
                mapFragment.setListener(() -> nsvEditShieling.requestDisallowInterceptTouchEvent(true));
                mapFragment.getMapAsync(this);
                navigationView.setCheckedItem(position);
                fcvMap.setVisibility(View.VISIBLE);
                tvShielingLocation.setVisibility(View.VISIBLE);
            } else {
                fcvMap.setVisibility(View.GONE);
                tvShielingLocation.setVisibility(View.GONE);
            }

            Log.i(TAG, "Activity populated.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        viewModel.getShieling().observe(this, shielingEntity -> {
            if (shielingEntity != null) {
                if (shielingEntity.getLatitude() != 0.0f && shielingEntity.getLongitude() != 0.0f) {
                    LatLng loc = new LatLng(shielingEntity.getLatitude(), shielingEntity.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(loc).title(shielingEntity.getName()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 9.0f));
                }
            }
        });
    }
}
