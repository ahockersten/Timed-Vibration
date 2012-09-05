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
import android.content.Context;
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

public class CompetitionTab extends Fragment {
	private static final String COMPETING = "COMPETING";
	
	private View root;
	private boolean competing;
	private Calendar lastPress;
	private PowerManager.WakeLock wakeLock;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			competing = savedInstanceState.getBoolean(COMPETING);
		}

		root = inflater.inflate(R.layout.main_competition, container, false);
		updateUI();
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putBoolean(COMPETING, competing);
	}

	@Override
	public void onResume() {
		PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); // FIXME proper tag
		
		Button startBtn = (Button) root.findViewById(R.id.mainCompetition_btnStart);
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				competing = !competing;
				if (competing) {
					startCompetition();
				}
				else {
					stopCompetition();
				}
				updateUI();
			}
		});
		
		Button tapBtn = (Button) root.findViewById(R.id.mainCompetition_btnTap);
		tapBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				assert(!competing); // should be greyed out when not competing
				Calendar currentTime = Calendar.getInstance();
				
				long timeDiff = currentTime.getTimeInMillis() - lastPress.getTimeInMillis();
				TextView lastResultTv = (TextView) root.findViewById(R.id.mainCompetition_tvLastResult);
				long minDiff = timeDiff / 60000;
				long secDiff = timeDiff / 1000 - minDiff * 60;
				long milliDiff = timeDiff - secDiff * 1000 - minDiff * 60000;
				Resources res = getResources();
				lastResultTv.setText(res.getString(R.string.last_result) + " " + minDiff + " minutes, " + secDiff + " seconds, " + milliDiff + " milliseconds"); // FIXME magic strings
				
				lastPress = currentTime;
			}
		});
		super.onResume();
	}
	
	public void startCompetition() {
		 wakeLock.acquire();
		 lastPress = Calendar.getInstance();
	}
	
	public void stopCompetition() {
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
	}
}
