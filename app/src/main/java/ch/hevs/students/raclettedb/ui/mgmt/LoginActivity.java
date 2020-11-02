package ch.hevs.students.raclettedb.ui.mgmt;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.AppDatabase;
import ch.hevs.students.raclettedb.database.repository.ClientRepository;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.ui.MainActivity;

import static ch.hevs.students.raclettedb.database.AppDatabase.initializeDemoData;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView emailView;
    private EditText et_login_password;
    private Button bt_login;
    private ProgressBar progressBar;

    private ClientRepository repository;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_login);

        setContentView(R.layout.activity_login);

        et_login_password = findViewById(R.id.et_login_password);
        bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(view -> attemptLogin());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * Attempts to sign in or register the client specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        et_login_password.setError(null);

        String password = et_login_password.getText().toString();

        if(!password.isEmpty()) {
            if(password.equals(BaseApp.ADMIN_PASSWORD)) {
                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(BaseActivity.PREFS_IS_ADMIN, 1);
                editor.apply();
                Log.d("TAG", "admin mode activated");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                et_login_password.setText("");
            } else {
                et_login_password.setError(getString(R.string.error_incorrect_password));
                et_login_password.requestFocus();
                et_login_password.setText("");
            }
        } else {
            et_login_password.setError(getString(R.string.error_incorrect_password));
            et_login_password.requestFocus();
            et_login_password.setText("");
        }
    }

}

