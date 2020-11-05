package ch.hevs.students.raclettedb.ui.shieling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private EditText etShielingDescription;
    private TextView tvEditShielingTitle;

    private ShielingViewModel viewModel;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_shieling, frameLayout);

        navigationView.setCheckedItem(position);

        tvEditShielingTitle = findViewById(R.id.tvEditShielingTitle);
        etShielingName = findViewById(R.id.etShielingName);
        etShielingName.requestFocus();
        etShielingDescription = findViewById(R.id.etShielingDescription);
        Button btSaveShieling = findViewById(R.id.btSaveShieling);
        btSaveShieling.setOnClickListener(view -> {
            saveChanges(etShielingName.getText().toString(), etShielingDescription.getText().toString());
            onBackPressed();
            toast.show();
        });

        Long shielingId = getIntent().getLongExtra("shielingId", 0L);
        if (shielingId == 0L) {
            setTitle(getString(R.string.empty));
            tvEditShielingTitle.setText(R.string.shieling_new_title);
            toast = Toast.makeText(this, getString(R.string.shieling_new_created), Toast.LENGTH_LONG);
            isEditMode = false;
        } else {
            setTitle(getString(R.string.title_activity_edit_shieling));
            btSaveShieling.setText(R.string.update);
            toast = Toast.makeText(this, getString(R.string.shieling_edit_edited), Toast.LENGTH_LONG);
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

    @Override
    protected void onResume() {
        if(!settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false)) {
            finish();
        }
        super.onResume();
    }

    private void saveChanges(String shielingName, String description) {
        if (isEditMode) {
            if(!"".equals(shielingName)) {
                shieling.setName(shielingName);
                shieling.setDescription(description);
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
            newShieling.setDescription(description);
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
