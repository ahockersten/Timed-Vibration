package se.hockersten.timed.vibration.main;

import java.util.Calendar;

import se.hockersten.timed.vibration.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainFragment extends Fragment {
	private boolean counting = false;
	private PendingIntent vibrateOnceTask;
	private PendingIntent vibrateTwiceTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState == null) {
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
		
		return v;
	}
	
	@Override
	public void onResume() {
		Button startBtn = (Button) getActivity().findViewById(R.id.btnStart);
		
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button self = (Button) v;
				counting = !counting;

				if (counting) {
					self.setText(R.string.stop_counting);
					Spinner spinSingle = (Spinner) getActivity().findViewById(R.id.spinIntervalSingle);
					Spinner spinDouble = (Spinner) getActivity().findViewById(R.id.spinIntervalDouble);
					spinSingle.setEnabled(false);
					spinDouble.setEnabled(false);

					Calendar nextApplicableMinuteSingle = Calendar.getInstance();
					Calendar nextApplicableMinuteDouble = Calendar.getInstance();
					int nextSingleMinute = normalizedMinuteDelay(nextApplicableMinuteSingle.get(Calendar.MINUTE), spinPosToMinutes(spinSingle.getSelectedItemPosition()));
					int nextDoubleMinute = normalizedMinuteDelay(nextApplicableMinuteDouble.get(Calendar.MINUTE), spinPosToMinutes(spinDouble.getSelectedItemPosition()));
					nextApplicableMinuteSingle.add(Calendar.MINUTE, nextSingleMinute);
					nextApplicableMinuteDouble.add(Calendar.MINUTE, nextDoubleMinute);
					long delaySingle = nextSingleMinute *  60000;
					long delayDouble = nextDoubleMinute *  60000;
					nextApplicableMinuteSingle.set(Calendar.SECOND, 0);
					nextApplicableMinuteSingle.set(Calendar.MILLISECOND, 0);
					nextApplicableMinuteDouble.set(Calendar.SECOND, 0);
					nextApplicableMinuteDouble.set(Calendar.MILLISECOND, 0);
					
					long firstVibrationSingle = nextApplicableMinuteSingle.getTimeInMillis();
					long firstVibrationDouble = nextApplicableMinuteDouble.getTimeInMillis();
					
					AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					Intent i1 = new Intent(getActivity(), VibrateOnce.class);
					Intent i2 = new Intent(getActivity(), VibrateTwice.class);
					
					vibrateOnceTask = PendingIntent.getBroadcast(getActivity(), 0, i1, 0);
					vibrateTwiceTask = PendingIntent.getBroadcast(getActivity(), 0, i2, 0);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibrationSingle, delaySingle, vibrateOnceTask);
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstVibrationDouble, delayDouble, vibrateTwiceTask);
					// DEBUG: Vibrate after 2 and 5 seconds instead
					//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, delaySingle, vibrateOnceTask);
					//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, delayDouble, vibrateTwiceTask);
				}
				else {
					self.setText(R.string.start_counting);
					Spinner spinSingle = (Spinner) getActivity().findViewById(R.id.spinIntervalSingle);
					Spinner spinDouble = (Spinner) getActivity().findViewById(R.id.spinIntervalDouble);
					spinSingle.setEnabled(true);
					spinDouble.setEnabled(true);

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
