package ch.hevs.students.raclettedb.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import java.util.Locale;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.ui.BaseActivity;

public class LocaleUtils {
    private static final String TAG = "TAG-"+ BaseApp.APP_NAME+"-LocaleUtils";
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    /**
     * Pour changer la locale du système
     * @param code le code de la langue (FR, DE, EN)
     * @param activity l'activity qui appelle
     */
    public static void changeLocale(String code, Activity activity) {

        // les paramètres partagés
        settings = activity.getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        // l'editeur pour les modifier
        editor = settings.edit();
        editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, true);
        editor.putString(BaseActivity.PREFS_APP_LANGUAGE, code);
        editor.apply();


        Log.d(TAG, "call from "+activity.getComponentName()+", code=" + code);
        Log.d(TAG, settings.toString());
        // Changement de langue
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Resources resources = activity.getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        // On force la recréation de l'activity pour prendre en compte la nouvelle locale
        activity.recreate();
        Log.d(TAG, "language in prefs after apply : "+settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT)+", has changed : " + settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false));
    }

    /**
     * Pour remettre à zéro la locale selon la locale système
     * @param activity l'activity qui appelle
     */
    public static void resetToSystemLocale(Activity activity) {
        settings = activity.getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        editor.putString(BaseActivity.PREFS_APP_LANGUAGE, Resources.getSystem().getConfiguration().locale.getLanguage());
        editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, true);
        editor.apply();
        activity.recreate();
    }
}
