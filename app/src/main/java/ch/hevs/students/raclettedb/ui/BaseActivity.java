package ch.hevs.students.raclettedb.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.ui.cheese.CheesesActivity;
import ch.hevs.students.raclettedb.ui.mgmt.LoginActivity;
import ch.hevs.students.raclettedb.ui.mgmt.SettingsActivity;
import ch.hevs.students.raclettedb.ui.shieling.ShielingsActivity;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREFS_USER = "LoggedIn";
    public static final String PREFS_IS_ADMIN = "IsAdmin";

    private final int isAdminDefaultValue = -1;

    SharedPreferences sharedPreferences;
    /**
     * Frame layout: Which is going to be used as parent layout for child activity layout.
     * This layout is protected so that child activity can access this
     */
    protected FrameLayout frameLayout;

    protected DrawerLayout drawerLayout;

    public NavigationView navigationView;

    /**
     * Static variable for selected item position. Which can be used in child activity to know which item is selected from the list.
     */
    protected static int position;

    private boolean isAdmin = false;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // reset des SharedPreferences
        //getSharedPreferences(BaseActivity.PREFS_NAME, 0).edit().clear().apply();

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
                //isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);
                isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
                Log.d("TAG", "onDrawerStateChanged / " + isAdmin);
                if (isAdmin) {
                    navigationView.getMenu().findItem(R.id.nav_admin).setTitle("Leave admin mode");
                    navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_exit_to_app_black_24dp);
                } else {
                    navigationView.getMenu().findItem(R.id.nav_admin).setTitle(R.string.action_admin);
                    navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_admin_panel_settings_black_24dp);
                }
            }
        };

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void onResume() {
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        Log.d("TAG", "onResume");
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
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
            //isAdmin = settings.getInt(BaseActivity.PREFS_IS_ADMIN, 0);
            isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
            Log.d("TAG", "onNavigationItemSelected / " + isAdmin);
            if (isAdmin) {
                intent = null;
                navigationView.getMenu().findItem(R.id.nav_admin).setTitle(R.string.action_admin);
                navigationView.getMenu().findItem(R.id.nav_admin).setIcon(R.drawable.ic_admin_panel_settings_black_24dp);
                navigationView.getMenu().findItem(R.id.nav_admin).setChecked(false);
                SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(BaseActivity.PREFS_IS_ADMIN, 0);
                editor.putBoolean(BaseActivity.PREFS_IS_ADMIN, false);
                editor.apply();
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

    public void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, 0).edit();
        editor.remove(BaseActivity.PREFS_USER);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void close() {
        SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, 0).edit();
        editor.remove(BaseActivity.PREFS_IS_ADMIN);
        editor.apply();
        this.finishAffinity();
    }
}
