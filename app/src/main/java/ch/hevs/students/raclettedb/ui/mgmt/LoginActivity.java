package ch.hevs.students.raclettedb.ui.mgmt;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.ui.MainActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText et_login_password;
    private Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_login);

        setContentView(R.layout.activity_login);

        et_login_password = findViewById(R.id.etLoginPassword);
        bt_login = findViewById(R.id.btLogin);
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

    private void attemptLogin() {

        et_login_password.setError(null);

        String password = et_login_password.getText().toString();

        if(!password.isEmpty()) {
            if(password.equals(BaseApp.ADMIN_PASSWORD)) {
                SharedPreferences.Editor editor = getSharedPreferences(BaseActivity.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean(BaseActivity.PREFS_IS_ADMIN, true);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                et_login_password.setText("");
            } else {
                et_login_password.setError(getString(R.string.admin_incorrect_password));
                et_login_password.requestFocus();
                et_login_password.setText("");
            }
        } else {
            et_login_password.setError(getString(R.string.admin_incorrect_password));
            et_login_password.requestFocus();
            et_login_password.setText("");
        }
    }

}

