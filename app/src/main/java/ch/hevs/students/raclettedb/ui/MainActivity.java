package ch.hevs.students.raclettedb.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;


import java.io.ByteArrayOutputStream;


import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.repository.CheeseRepository;
import ch.hevs.students.raclettedb.database.repository.ShielingRepository;
import ch.hevs.students.raclettedb.ui.cheese.CheeseDetailActivity;
import ch.hevs.students.raclettedb.ui.shieling.ShielingDetailActivity;
import ch.hevs.students.raclettedb.util.LocaleUtils;
import ch.hevs.students.raclettedb.util.MediaUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-" + MainActivity.class.getSimpleName();

    private boolean isAdmin = false;
    private String currentLocale = "";
    private CheeseRepository cheeseRepository;
    private ShielingRepository shielingRepository;
    private TextView[] tvMainFavorites;
    private ImageView[] ivMainFavorites;
    private TextView tvMainShielingName;
    private ImageView ivMainShieling;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;
    private MediaUtils mediaUtils = new MediaUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        // Récupération du stockage commun
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        // Est-ce que l'utilisateur est admin ?
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);

        if (settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT).equals(BaseActivity.PREFS_APP_LANGUAGE_DEFAULT)) {
            LocaleUtils.resetToSystemLocale(this);
        }

        Log.d(TAG, "Current locale : " + settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT));

        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        //recreate();


        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initiateView();

    }

    @Override
    protected void onResume() {
        setTitle(getString(R.string.app_name));
        navigationView.setCheckedItem(R.id.nav_none);
        Log.d(TAG, "Current locale : " + settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT) + ", has changed=" + settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false));
        Log.d(TAG, settings.toString());
        if (settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false)) {
            editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false);
            editor.apply();
            // On force la recréation de l'activity pour prendre en compte la nouvelle locale
            Log.d(TAG, "Recreating activity");
            recreate();
        }
        isAdmin = settings.getBoolean(BaseActivity.PREFS_IS_ADMIN, false);
        Log.d("TAG", isAdmin + "");
        if (isAdmin) {
            Toast.makeText(this, getString(R.string.admin_mode_active), Toast.LENGTH_SHORT).show();
        }

        // on vide le tablelayout
        TableLayout tl = findViewById(R.id.tableLayout);
        cheeseRepository = ((BaseApp) getApplication()).getCheeseRepository();
        cheeseRepository.getAllCheeses(getApplication()).observe(MainActivity.this, cheeseEntities -> {
            tl.removeAllViews();
            int i = 0;
            TableRow trImages = new TableRow(this);
            TableRow trLabels = new TableRow(this);
            for (CheeseEntity cheeseEntity :
                    cheeseEntities) {
                // Pour chaque item, on ajout un élément dans le TableRow Image et le TableRow label
                if (i < 3) {
                    // Textview avec le nom du fromage
                    TextView tv = new TextView(this);
                    tv.setText(cheeseEntity.getName());
                    tv.setTypeface(ResourcesCompat.getFont(this, R.font.dk_lemon_yellow_sun));
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tv.setOnClickListener(v -> showCheese(cheeseEntity.getId()));
                    // Imageview pour le logo
                    ImageView iv = new ImageView(this);
                    if(!TextUtils.isEmpty(cheeseEntity.getImagePath())) {
                        if(!cheeseEntity.getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                            if(BaseApp.CLOUD_ACTIVE) {
                                mediaUtils.getFromFirebase(MediaUtils.TARGET_CHEESES, cheeseEntity.getImagePath(), getApplicationContext(), iv);
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeFile(cheeseEntity.getImagePath());
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new ByteArrayOutputStream());
                                iv.setImageBitmap(bitmap);
                            }
                        } else {
                            iv.setImageResource(R.drawable.placeholder_cheese);
                        }
                    } else {
                        iv.setImageResource(R.drawable.placeholder_cheese);
                    }
                    iv.setPadding(8, 8, 8, 8);
                    float factor = getApplicationContext().getResources().getDisplayMetrics().density;

                    TableRow.LayoutParams lp = new TableRow.LayoutParams((int)(100*factor), (int)(100*factor));
                    iv.setLayoutParams(lp);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    iv.setOnClickListener(v -> showCheese(cheeseEntity.getId()));
                    // on les ajoute dans le TableRow respective
                    trImages.addView(iv);
                    trLabels.addView(tv);
                    // incrément du compteur
                    i++;
                }
            }
            // si on a qqch à afficher, on ajouter les rows à la TableLayout
            if (i > 0) {
                tl.addView(trImages);
                tl.addView(trLabels);
            } else { // Sinon affiche d'un message
                Log.d(TAG, "No cheeses");
                TextView tv = new TextView(this);
                tv.setText(getString(R.string.no_cheese));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setTypeface(ResourcesCompat.getFont(this, R.font.dk_lemon_yellow_sun));
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tl.addView(tv);
            }
        });

        shielingRepository = ((BaseApp) getApplication()).getShielingRepository();
        shielingRepository.getAllShielings(getApplication()).observe(MainActivity.this, shielingEntities -> {

            if (shielingEntities.size() > 0) {
                tvMainShielingName.setText(shielingEntities.get(0).getName());
                tvMainShielingName.setOnClickListener(v -> showShieling(shielingEntities.get(0).getId()));
                ivMainShieling.setOnClickListener(v -> showShieling(shielingEntities.get(0).getId()));
                ivMainShieling.setVisibility(View.VISIBLE);

                ivMainShieling.setImageResource(R.drawable.placeholder_shieling);
                if(!TextUtils.isEmpty(shielingEntities.get(0).getImagePath())) {
                    if(!shielingEntities.get(0).getImagePath().equals(BaseActivity.IMAGE_CHEESE_DEFAULT)) {
                        if(BaseApp.CLOUD_ACTIVE) {
                            mediaUtils.getFromFirebase(MediaUtils.TARGET_CHEESES, shielingEntities.get(0).getImagePath(), getApplicationContext(), ivMainShieling);
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeFile(shielingEntities.get(0).getImagePath());
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new ByteArrayOutputStream());
                            ivMainShieling.setImageBitmap(bitmap);
                        }
                    }
                }

            } else {
                tvMainShielingName.setText(getString(R.string.no_shieling));
                tvMainShielingName.setOnClickListener(null);
                ivMainShieling.setVisibility(View.GONE);
                ivMainShieling.setOnClickListener(null);
            }
        });

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.close));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getString(R.string.close_application));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.close), (dialog, which) -> close());
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> alertDialog.dismiss());
        alertDialog.show();

    }

    private void initiateView() {
        tvMainShielingName = findViewById(R.id.tvMainShielingName);
        ivMainShieling = findViewById(R.id.ivMainShieling);

        

        tvMainFavorites = new TextView[3];
        tvMainFavorites[0] = findViewById(R.id.tvMainFavorites1);
        tvMainFavorites[1] = findViewById(R.id.tvMainFavorites2);
        tvMainFavorites[2] = findViewById(R.id.tvMainFavorites3);
        ivMainFavorites = new ImageView[3];
        ivMainFavorites[0] = findViewById(R.id.ivMainFavorites1);
        ivMainFavorites[1] = findViewById(R.id.ivMainFavorites2);
        ivMainFavorites[2] = findViewById(R.id.ivMainFavorites3);
    }

    public void showCheese(Long cheeseId) {
        Intent intent = new Intent(MainActivity.this, CheeseDetailActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY
        );
        intent.putExtra("cheeseId", cheeseId);
        startActivity(intent);
    }

    public void showShieling(Long shielingId) {
        Intent intent = new Intent(MainActivity.this, ShielingDetailActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY
        );
        intent.putExtra("shielingId", shielingId);
        startActivity(intent);
    }

}
