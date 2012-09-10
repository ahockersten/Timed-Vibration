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
import android.content.Context;
import android.content.SharedPreferences;
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
    // Constants used when saving state for this Fragment
    private static String COUNTING = "COUNTING";
    private static String SPIN_FIRST_POS = "SPIN_FIRST_POS";
    private static String SPIN_SECOND_POS = "SPIN_SECOND_POS";

    private View root;
    private boolean counting = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_practice, container, false);

        Spinner spinFirst = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalFirst);
        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFirst.setAdapter(adapter);

        Spinner spinSecond = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSecond);
        ArrayAdapter<CharSequence> adapter2 =
            ArrayAdapter.createFromResource(getActivity(), R.array.time_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSecond.setAdapter(adapter2);

        if (savedInstanceState != null) {
            counting = savedInstanceState.getBoolean(COUNTING);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putBoolean(COUNTING, counting);
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
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        Spinner spinFirst = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalFirst);
        spinFirst.setSelection(sharedPrefs.getInt(SPIN_FIRST_POS, 1)); // 1 minute by default
        Spinner spinSecond = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSecond);
        spinSecond.setSelection(sharedPrefs.getInt(SPIN_SECOND_POS, 3)); // 5 minutes by default
        updateUI();
        super.onResume();
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        Spinner spinFirst = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalFirst);
        prefsEditor.putInt(SPIN_FIRST_POS, spinFirst.getSelectedItemPosition());
        Spinner spinSecond = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSecond);
        prefsEditor.putInt(SPIN_SECOND_POS, spinSecond.getSelectedItemPosition());
        prefsEditor.commit();
        super.onStop();
    }

    public void flipCounting() {
        if (counting) {
            stopCounting();
        }
        else {
            startCounting();
        }
    }

    public void startCounting() {
        Spinner spinFirst = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalFirst);
        Spinner spinSecond = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSecond);
        counting = true;

        int firstMinutes = spinPosToMinutes(spinFirst.getSelectedItemPosition());
        Vibrate.setFirstVibrationInterval(firstMinutes);
        int secondMinutes = spinPosToMinutes(spinSecond.getSelectedItemPosition());
        Vibrate.setSecondVibrationInterval(secondMinutes);
        Vibrate.setEnabled(true, getActivity());
    }

    public void stopCounting() {
        counting = false;
        Vibrate.setEnabled(false, getActivity());
    }

    private void updateUI() {
        Button startBtn = (Button) root.findViewById(R.id.mainPractice_btnStart);
        Spinner spinFirst = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalFirst);
        Spinner spinSecond = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSecond);
        spinFirst.setEnabled(!counting);
        spinSecond.setEnabled(!counting);
        if (counting) {
            startBtn.setText(R.string.Stop_counting);
        }
        else {
            startBtn.setText(R.string.Start_counting);
        }
    }

    /**
     * Converts a position on the interval spinners to its value in minutes
     * @param spinPos The position on the spinner
     * @return The value of the spinner, in minutes, or 0 if disabled
     */
    private int spinPosToMinutes(int spinPos) {
        switch (spinPos) {
        case 0:
            return 0;
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
            return 0;
        }
    }

    @Override
    public void onTabVisible() {
        // need to check this because this function can be called during startup
        if (counting) {
            Vibrate.setEnabled(true, getActivity());
        }
    }

    @Override
    public void onTabInvisible() {
        // need to check this because this function can be called during startup
        if (counting) {
            Vibrate.setEnabled(false, getActivity());
        }
    }
}
