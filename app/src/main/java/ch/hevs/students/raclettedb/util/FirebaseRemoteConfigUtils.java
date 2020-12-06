package ch.hevs.students.raclettedb.util;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;

public class FirebaseRemoteConfigUtils {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-FirebaseRemoteConfigUtils";
    boolean sync = false;
    private Activity activity;

    public FirebaseRemoteConfigUtils(Activity activity) {
        this.activity = activity;
    }

    public void setup() {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(30)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, (OnCompleteListener<Boolean>) task -> {
                    if (task.isSuccessful()) {
                        sync = task.getResult();
                        Log.d(TAG, "Config params updated: " + sync);

                    }
                });
    }

    public String getParam(String paramName) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getString(paramName);
    }
}
