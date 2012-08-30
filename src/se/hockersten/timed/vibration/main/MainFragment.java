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

// TODO
// add Swedish translation
// add configuration of vibrations
// beautify the UI more
// add feature: active time measurement (aka "competition mode")

public class MainFragment extends Fragment {
	private static String COUNTING = "COUNTING";
	private static String VIBRATE_ONCE_TASK = "VIBRATE_ONCE_TASK";
	private static String VIBRATE_TWICE_TASK = "VIBRATE_TWICE_TASK";
	private static String SPIN_SINGLE_POS = "SPIN_SINGLE_POS";
	private static String SPIN_DOUBLE_POS = "SPIN_DOUBLE_POS";
	
	private boolean counting = false;
	private PendingIntent vibrateOnceTask;
	private PendingIntent vibrateTwiceTask;
	private int spinSinglePos = 1;
	private int spinDoublePos = 3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			counting = savedInstanceState.getBoolean(COUNTING);
			vibrateOnceTask = savedInstanceState.getParcelable(VIBRATE_ONCE_TASK);
			vibrateTwiceTask = savedInstanceState.getParcelable(VIBRATE_TWICE_TASK);
			spinSinglePos = savedInstanceState.getInt(SPIN_SINGLE_POS);
			spinDoublePos = savedInstanceState.getInt(SPIN_DOUBLE_POS);
		}
		
		View v = inflater.inflate(R.layout.main_fragment, container, false);
		
		Spinner spinner = (Spinner) v.findViewById(R.id.spinIntervalSingle);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		Spinner spinner2 = (Spinner) v.findViewById(R.id.spinIntervalDouble);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner2.setAdapter(adapter2);
		
		updateUI(v);
		
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle b) {
		b.putBoolean(COUNTING, counting);
		b.putParcelable(VIBRATE_ONCE_TASK, vibrateOnceTask);
		b.putParcelable(VIBRATE_TWICE_TASK, vibrateTwiceTask);
		b.putInt(SPIN_SINGLE_POS, spinSinglePos);
		b.putInt(SPIN_DOUBLE_POS, spinDoublePos);
	}
	
	@Override
	public void onResume() {
		Button startBtn = (Button) getActivity().findViewById(R.id.btnStart);
		
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Spinner spinSingle = (Spinner) getActivity().findViewById(R.id.spinIntervalSingle);
				Spinner spinDouble = (Spinner) getActivity().findViewById(R.id.spinIntervalDouble);
				counting = !counting;

				if (counting) {
					updateUI(getActivity().findViewById(android.R.id.content));
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
				else {
					updateUI(getActivity().findViewById(android.R.id.content));
					AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					alarmManager.cancel(vibrateOnceTask);
					alarmManager.cancel(vibrateTwiceTask);
					vibrateOnceTask = null;
					vibrateTwiceTask = null;
				}
			}
		});
		
		super.onResume();
	}
	
	private void updateUI(View v) {
		Button startBtn = (Button) v.findViewById(R.id.btnStart);
		Spinner spinSingle = (Spinner) v.findViewById(R.id.spinIntervalSingle);
		Spinner spinDouble = (Spinner) v.findViewById(R.id.spinIntervalDouble);
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
	
	private int normalizedMinuteDelay(int currentMinute, int delay) {
		return delay - currentMinute % delay;
	}
	
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
			// how the fuck did we end up here?
			assert(false);
			return -1;
		}
	}
}
