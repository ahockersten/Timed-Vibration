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

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class Vibrate extends BroadcastReceiver {
    private static int singleVibrationInterval;
    private static int doubleVibrationInterval;
    private static PendingIntent vibrationTask;

    /**
     * Enables or disables vibration globally.
     * @param enabled Whether vibration should be enabled or disabled
     * @param owner The Activity that owns the vibration
     */
    public static void setEnabled(boolean enabled, Activity owner) {
        AlarmManager alarmManager = (AlarmManager) owner.getSystemService(Context.ALARM_SERVICE);
        if (enabled) {
            Calendar nextMinute = Calendar.getInstance();
            Intent i = new Intent(owner, Vibrate.class);
            vibrationTask = PendingIntent.getBroadcast(owner, 0, i, 0);
            nextMinute.add(Calendar.MINUTE, 1);
            nextMinute.set(Calendar.SECOND, 0);
            // DEBUG: vibrate per second instead of per minute
            //nextMinute.add(Calendar.SECOND, 1);
            nextMinute.set(Calendar.MILLISECOND, 0);
            long firstVibration = nextMinute.getTimeInMillis();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibration, 60000, vibrationTask);
            // DEBUG: vibrate per second instead of per minute
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibration, 1000, vibrationTask);
        }
        else {
            alarmManager.cancel(vibrationTask);
        }
    }

    public static void setSingleVibrationInterval(int interval) {
        singleVibrationInterval = interval;
    }

    public static void setDoubleVibrationInterval(int interval) {
        doubleVibrationInterval = interval;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Calendar currentTime = Calendar.getInstance();
        boolean shouldSingleVibrate = singleVibrationInterval != 0 && (currentTime.get(Calendar.MINUTE) % Vibrate.singleVibrationInterval) == 0;
        boolean shouldDoubleVibrate = doubleVibrationInterval != 0 && (currentTime.get(Calendar.MINUTE) % Vibrate.doubleVibrationInterval) == 0;
        // DEBUG: vibrate per second instead of per minute
        //boolean shouldSingleVibrate = singleVibrationInterval != 0 && (currentTime.get(Calendar.SECOND) % Vibrate.singleVibrationInterval) == 0;
        //boolean shouldDoubleVibrate = doubleVibrationInterval != 0 && (currentTime.get(Calendar.SECOND) % Vibrate.doubleVibrationInterval) == 0;
        long[] pattern = null;
        if (shouldDoubleVibrate) {
            pattern = new long[4];
            pattern[0] = 0;
            for (int i = 1; i < 4; i++) {
                pattern[i] = 100;
            }
        }
        else if (shouldSingleVibrate) {
            pattern = new long[2];
            pattern[0] = 0;
            pattern[1] = 100;
        }
        if (pattern != null) {
            vibrator.vibrate(pattern, -1);
        }
    }
}
