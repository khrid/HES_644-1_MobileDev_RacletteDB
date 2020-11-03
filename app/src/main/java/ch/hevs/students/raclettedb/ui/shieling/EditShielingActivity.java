package ch.hevs.students.raclettedb.ui.shieling;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class EditShielingActivity extends BaseActivity {

    private static final String TAG = "EditShielingActivity";

    private ShielingEntity shieling;
    private boolean isEditMode;
    private Toast toast;
    private EditText etShielingName;

    private ShielingViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_shieling, frameLayout);

        navigationView.setCheckedItem(position);

        etShielingName = findViewById(R.id.shielingName);
        etShielingName.requestFocus();
        Button saveBtn = findViewById(R.id.createShielingButton);
        saveBtn.setOnClickListener(view -> {
            saveChanges(etShielingName.getText().toString());
            onBackPressed();
            toast.show();
        });

        Long shielingId = getIntent().getLongExtra("shielingId", 0L);
        if (shielingId == 0L) {
            setTitle(getString(R.string.title_activity_create_shieling));
            toast = Toast.makeText(this, getString(R.string.shieling_created), Toast.LENGTH_LONG);
            isEditMode = false;
        } else {
            setTitle(getString(R.string.title_activity_edit_shieling));
            saveBtn.setText(R.string.action_update);
            toast = Toast.makeText(this, getString(R.string.shieling_edited), Toast.LENGTH_LONG);
            isEditMode = true;
        }

        ShielingViewModel.Factory factory = new ShielingViewModel.Factory(
                getApplication(), shielingId);
        viewModel = ViewModelProviders.of(this, factory).get(ShielingViewModel.class);
        if (isEditMode) {
            viewModel.getShieling().observe(this, shielingEntity -> {
                if (shielingEntity != null) {
                    shieling = shielingEntity;
                    etShielingName.setText(shieling.getName());
                }
            });
        }
    }

    private void saveChanges(String shielingName) {
        if (isEditMode) {
            if(!"".equals(shielingName)) {
                shieling.setName(shielingName);
                viewModel.updateShieling(shieling, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "updateShieling: success");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "updateShieling: failure", e);
                    }
                });
            }
        } else {
            ShielingEntity newShieling = new ShielingEntity();
            newShieling.setName(shielingName);
            viewModel.createShieling(newShieling, new OnAsyncEventListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "createShieling: success");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "createShieling: failure", e);
                }
            });
        }
    }
}
