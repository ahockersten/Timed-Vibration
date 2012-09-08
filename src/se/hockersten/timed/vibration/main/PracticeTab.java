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

import java.util.ArrayList;
import java.util.Calendar;

import se.hockersten.timed.vibration.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PracticeTab extends Fragment implements Tab {
    private static String COUNTING = "COUNTING";
    private static String VIBRATIONS = "VIBRATIONS";
    private static String SPIN_SINGLE_POS = "SPIN_SINGLE_POS";
    private static String SPIN_DOUBLE_POS = "SPIN_DOUBLE_POS";

    private View root;
    private boolean counting = false;
    private ArrayList<PendingIntent> vibrations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_practice, container, false);

        Spinner spinSingle = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSingle.setAdapter(adapter);

        Spinner spinDouble = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
        ArrayAdapter<CharSequence> adapter2 =
            ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDouble.setAdapter(adapter2);

        if (savedInstanceState != null) {
            counting = savedInstanceState.getBoolean(COUNTING);
            vibrations = savedInstanceState.getParcelableArrayList(VIBRATIONS);
            spinSingle.setSelection(savedInstanceState.getInt(SPIN_SINGLE_POS));
            spinDouble.setSelection(savedInstanceState.getInt(SPIN_DOUBLE_POS));
        }
        else {
            vibrations = new ArrayList<PendingIntent>(2);
            spinSingle.setSelection(1); // 1 minute by default
            spinDouble.setSelection(3); // 5 minutes by default
        }

        updateUI();
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putBoolean(COUNTING, counting);
        b.putParcelableArrayList(VIBRATIONS, vibrations);
        Spinner spinSingle = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
        Spinner spinDouble = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
        b.putInt(SPIN_SINGLE_POS, spinSingle.getSelectedItemPosition());
        b.putInt(SPIN_DOUBLE_POS, spinDouble.getSelectedItemPosition());
    }

    @Override
    public void onResume() {
        Button startBtn = (Button) getActivity().findViewById(R.id.mainPractice_btnStart);
        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                flipCounting();
                updateUI();
            }
        });

        super.onResume();
    }

    /**
     * Stops counting if currently counting, starts counting if not currently
     * counting.
     */
    public void flipCounting() {
        if (counting) {
            stopCounting();
        }
        else {
            startCounting();
        }
    }

    /**
     * Starts the vibrating counter
     */
    public void startCounting() {
        Spinner spinSingle = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
        Spinner spinDouble = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
        counting = true;

        int singleMinutes = spinPosToMinutes(spinSingle.getSelectedItemPosition());
        if (singleMinutes != -1) {
            addVibration(singleMinutes, 1);
        }
        int doubleMinutes = spinPosToMinutes(spinDouble.getSelectedItemPosition());
        if (doubleMinutes != -1) {
            addVibration(doubleMinutes, 2);
        }
    }

    /**
     * Stops the vibrating counter
     */
    public void stopCounting() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        counting = false;
        for (PendingIntent vibration : vibrations) {
            alarmManager.cancel(vibration);
        }
        vibrations.clear();
    }

    /**
     * Adds a vibration at the given interval.
     * @param interval The interval to vibrate at (in minutes)
     * @param timesToVibrate The number of times to vibrate
     */
    private void addVibration(int interval, int timesToVibrate) {
        Calendar nextApplicableMinute = Calendar.getInstance();
        int nextMinute = normalizedMinuteDelay(nextApplicableMinute.get(Calendar.MINUTE), interval);
        nextApplicableMinute.add(Calendar.MINUTE, nextMinute);
        long delay = nextMinute *  60000;
        nextApplicableMinute.set(Calendar.SECOND, 0);
        nextApplicableMinute.set(Calendar.MILLISECOND, 0);
        long firstVibration = nextApplicableMinute.getTimeInMillis();
        Intent i = new Intent(getActivity(), Vibrate.class);
        i.putExtra(Vibrate.TIMES_TO_VIBRATE, timesToVibrate);
        PendingIntent task = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
        vibrations.add(task);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibration, delay, task);
        // DEBUG: vibrate per second instead of per minute
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibration / 60, delay / 60, task);
    }

    /**
     * Updates the UI based on whether we are currently counting or not
     */
    private void updateUI() {
        Button startBtn = (Button) root.findViewById(R.id.mainPractice_btnStart);
        Spinner spinSingle = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
        Spinner spinDouble = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
        spinSingle.setEnabled(!counting);
        spinDouble.setEnabled(!counting);
        if (counting) {
            startBtn.setText(R.string.stop_counting);
        }
        else {
            startBtn.setText(R.string.start_counting);
        }
    }

    /**
     * Given the current minute in an hour, and an ínterval, this returns the
     * appropriate number of minutes to delay to get an "even" count. For
     * a delay of 1 minute, this will always return a 1 minute delay. For 5
     * minutes, this would return a number between 1 and 5 which, when this
     * delay is applied, would end at the next appropriate 5 minute boundary
     * (so 3 would delay 2 minutes, 6 would delay 4 minutes and so on).
     *
     * @param currentMinute The current clock minute
     * @param interval The interval to vibrate at
     * @return The number of minutes to wait until the first vibration
     */
    private static int normalizedMinuteDelay(int currentMinute, int interval) {
        return interval - currentMinute % interval;
    }

    /**
     * Converts a position on the interval spinners to its value in minutes
     * @param spinPos The position on the spinner
     * @return The value of the spinner, in minutes, or -1 if disabled
     */
    private int spinPosToMinutes(int spinPos) {
        switch (spinPos) {
        case 0:
            return -1;
        case 1:
            return 1;
        case 2:
            return 2;
        case 3:
            return 5;
        case 4:
            return 10;
        case 5:
            return 15;
        case 6:
            return 30;
        case 7:
            return 60;
        default:
            // how did we end up here?
            assert(false);
            return -1;
        }
    }

    @Override
    public void onTabVisible() {
        Vibrate.setEnabled(true);
    }

    @Override
    public void onTabInvisible() {
        Vibrate.setEnabled(false);
    }
}
