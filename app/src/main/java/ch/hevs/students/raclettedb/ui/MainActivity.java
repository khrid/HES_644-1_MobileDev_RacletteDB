package ch.hevs.students.raclettedb.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.AppDatabase;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;

import static ch.hevs.students.raclettedb.database.AppDatabase.initializeDemoData;

public class MainActivity extends BaseActivity {

    private int isAdmin = 0;
    private List<CheeseEntity> cheeses;
    private CheeseRepository cheeseRepository;
    private List<ShielingEntity> shielings;
    private ShielingRepository shielingRepository;
    private TextView tv_main_favorites_1;
    private TextView tv_main_favorites_2;
    private TextView tv_main_favorites_3;
    private TextView tv_main_shieling_name;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initiateView();
        cheeseRepository = ((BaseApp) getApplication()).getCheeseRepository();
        cheeseRepository.getAllCheeses(getApplication()).observe(MainActivity.this, cheeseEntities -> {
            cheeses = cheeseEntities;
            tv_main_favorites_1.setText(cheeses.get(0).getName());
            tv_main_favorites_2.setText(cheeses.get(1).getName());
            tv_main_favorites_3.setText(cheeses.get(2).getName());
        });

        shielingRepository = ((BaseApp) getApplication()).getShielingRepository();
        shielingRepository.getAllShielings(getApplication()).observe(MainActivity.this, shielingEntities -> {
            shielings = shielingEntities;
            tv_main_shieling_name.setText(shielings.get(0).getName());
        });

        SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        SharedPreferences settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);
        if(isAdmin == 1) {
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
        tv_main_favorites_1 = findViewById(R.id.tv_main_favorites_1);
        tv_main_favorites_2 = findViewById(R.id.tv_main_favorites_2);
        tv_main_favorites_3 = findViewById(R.id.tv_main_favorites_3);
        tv_main_shieling_name = findViewById(R.id.tv_main_shieling_name);

    }

    private void updateContent() {
        if (cheeses != null && cheeses.size() > 0) {
            tv_main_favorites_1.setText(cheeses.get(0).getName());
            tv_main_favorites_2.setText(cheeses.get(1).getName());
            tv_main_favorites_3.setText(cheeses.get(2).getName());
            //tv_main_shieling_name.setText(shielings.get(0).getName());
            // TODO Réactiver ça quand initialisation base OK
        }
    }
}
