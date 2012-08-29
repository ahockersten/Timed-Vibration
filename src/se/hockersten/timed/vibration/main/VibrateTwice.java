package se.hockersten.timed.vibration.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;


public class VibrateTwice extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 0, 100, 100, 100 };
		vibrator.vibrate(pattern, -1);
	}
}
