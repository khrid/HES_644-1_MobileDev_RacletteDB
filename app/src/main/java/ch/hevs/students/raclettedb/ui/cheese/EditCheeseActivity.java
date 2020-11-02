package ch.hevs.students.raclettedb.ui.cheese;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.cheese.CheeseViewModel;

public class EditCheeseActivity extends BaseActivity {

    private static final String TAG = "EditCheeseActivity";

    private CheeseEntity cheese;
    private boolean isEditMode;
    private Toast toast;
    private EditText etCheeseName;
    private EditText etCheeseType;

    private CheeseViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_cheese, frameLayout);

        navigationView.setCheckedItem(position);

        etCheeseName = findViewById(R.id.cheeseName);
        etCheeseName.requestFocus();
        etCheeseType = findViewById(R.id.cheeseType);
        Button saveBtn = findViewById(R.id.createCheeseButton);
        saveBtn.setOnClickListener(view -> {
            saveChanges(etCheeseName.getText().toString(), etCheeseType.getText().toString());
            onBackPressed();
            toast.show();
        });

        Long cheeseId = getIntent().getLongExtra("cheeseId", 0L);
        if (cheeseId == 0L) {
            setTitle(getString(R.string.title_activity_create_cheese));
            toast = Toast.makeText(this, getString(R.string.cheese_created), Toast.LENGTH_LONG);
            isEditMode = false;
        } else {
            setTitle(getString(R.string.title_activity_edit_cheese));
            saveBtn.setText(R.string.action_update);
            toast = Toast.makeText(this, getString(R.string.cheese_edited), Toast.LENGTH_LONG);
            isEditMode = true;
        }

        CheeseViewModel.Factory factory = new CheeseViewModel.Factory(
                getApplication(), cheeseId);
        viewModel = ViewModelProviders.of(this, factory).get(CheeseViewModel.class);
        if (isEditMode) {
            viewModel.getCheese().observe(this, cheeseEntity -> {
                if (cheeseEntity != null) {
                    cheese = cheeseEntity;
                    etCheeseName.setText(cheese.getName());
                    etCheeseType.setText(cheese.getType());
                }
            });
        }
    }

    private void saveChanges(String cheeseName, String cheeseType) {
        if (isEditMode) {
            if(!"".equals(cheeseName)) {
                cheese.setName(cheeseName);
                cheese.setType(cheeseType);
                viewModel.updateCheese(cheese, new OnAsyncEventListener() {
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
            newCheese.setType(cheeseType);
            viewModel.createCheese(newCheese, new OnAsyncEventListener() {
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
