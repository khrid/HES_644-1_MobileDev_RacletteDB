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

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }


    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to your app server. Not needed in our project as we send notifications from FCM console

    }

}
