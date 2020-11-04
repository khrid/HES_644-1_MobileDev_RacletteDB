package ch.hevs.students.raclettedb.ui.cheese;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel;

public class CheeseDetailActivity extends BaseActivity {

    private static final String TAG = "CheeseDetailActivity";

    private static final int EDIT_CHEESE = 1;

    private CheeseEntity cheese;
    private TextView tvCheeseName;
    private TextView tvCheeseType;
    private TextView tvCheeseDescription;


    private CheeseViewModel viewModel;

    private boolean isAdmin = false;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_cheese, frameLayout);

        navigationView.setCheckedItem(position);

        Long cheeseId = getIntent().getLongExtra("cheeseId", 0L);

        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        initiateView();

        CheeseViewModel.Factory factory = new CheeseViewModel.Factory(
                getApplication(), cheeseId);
        viewModel = ViewModelProviders.of(this, factory).get(CheeseViewModel.class);
        viewModel.getCheese().observe(this, cheeseEntity -> {
            if (cheeseEntity != null) {
                cheese = cheeseEntity;
                updateContent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(isAdmin) {
            menu.add(0, EDIT_CHEESE, Menu.NONE, getString(R.string.title_activity_edit_cheese))
                    .setIcon(R.drawable.ic_edit_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
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
        tvCheeseType = findViewById(R.id.tvCheeseType);
        tvCheeseDescription = findViewById(R.id.tvCheeseDescription);
    }

    private void updateContent() {
        if (cheese != null) {
            setTitle(R.string.empty);
            tvCheeseName.setText(cheese.getName());
            tvCheeseType.setText(cheese.getType());
            tvCheeseDescription.setText(cheese.getDescription());
            Log.i(TAG, "Activity populated.");
        }
    }

}
