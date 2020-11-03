package ch.hevs.students.raclettedb.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseListViewModel;

public class MainActivity extends BaseActivity {

    private CheeseListViewModel viewModel;
    private List<CheeseEntity> cheeses;
    private CheeseRepository cheeseRepository;
    private TextView tv_main_favorites_1;
    private TextView tv_main_favorites_2;
    private TextView tv_main_favorites_3;

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
            updateContent();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
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
    }

    private void updateContent() {
        if (cheeses != null) {
            tv_main_favorites_1.setText(cheeses.get(0).getName());
            tv_main_favorites_2.setText(cheeses.get(1).getName());
            tv_main_favorites_3.setText(cheeses.get(2).getName());
        }
    }
}
