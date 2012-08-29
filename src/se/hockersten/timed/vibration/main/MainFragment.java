package se.hockersten.timed.vibration.main;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.hockersten.timed.vibration.R;
import android.content.Context;
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
	private ScheduledThreadPoolExecutor executor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		executor = new ScheduledThreadPoolExecutor(2); // 2 should be enough for now
		return inflater.inflate(R.layout.main_fragment, container, false);
	}
	
	@Override
	public void onResume() {
		Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinIntervalSingle);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		Spinner spinner2 = (Spinner) getActivity().findViewById(R.id.spinIntervalDouble);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
		        R.array.time_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner2.setAdapter(adapter2);
		
		Button startBtn = (Button) getActivity().findViewById(R.id.btnStart);
		
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button self = (Button) v;
				counting = !counting;
				
				if (counting) {
					self.setText("Stop counting"); // FIXME magic string
					Calendar nextApplicableMinuteSingle = Calendar.getInstance();
					Calendar nextApplicableMinuteDouble = Calendar.getInstance();
					Spinner spinSingle = (Spinner) getActivity().findViewById(R.id.spinIntervalSingle);
					Spinner spinDouble = (Spinner) getActivity().findViewById(R.id.spinIntervalDouble);
					
					int spinSingleMinute = spinPosToMinutes(spinSingle.getSelectedItemPosition());
					int spinDoubleMinute = spinPosToMinutes(spinDouble.getSelectedItemPosition());
					// FIXME this is wrong. It should match up to *even* places, like 10:00, 10:02, 10:05, 10:10, 10:30, 11:00 etc
					nextApplicableMinuteSingle.roll(Calendar.MINUTE, spinSingleMinute); // FIXME check so this works correctly for 1 hour case
					// FIXME this is wrong. It should match up to *even* places, like 10:00, 10:02, 10:05, 10:10, 10:30, 11:00 etc
					nextApplicableMinuteDouble.roll(Calendar.MINUTE, spinDoubleMinute); // FIXME check so this works correctly for 1 hour case
					long delaySingle = spinSingleMinute *  60000;
					long delayDouble = spinDoubleMinute *  60000;
					nextApplicableMinuteSingle.set(Calendar.SECOND, 0);
					nextApplicableMinuteSingle.set(Calendar.MILLISECOND, 0);
					nextApplicableMinuteDouble.set(Calendar.SECOND, 0);
					nextApplicableMinuteDouble.set(Calendar.MILLISECOND, 0);
					
					Calendar now = Calendar.getInstance();
					long firstDelaySingle = nextApplicableMinuteSingle.getTimeInMillis() - now.getTimeInMillis();
					long firstDelayDouble = nextApplicableMinuteDouble.getTimeInMillis() - now.getTimeInMillis();
					
					executor.scheduleAtFixedRate(new VibrateOnce(), firstDelaySingle, delaySingle, TimeUnit.MILLISECONDS);
					executor.scheduleAtFixedRate(new VibrateTwice(), firstDelayDouble, delayDouble, TimeUnit.MILLISECONDS);
				}
				else {
					self.setText("Start counting"); // FIXME magic string
					executor.shutdown();
					executor = new ScheduledThreadPoolExecutor(2);
				}
			}
		});
		
		super.onResume();
	}
	
	private int spinPosToMinutes(int spinPos) {
		switch (spinPos) {
		case 0:
	        // 1 minute
			return 1;
		case 1:
			// 2 minutes
			return 2;
		case 2:
			// 5 minutes
			return 5;
		case 3:
			// 10 minutes
			return 10;
		case 4:
			// 15 minutes
			return 15;
		case 5:
			return 30;
		case 6:
			return 60;
		default:
			// how the fuck did we end up here?
			assert(false);
			return -1;
		}
	}
	
	private class VibrateOnce implements Runnable {
		@Override
		public void run() {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 100 };
			vibrator.vibrate(pattern, -1);
		}
	}
	
	private class VibrateTwice implements Runnable {
		@Override
		public void run() {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 100, 100, 100 };
			vibrator.vibrate(pattern, -1);
		}
	}
}
