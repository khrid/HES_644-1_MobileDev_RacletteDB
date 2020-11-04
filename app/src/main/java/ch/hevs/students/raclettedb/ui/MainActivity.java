package ch.hevs.students.raclettedb.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;
import ch.hevs.students.raclettedb.ui.cheese.CheeseDetailActivity;
import ch.hevs.students.raclettedb.ui.shieling.ShielingDetailActivity;

public class MainActivity extends BaseActivity {

    private boolean isAdmin = false;
    private List<CheeseEntity> cheeses;
    private CheeseRepository cheeseRepository;
    private List<ShielingEntity> shielings;
    private ShielingRepository shielingRepository;
    private TextView tvMainFavorites1;
    private ImageView ivMainFavorites1;
    private TextView tvMainFavorites2;
    private ImageView ivMainFavorites2;
    private TextView tvMainFavorites3;
    private ImageView ivMainFavorites3;
    private TextView tvMainShielingName;
    private ImageView ivMainShieling;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        //initializeDemoData(AppDatabase.getInstance(this)); // INITIALISE LA BASE A CHAQUE DEMARRAGE

        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initiateView();
        cheeseRepository = ((BaseApp) getApplication()).getCheeseRepository();
        cheeseRepository.getAllCheeses(getApplication()).observe(MainActivity.this, cheeseEntities -> {
            //cheeses = cheeseEntities;
            tvMainFavorites1.setText(cheeseEntities.get(0).getName());
            tvMainFavorites2.setText(cheeseEntities.get(1).getName());
            tvMainFavorites3.setText(cheeseEntities.get(2).getName());

            tvMainFavorites1.setOnClickListener(v -> showCheese(cheeseEntities.get(0).getId()));
            ivMainFavorites1.setOnClickListener(v -> showCheese(cheeseEntities.get(0).getId()));
            tvMainFavorites2.setOnClickListener(v -> showCheese(cheeseEntities.get(1).getId()));
            ivMainFavorites2.setOnClickListener(v -> showCheese(cheeseEntities.get(1).getId()));
            tvMainFavorites1.setOnClickListener(v -> showCheese(cheeseEntities.get(2).getId()));
            ivMainFavorites3.setOnClickListener(v -> showCheese(cheeseEntities.get(2).getId()));
        });

        shielingRepository = ((BaseApp) getApplication()).getShielingRepository();
        shielingRepository.getAllShielings(getApplication()).observe(MainActivity.this, shielingEntities -> {
            //shielings = shielingEntities;
            tvMainShielingName.setText(shielingEntities.get(0).getName());
            tvMainShielingName.setOnClickListener(v -> showShieling(shielingEntities.get(0).getId()));
            ivMainShieling.setOnClickListener(v -> showShieling(shielingEntities.get(0).getId()));
        });

        SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        //isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        //isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
        Log.d("TAG", isAdmin+"");
        if(isAdmin) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainlayout), "Admin mode is active", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.action_close));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getString(R.string.close_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_close), (dialog, which) -> close());
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();

    }

    private void initiateView() {
        tvMainFavorites1 = findViewById(R.id.tvMainFavorites1);
        tvMainFavorites2 = findViewById(R.id.tvMainFavorites2);
        tvMainFavorites3 = findViewById(R.id.tvMainFavorites3);
        ivMainFavorites1 = findViewById(R.id.ivMainFavorites1);
        ivMainFavorites2 = findViewById(R.id.ivMainFavorites2);
        ivMainFavorites3 = findViewById(R.id.ivMainFavorites3);
        tvMainShielingName = findViewById(R.id.tvMainShielingName);
        ivMainShieling = findViewById(R.id.ivMainShieling);

    }

    private void updateContent() {
        if (cheeses != null && cheeses.size() > 0) {
            tvMainFavorites1.setText(cheeses.get(0).getName());
            tvMainFavorites2.setText(cheeses.get(1).getName());
            tvMainFavorites3.setText(cheeses.get(2).getName());
            //tv_main_shieling_name.setText(shielings.get(0).getName());
            // TODO Réactiver ça quand initialisation base OK
        }
    }

    public void showCheese(Long cheeseId) {
        Intent intent = new Intent(MainActivity.this, CheeseDetailActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY
        );
        intent.putExtra("cheeseId", cheeseId);
        startActivity(intent);
    }

    public void showShieling(Long shielingId) {
        Intent intent = new Intent(MainActivity.this, ShielingDetailActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY
        );
        intent.putExtra("shielingId", shielingId);
        startActivity(intent);
    }
}
