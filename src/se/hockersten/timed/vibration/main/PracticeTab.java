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

public class PracticeTab extends Fragment {
	private static String COUNTING = "COUNTING";
	private static String VIBRATE_ONCE_TASK = "VIBRATE_ONCE_TASK";
	private static String VIBRATE_TWICE_TASK = "VIBRATE_TWICE_TASK";
	private static String SPIN_SINGLE_POS = "SPIN_SINGLE_POS";
	private static String SPIN_DOUBLE_POS = "SPIN_DOUBLE_POS";

	private View root;
	private boolean counting = false;
	private PendingIntent vibrateOnceTask;
	private PendingIntent vibrateTwiceTask;
	private int spinSinglePos = 1; // 1 minute by default
	private int spinDoublePos = 3; // 5 minutes by default 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			counting = savedInstanceState.getBoolean(COUNTING);
			vibrateOnceTask = savedInstanceState.getParcelable(VIBRATE_ONCE_TASK);
			vibrateTwiceTask = savedInstanceState.getParcelable(VIBRATE_TWICE_TASK);
			spinSinglePos = savedInstanceState.getInt(SPIN_SINGLE_POS);
			spinDoublePos = savedInstanceState.getInt(SPIN_DOUBLE_POS);
		}

		root = inflater.inflate(R.layout.main_practice, container, false);

		Spinner spinner = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		Spinner spinner2 = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);

		updateUI();
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putBoolean(COUNTING, counting);
		b.putParcelable(VIBRATE_ONCE_TASK, vibrateOnceTask);
		b.putParcelable(VIBRATE_TWICE_TASK, vibrateTwiceTask);
		b.putInt(SPIN_SINGLE_POS, spinSinglePos);
		b.putInt(SPIN_DOUBLE_POS, spinDoublePos);
	}

	@Override
	public void onResume() {
		Button startBtn = (Button) getActivity().findViewById(R.id.mainPractice_btnStart);
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				counting = !counting;
				updateUI();
				if (counting) {
					startCounting();
				}
				else {
					stopCounting();
				}
			}
		});

		super.onResume();
	}
	
	/**
	 * Starts the vibrating counter
	 */
	public void startCounting() {
		Spinner spinSingle = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalSingle);
		Spinner spinDouble = (Spinner) root.findViewById(R.id.mainPractice_spinIntervalDouble);
		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

		// FIXME: lots of code that could potentially be reused here (but does it make it any clearer?)
		int singleMinutes = spinPosToMinutes(spinSingle.getSelectedItemPosition());
		if (singleMinutes != -1) {
			Calendar nextApplicableMinuteSingle = Calendar.getInstance();
			int nextSingleMinute = normalizedMinuteDelay(nextApplicableMinuteSingle.get(Calendar.MINUTE), spinPosToMinutes(singleMinutes));
			nextApplicableMinuteSingle.add(Calendar.MINUTE, nextSingleMinute);
			long delaySingle = nextSingleMinute *  60000;
			nextApplicableMinuteSingle.set(Calendar.SECOND, 0);
			nextApplicableMinuteSingle.set(Calendar.MILLISECOND, 0);
			long firstVibrationSingle = nextApplicableMinuteSingle.getTimeInMillis();
			Intent i1 = new Intent(getActivity(), VibrateOnce.class);
			vibrateOnceTask = PendingIntent.getBroadcast(getActivity(), 0, i1, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibrationSingle, delaySingle, vibrateOnceTask);
		}

		int doubleMinutes = spinPosToMinutes(spinDouble.getSelectedItemPosition());
		if (doubleMinutes != -1) {
			Calendar nextApplicableMinuteDouble = Calendar.getInstance();
			int nextDoubleMinute = normalizedMinuteDelay(nextApplicableMinuteDouble.get(Calendar.MINUTE), spinPosToMinutes(doubleMinutes));
			nextApplicableMinuteDouble.add(Calendar.MINUTE, nextDoubleMinute);
			long delayDouble = nextDoubleMinute *  60000;
			nextApplicableMinuteDouble.set(Calendar.SECOND, 0);
			nextApplicableMinuteDouble.set(Calendar.MILLISECOND, 0);
			long firstVibrationDouble = nextApplicableMinuteDouble.getTimeInMillis();
			Intent i2 = new Intent(getActivity(), VibrateTwice.class);
			vibrateTwiceTask = PendingIntent.getBroadcast(getActivity(), 0, i2, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibrationDouble, delayDouble, vibrateTwiceTask);
		}
		// DEBUG: Vibrate after 2 and 5 seconds instead, and then every 10 seconds (this won't work if fields are set to disabled)
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, 10000, vibrateOnceTask);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, 10000, vibrateTwiceTask);
	}

	/**
	 * Stops the vibrating counter
	 */
	public void stopCounting() {
		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		counting = false;
		alarmManager.cancel(vibrateOnceTask);
		alarmManager.cancel(vibrateTwiceTask);
		vibrateOnceTask = null;
		vibrateTwiceTask = null;
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
		spinSingle.setSelection(spinSinglePos);
		spinDouble.setSelection(spinDoublePos);
		if (counting) {
			startBtn.setText(R.string.stop_counting);
		}
		else {
			startBtn.setText(R.string.start_counting);
		}
	}

	private static int normalizedMinuteDelay(int currentMinute, int delay) {
		return delay - currentMinute % delay;
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
}
