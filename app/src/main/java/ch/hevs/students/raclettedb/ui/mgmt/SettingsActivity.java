package ch.hevs.students.raclettedb.ui.mgmt;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;

import java.util.List;

import ch.hevs.students.raclettedb.BaseApp;
import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.AppDatabase;
import ch.hevs.students.raclettedb.ui.BaseActivity;
import ch.hevs.students.raclettedb.util.LocaleUtils;

import static ch.hevs.students.raclettedb.database.AppDatabase.initializeDemoData;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "TAG-" + BaseApp.APP_NAME + "-SettingsActivity";

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        // Récupération du stockage commun
        /*settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        if(settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false)) {
            Utils.changeLocale(settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT), this);
            editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false);
            editor.apply();
        }*/
        super.onResume();
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(BaseActivity.PREFS_NAME, 0);
        editor = settings.edit();
        if (settings.getBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false)) {
            //Utils.changeLocale(settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT), this);
            //editor.putBoolean(BaseActivity.PREFS_APP_LANGUAGE_CHANGED, false);
            //editor.apply();
            // On force la recréation de l'activity pour prendre en compte la nouvelle locale
            //Log.d(TAG, "Recreating activity");
            //recreate();
            //getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        }
        setupActionBar();
        setActionBarTitle(getString(R.string.title_activity_settings));
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setActionBarTitle(String title) {
        setTitle(title);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName)
                || LanguageChangePreferenceFragment.class.getName().equals(fragmentName)
                || ResetDatabaseFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class LanguageChangePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_language);

            setHasOptionsMenu(true);

            ListPreference pref = (ListPreference) findPreference("AppLanguage");
            String[] arrays = getResources().getStringArray(R.array.languages_values);
            String target = settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT);
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i].equals(target)) {
                    pref.setValueIndex(i);
                }
            }
            Log.d(TAG, pref.getEntries().length + "");
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(BaseActivity.PREFS_APP_LANGUAGE)) {
                LocaleUtils.changeLocale(sharedPreferences.getString(key, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT), getActivity());
            }
        }
    }

    public static class ResetDatabaseFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_reset);
            setHasOptionsMenu(true);
            SwitchPreference spResetDb = (SwitchPreference) findPreference("pref_reset_db");
            if (spResetDb != null) {
                spResetDb.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (newValue.equals(true)) {
                        Log.d(TAG, "activé");
                        createDeleteDialog(0);
                    }
                    return false;
                });
            }

            SwitchPreference spResetPrefs = (SwitchPreference) findPreference("pref_reset_prefs");
            if (spResetPrefs != null) {
                spResetPrefs.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (newValue.equals(true)) {
                        Log.d(TAG, "activé");
                        createDeleteDialog(1);
                    }
                    return false;
                });
            }
        }

        private void createDeleteDialog(int target) {
            Context ctx = getContext();
            LayoutInflater inflater = LayoutInflater.from(ctx);
            final View view = inflater.inflate(R.layout.dialog_reset_db, null);
            final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle(getString(R.string.settings_data_room_reset_dialog_title));
            alertDialog.setCancelable(false);

            final TextView deleteMessage = view.findViewById(R.id.tvResetdb);
            deleteMessage.setText(getString(R.string.settings_data_room_reset_dialog_text));

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.execute), (dialog, which) -> {
                Toast toast = Toast.makeText(ctx, getString(R.string.settings_data_room_reset_successful), Toast.LENGTH_LONG);
                if (target == 0) {
                    initializeDemoData(AppDatabase.getInstance(ctx));
                } else if (target == 1) {
                    settings.edit().clear().apply();
                    Log.d(TAG, settings.getString(BaseActivity.PREFS_APP_LANGUAGE, BaseActivity.PREFS_APP_LANGUAGE_DEFAULT));
                    LocaleUtils.resetToSystemLocale(getActivity());
                }
                toast.show();
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> alertDialog.dismiss());
            alertDialog.setView(view);
            alertDialog.show();
        }

    }

}
