package ch.hevs.students.raclettedb.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.ui.cheese.CheesesActivity;
import ch.hevs.students.raclettedb.ui.mgmt.LoginActivity;
import ch.hevs.students.raclettedb.ui.mgmt.SettingsActivity;
import ch.hevs.students.raclettedb.ui.shieling.ShielingsActivity;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREFS_IS_ADMIN = "IsAdmin";
    public static final String PREFS_APP_LANGUAGE = "AppLanguage";
    public static final String PREFS_APP_LANGUAGE_DEFAULT = "system";
    public static final String PREFS_APP_LANGUAGE_CHANGED = "AppLanguageChanged";
    public static final String IMAGE_CHEESE_DEFAULT = "placeholder";

    private static final String TAG = "TAG-"+BaseApp.APP_NAME+"-"+BaseActivity.class.getSimpleName();

    protected FrameLayout frameLayout;

    protected DrawerLayout drawerLayout;

    public NavigationView navigationView;


    protected static int position;

    private boolean isAdmin = false;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.flContent);

        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        navigationView = findViewById(R.id.base_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.base_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
                Log.d(TAG, "onDrawerStateChanged / " + isAdmin);
                if (isAdmin) {
                    navigationView.getMenu().findItem(R.id.nav_admin).setTitle(R.string.drawer_admin_exit);
                    navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_exit_to_app_black_24dp);
                } else {
                    navigationView.getMenu().findItem(R.id.nav_admin).setTitle(R.string.drawer_admin_enter);
                    navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_admin_panel_settings_black_24dp);
                }
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        BaseActivity.position = 0;
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == BaseActivity.position) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        BaseActivity.position = id;
        Intent intent = null;

        navigationView.setCheckedItem(id);

        if (id == R.id.nav_admin) {
            position = -1;
            isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
            Log.d(TAG, "onNavigationItemSelected / " + isAdmin);
            if (isAdmin) {
                intent = null;
                navigationView.getMenu().findItem(R.id.nav_admin).setTitle(R.string.drawer_admin_enter);
                navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_admin_panel_settings_black_24dp);
                navigationView.getMenu().findItem(R.id.nav_admin).setChecked(false);
                SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(BaseActivity.PREFS_IS_ADMIN, false);
                editor.apply();
                recreate();
                navigationView.setCheckedItem(id);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
        } else if (id == R.id.nav_cheeses) {
            intent = new Intent(this, CheesesActivity.class);
        } else if (id == R.id.nav_shielings) {
            intent = new Intent(this, ShielingsActivity.class);
        }

        if (intent != null) {
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
            );
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void close() {
        SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, 0).edit();
        editor.remove(BaseActivity.PREFS_IS_ADMIN);
        editor.apply();
        this.finishAffinity();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(BaseActivity.PREFS_NAME, MODE_PRIVATE);
        String localeString = prefs.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT);
        Locale myLocale = new Locale(localeString);
        Locale.setDefault(myLocale);
        Configuration config = newBase.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(myLocale);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                Context newContext = newBase.createConfigurationContext(config);
                super.attachBaseContext(newContext);
                return;
            }
        } else {
            config.locale = myLocale;
        }
        super.attachBaseContext(newBase);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


}
