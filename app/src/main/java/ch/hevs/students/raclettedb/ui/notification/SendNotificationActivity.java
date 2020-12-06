package ch.hevs.students.raclettedb.ui.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.CustomSupportMapFragment;
import ch.hevs.students.raclettedb.util.MediaUtils;
import ch.hevs.students.raclettedb.util.OnAsyncEventListener;
import ch.hevs.students.raclettedb.viewmodel.shieling.ShielingViewModel;

public class SendNotificationActivity extends BaseActivity {

    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-"+"SendNotificationActivity";

    private Toast toast;
    private EditText etNotificationHeader;
    private EditText etNotificationText;
    private TextView tvSendNotificationTitle;


    static SharedPreferences settings;
    static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_send_notification, frameLayout);

        navigationView.setCheckedItem(position);

        tvSendNotificationTitle = findViewById(R.id.tvSendNotificationTitle);
        etNotificationHeader = findViewById(R.id.etNotificationHeader);
        etNotificationHeader.requestFocus();
        etNotificationText = findViewById(R.id.etNotificationText);
        Button btSendNotification = findViewById(R.id.btSendNotification);
        btSendNotification.setOnClickListener(view -> {
            if(etNotificationHeader.getText().toString().isEmpty()) {
                toast = Toast.makeText(this, getString(R.string.send_notification_header_empty), Toast.LENGTH_LONG);
                etNotificationHeader.requestFocus();
            } else{
                sendNotification(etNotificationHeader.getText().toString(), etNotificationText.getText().toString());
                etNotificationHeader.setText("");
                etNotificationText.setText("");
                onBackPressed();
                toast = Toast.makeText(this, getString(R.string.send_notification_success), Toast.LENGTH_LONG);
            }

            toast.show();
        });



    }


    public void sendNotification(String header, String text) {

        String URL = "https://fcm.googleapis.com/fcm/send";
        RequestQueue mRequestQue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+"racletteDB");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title",header);
            notificationObj.put("body",text);


            json.put("notification",notificationObj);



            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("MUR", "onResponse: ");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("MUR", "onError: "+error.networkResponse);
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    // On donne la clé récupérée dans la console Firebase Cloud Messaging sur le projet
                    header.put("authorization","key=AAAAsY4AhL4:APA91bHQj-bdWDS5Tbd_WEngxVhah-pENff0ZWFBIzgwfYNLxNXX9jGLwZ_6wp-PWd03q3X0fUG8yxSmSyuys6e-Q3djNlS08OqsjJTlNXP-A4m-P8s9h06Bn4cfLpwjGawXZQAu4v4u");
                    return header;
                }
            };
            mRequestQue.add(request);
        }
        catch (JSONException e)

        {
            e.printStackTrace();
        }
    }



    @Override
    protected void onResume() {
        if (!settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false)) {
            finish();
        }
        super.onResume();
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }


}
