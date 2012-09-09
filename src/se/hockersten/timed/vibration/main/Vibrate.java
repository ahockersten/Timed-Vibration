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
    private static int firstVibrationInterval;
    private static int secondVibrationInterval;
    private static int firstVibrationTimes;
    private static int secondVibrationTimes;
    private static int firstVibrationDuration;
    private static int secondVibrationDuration;
    private static int firstVibrationRestDuration;
    private static int secondVibrationRestDuration;
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

    public static void setFirstVibrationInterval(int interval) {
        firstVibrationInterval = interval;
    }
    public static void setSecondVibrationInterval(int interval) {
        secondVibrationInterval = interval;
    }

    public static void setFirstVibrationTimes(int times) {
        firstVibrationTimes = times;
    }
    public static void setSecondVibrationTimes(int times) {
        secondVibrationTimes = times;
    }

    public static void setFirstVibrationDuration(int duration) {
        firstVibrationDuration = duration;
    }
    public static void setSecondVibrationDuration(int duration) {
        secondVibrationDuration = duration;
    }

    public static void setFirstVibrationRestDuration(int duration) {
        firstVibrationRestDuration = duration;
    }
    public static void setSecondVibrationRestDuration(int duration) {
        secondVibrationRestDuration = duration;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Calendar currentTime = Calendar.getInstance();
        boolean shouldFirstVibrate = firstVibrationInterval != 0 && (currentTime.get(Calendar.MINUTE) % Vibrate.firstVibrationInterval) == 0;
        boolean shouldSecondVibrate = secondVibrationInterval != 0 && (currentTime.get(Calendar.MINUTE) % Vibrate.secondVibrationInterval) == 0;
        // DEBUG: vibrate per second instead of per minute
        //boolean shouldFirstVibrate = firstVibrationInterval != 0 && (currentTime.get(Calendar.SECOND) % Vibrate.firstVibrationInterval) == 0;
        //boolean shouldSecondVibrate = secondVibrationInterval != 0 && (currentTime.get(Calendar.SECOND) % Vibrate.secondVibrationInterval) == 0;
        long[] pattern = null;
        if (shouldFirstVibrate && shouldSecondVibrate) {
            // second trumps first in case they are equal
            if (firstVibrationTimes > secondVibrationTimes) {
                shouldSecondVibrate = false;
            }
        }
        if (shouldSecondVibrate) {
            pattern = new long[2 * secondVibrationTimes];
            pattern[0] = 0;
            for (int i = 1; i < 2 * secondVibrationTimes; i+=2) {
                pattern[i] = secondVibrationDuration;
            }
            for (int i = 2; i < 2 * secondVibrationTimes; i+=2) {
                pattern[i] = secondVibrationRestDuration;
            }
        }
        else if (shouldFirstVibrate) {
            pattern = new long[2 * firstVibrationTimes];
            pattern[0] = 0;
            for (int i = 1; i < 2 * firstVibrationTimes; i+=2) {
                pattern[i] = firstVibrationDuration;
            }
            for (int i = 2; i < 2 * firstVibrationTimes; i+=2) {
                pattern[i] = firstVibrationRestDuration;
            }
        }
        if (pattern != null) {
            vibrator.vibrate(pattern, -1);
        }
    }
}
