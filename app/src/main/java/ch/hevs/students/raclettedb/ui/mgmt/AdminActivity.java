package ch.hevs.students.raclettedb.ui.mgmt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import ch.hevs.students.raclettedb.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d("ADMIN", "Entered admin activity");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            /* Type here what u wanted to do on pressing the back button*/
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}