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
import java.util.LinkedList;

import se.hockersten.timed.vibration.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CompetitionTab extends Fragment implements Tab {
    // Constants used when saving state for this Fragment
    private static final String COMPETING = "COMPETING";
    private static final String TAP_TIMES = "TAP_TIMES";
    private static final String LAST_PRESS = "LAST_PRESS";
    private static final String VISIBLE = "VISIBLE";

    private View root;
    /** True if competition mode is currently turned on */
    private boolean competing;
    /** True if this tab is currently visible */
    private boolean visible;
    /** The time the "tap me" button was last pressed */
    private Calendar lastPress;
    /**
     * This WakeLock is grabbed whenever competition mode is turned on and
     * this tab is being displayed. This is to allow the user to always be
     * be able to tap the button when it is visible.
     */
    private PowerManager.WakeLock wakeLock;
    /** The last 5 taps, in milliseconds. The latest tap is first in the list */
    private LinkedList<Long> tapTimes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CompetitionTab.onCreateView()");
        if (savedInstanceState != null) {
            competing = savedInstanceState.getBoolean(COMPETING);
            visible = savedInstanceState.getBoolean(VISIBLE);
        }

        root = inflater.inflate(R.layout.main_competition, container, false);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putBoolean(COMPETING, competing);
        b.putBoolean(VISIBLE, visible);
        b.putSerializable(LAST_PRESS, lastPress);
    }

    @Override
    public void onResume() {
        tapTimes = new LinkedList<Long>();
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        for (int i = 0; i < 5; i++) {
            tapTimes.add(i, sharedPrefs.getLong(TAP_TIMES + i, 0));
        }

        Button startBtn = (Button) root.findViewById(R.id.mainCompetition_btnStart);
        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                flipCompeting();
                updateUI();
            }
        });

        Button tapBtn = (Button) root.findViewById(R.id.mainCompetition_btnTap);
        tapBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                assert(!competing); // this button should be greyed out when not competing, so being able to click it indicates something is wrong
                Calendar currentTime = Calendar.getInstance();

                long timeDiff = currentTime.getTimeInMillis() - lastPress.getTimeInMillis();
                if (tapTimes.size() > 4) {
                    tapTimes.removeLast();
                }
                tapTimes.addFirst(timeDiff);
                lastPress = currentTime;

                updateUI();
            }
        });

        if (competing && visible) {
            // take the new wakelock if we are competing
            wakeLock.acquire();
        }
        updateUI();
        super.onResume();
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        for (int i = 0; i < tapTimes.size(); i++) {
            prefsEditor.putLong(TAP_TIMES + i, tapTimes.get(i));
        }
        prefsEditor.commit();

        if (competing && visible) {
            // need to release the wakelock here, because it can't be saved
            // a new wakelock is taken when recreating
            wakeLock.release();
        }
        super.onStop();
    }

    public void flipCompeting() {
        if (competing) {
            stopCompetition();
        }
        else {
            startCompetition();
        }
    }

    public void startCompetition() {
         competing = true;
         wakeLock.acquire();
         lastPress = Calendar.getInstance();
    }

    public void stopCompetition() {
        competing = false;
        wakeLock.release();
    }

    private void updateUI() {
        Button startBtn = (Button) root.findViewById(R.id.mainCompetition_btnStart);
        Button tapBtn = (Button) root.findViewById(R.id.mainCompetition_btnTap);
        tapBtn.setEnabled(competing);
        if (competing) {
            startBtn.setText(R.string.stop_counting);
        }
        else {
            startBtn.setText(R.string.start_counting);
        }
        Resources res = getResources();
        StringBuffer resultText = new StringBuffer(res.getString(R.string.last_results));
        for (long tapTime : tapTimes) {
            if (tapTime != 0) {
                long minDiff = tapTime / 60000;
                long secDiff = tapTime / 1000 - minDiff * 60;
                long milliDiff = tapTime - secDiff * 1000 - minDiff * 60000;
                resultText.append("\n" + minDiff + " " + res.getString(R.string.minutes) + ", " +
                                   secDiff + " " + res.getString(R.string.seconds) + ", " +
                                   milliDiff + " " + res.getString(R.string.milliseconds));
            }
        }
        TextView lastResultTv = (TextView) root.findViewById(R.id.mainCompetition_tvLastResult);
        lastResultTv.setText(resultText);
    }

    @Override
    public void onTabVisible() {
        if (competing) {
            wakeLock.acquire();
        }
        visible = true;
    }

    @Override
    public void onTabInvisible() {
        if (competing) {
            wakeLock.release();
        }
        visible = false;
    }
}
