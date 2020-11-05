package ch.hevs.students.raclettedb.ui.shieling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class ShielingDetailActivity extends BaseActivity {

    private static final String TAG = "ShielingDetailActivity";

    private static final int EDIT_SHIELING = 1;

    private ShielingEntity shieling;
    private TextView tvShielingName;
    private TextView tvShielingDescription;

    private ShielingViewModel viewModel;

    private boolean isAdmin = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_shieling, frameLayout);

        navigationView.setCheckedItem(position);

        Long shielingId = getIntent().getLongExtra("shielingId", 0L);

        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        initiateView();

        ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                getApplication(), shielingId);
        viewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
        viewModel.getShieling().observe(this, shielingEntity -> {
            if (shielingEntity != null) {
                shieling = shielingEntity;
                updateContent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isAdmin) {
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
        MenuItem item= menu.findItem(R.id.action_settings);
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
    }

    private void updateContent() {
        if (shieling != null) {
            setTitle(R.string.empty);
            tvShielingName.setText(shieling.getName());
            tvShielingDescription.setText(shieling.getDescription());
            Log.i(TAG, "Activity populated.");
        }
    }

}
