package ch.hevs.students.raclettedb.util;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.ui.BaseActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+ BaseActivity.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }


}
