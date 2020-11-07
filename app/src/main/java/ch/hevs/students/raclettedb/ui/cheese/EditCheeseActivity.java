package ch.hevs.students.raclettedb.ui.cheese;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.adapter.ListAdapter;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingListViewModel;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class EditCheeseActivity extends BaseActivity {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-EditCheeseActivity";

    private Long cheeseId;
    private CheeseEntity cheese;
    private boolean isEditMode;
    private Toast toast;
    private EditText etCheeseName;
    private EditText etCheeseDescription;
    private EditText etCheeseType;
    private TextView tvEditCheeseTitle;
    private Spinner spinCheeseShieling;
    private Button btSaveCheese;

    private CheeseViewModel cheeseViewModel;
    private ShielingListViewModel shielingViewModel;
    private ListAdapter<ShielingEntity> adapterShieling;


    static SharedPreferences settings;
    static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_cheese, frameLayout);

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
            saveChanges(etCheeseName.getText().toString(),etCheeseDescription.getText().toString(), etCheeseType.getText().toString());
            onBackPressed();
            toast.show();
        });

        cheeseId = getIntent().getLongExtra("cheeseId", 0L);
        if (cheeseId == 0L) {
            setTitle(R.string.empty);
            tvEditCheeseTitle.setText(R.string.cheese_new_title);
            btSaveCheese.setText(R.string.save);
            toast = Toast.makeText(this, getString(R.string.cheese_new_created), Toast.LENGTH_LONG);
            isEditMode = false;
        } else {
            setTitle(R.string.empty);
            btSaveCheese.setText(R.string.update);
            toast = Toast.makeText(this, getString(R.string.cheese_edit_edited), Toast.LENGTH_LONG);
            isEditMode = true;
        }

        setupShielingSpinner();
        setupViewModels();
    }

    private void setupViewModels() {

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
                }
            });
        }

        ShielingListViewModel.Factory shielingFactory = new ShielingListViewModel.Factory(
                getApplication());
        shielingViewModel = ViewModelProviders.of(this, shielingFactory).get(ShielingListViewModel.class);
        shielingViewModel.getShielings().observe(this, shielingEntities -> {
            if (shielingEntities != null) {
                updateShielingSpinner(shielingEntities);
            }
        });

    }


    @Override
    protected void onResume() {
        if(!settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false)) {
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


    private void saveChanges(String cheeseName, String description, String cheeseType) {
        if (isEditMode) {
            if(!"".equals(cheeseName)) {
                cheese.setName(cheeseName);
                cheese.setDescription(description);
                cheese.setType(cheeseType);
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
