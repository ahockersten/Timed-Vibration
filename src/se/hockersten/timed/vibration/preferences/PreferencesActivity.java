package se.hockersten.timed.vibration.preferences;

import se.hockersten.timed.vibration.R;
import se.hockersten.timed.vibration.main.Vibrate;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
    // suppress the warning about calls to addPreferencesFromResource being
    // deprecated. There is no compatibility library for the new
    // PreferenceFragment framework, so it cannot be used without moving to
    // API level 11
    // A possible solution would be to detect the API level and create a
    // fragment for API level 11 and beyond, but that's a lot of work for
    // quite a small gain
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_first_times_to_vibrate")) {
            EditTextPreference firstTimesToVibrate = (EditTextPreference) findPreference(key);
            Vibrate.setFirstVibrationTimes(Integer.parseInt(firstTimesToVibrate.getText()));
        }
        if (key.equals("pref_second_times_to_vibrate")) {
            EditTextPreference secondTimesToVibrate = (EditTextPreference) findPreference(key);
            Vibrate.setSecondVibrationTimes(Integer.parseInt(secondTimesToVibrate.getText()));
        }

        if (key.equals("pref_first_vibration_duration")) {
            EditTextPreference firstVibrationDuration = (EditTextPreference) findPreference(key);
            Vibrate.setFirstVibrationDuration(Integer.parseInt(firstVibrationDuration.getText()));
        }
        if (key.equals("pref_second_vibration_duration")) {
            EditTextPreference secondVibrationDuration = (EditTextPreference) findPreference(key);
            Vibrate.setSecondVibrationDuration(Integer.parseInt(secondVibrationDuration.getText()));
        }

        if (key.equals("pref_first_vibration_rest_duration")) {
            EditTextPreference firstVibrationRestDuration = (EditTextPreference) findPreference(key);
            Vibrate.setFirstVibrationRestDuration(Integer.parseInt(firstVibrationRestDuration.getText()));
        }
        if (key.equals("pref_second_vibration_rest_duration")) {
            EditTextPreference firstVibrationRestDuration = (EditTextPreference) findPreference(key);
            Vibrate.setSecondVibrationRestDuration(Integer.parseInt(firstVibrationRestDuration.getText()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }
}
