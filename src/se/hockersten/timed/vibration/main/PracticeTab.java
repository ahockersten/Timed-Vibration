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
    private static String SPIN_SINGLE_POS = "SPIN_SINGLE_POS";
    private static String SPIN_DOUBLE_POS = "SPIN_DOUBLE_POS";

    private View root;
    private boolean counting = false;

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
            spinSingle.setSelection(savedInstanceState.getInt(SPIN_SINGLE_POS));
            spinDouble.setSelection(savedInstanceState.getInt(SPIN_DOUBLE_POS));
        }
        else {
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
            Vibrate.setSingleVibrationInterval(singleMinutes);
        }
        int doubleMinutes = spinPosToMinutes(spinDouble.getSelectedItemPosition());
        if (doubleMinutes != -1) {
            Vibrate.setDoubleVibrationInterval(doubleMinutes);
        }
        Vibrate.setEnabled(true, getActivity());
    }

    /**
     * Stops the vibrating counter
     */
    public void stopCounting() {
        counting = false;
        Vibrate.setEnabled(false, getActivity());
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
