// Copyright 2012, Anders Höckersten
//
// This file is part of Timed Vibration.
//
// Timed Vibration is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Timed Vibration is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Timed Vibration.  If not, see <http://www.gnu.org/licenses/>.

package se.hockersten.timed.vibration.main;

import se.hockersten.timed.vibration.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setupFromPreferences();
    }

    /**
     * Sets various actual settings from preferences. Used to initialize
     * the application state on startup.
     */
    private void setupFromPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String firstTimesToVibrate = sharedPref.getString("pref_first_times_to_vibrate", "1");
        Vibrate.setFirstVibrationTimes(Integer.parseInt(firstTimesToVibrate));
        String secondTimesToVibrate = sharedPref.getString("pref_second_times_to_vibrate", "2");
        Vibrate.setSecondVibrationTimes(Integer.parseInt(secondTimesToVibrate));

        String firstVibrationDuration = sharedPref.getString("pref_first_vibration_duration", "100");
        Vibrate.setFirstVibrationDuration(Integer.parseInt(firstVibrationDuration));
        String secondVibrationDuration = sharedPref.getString("pref_second_vibration_duration", "100");
        Vibrate.setSecondVibrationDuration(Integer.parseInt(secondVibrationDuration));

        String firstVibrationRestDuration = sharedPref.getString("pref_first_vibration_rest_duration", "100");
        Vibrate.setFirstVibrationRestDuration(Integer.parseInt(firstVibrationRestDuration));
        String secondVibrationRestDuration = sharedPref.getString("pref_second_vibration_rest_duration", "100");
        Vibrate.setSecondVibrationRestDuration(Integer.parseInt(secondVibrationRestDuration));
    }
}
