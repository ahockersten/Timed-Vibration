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

public class VibrateOnce extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 0, 100 };
		vibrator.vibrate(pattern, -1);
	}
}
