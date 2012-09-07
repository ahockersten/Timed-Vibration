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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class Vibrate extends BroadcastReceiver {
	public static final String TIMES_TO_VIBRATE = "TIMES_TO_VIBRATE";
	private static boolean silenced = false;
	
	public static void setSilenced(boolean silenced) {
		Vibrate.silenced = silenced;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (silenced) {
			return;
		}
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		int times = intent.getIntExtra(TIMES_TO_VIBRATE, 1); // fuck this is not working
		long[] pattern = new long[times*2];
		pattern[0] = 0;
		for (int i = 1; i < times*2; i++) {
			pattern[i] = 100;
		}
		vibrator.vibrate(pattern, -1);
	}
}
