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
import java.util.LinkedList;

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

public class CompetitionTab extends Fragment implements Tab {
	private static final String COMPETING = "COMPETING";
	private static final String TAPTIMES = "TAPTIMES";
	
	private View root;
	private boolean competing;
	private Calendar lastPress;
	private PowerManager.WakeLock wakeLock;
	private LinkedList<Long> tapTimes;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		tapTimes = new LinkedList<Long>();
		if (savedInstanceState != null) {
			competing = savedInstanceState.getBoolean(COMPETING);
			long[] tempArray = savedInstanceState.getLongArray(TAPTIMES);
			for (int i = 0; i < tempArray.length; i++) {
				tapTimes.add(tempArray[i]);
			}
		}

		root = inflater.inflate(R.layout.main_competition, container, false);
		PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CompetitionTab.onResume()");
		updateUI();
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
		b.putBoolean(COMPETING, competing);
		long[] tempArray = new long[tapTimes.size()];
		for (int i = 0; i < tapTimes.size(); i++) {
			tempArray[i] = tapTimes.get(i);
		}
		b.putLongArray(TAPTIMES, tempArray);
	}

	@Override
	public void onResume() {
		Button startBtn = (Button) root.findViewById(R.id.mainCompetition_btnStart);
		startBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flipCompeting();
				updateUI();
			}
		});
		
		Button tapBtn = (Button) root.findViewById(R.id.mainCompetition_btnTap);
		tapBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				assert(!competing); // should be greyed out when not competing
				Calendar currentTime = Calendar.getInstance();
				
				long timeDiff = currentTime.getTimeInMillis() - lastPress.getTimeInMillis();
				if (tapTimes.size() > 4) {
					tapTimes.removeLast();
				}
				tapTimes.addFirst(timeDiff);
				lastPress = currentTime;
				
				updateUI();
			}
		});
		super.onResume();
	}
	
	public void flipCompeting() {
		if (competing) {
			stopCompetition();
		}
		else {
			startCompetition();
		}
	}
	
	public void startCompetition() {
		 wakeLock.acquire();
		 competing = true;
		 lastPress = Calendar.getInstance();
	}
	
	public void stopCompetition() {
		competing = false;
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
		Resources res = getResources();
		StringBuffer resultText = new StringBuffer(res.getString(R.string.last_results));
		for (long tapTime : tapTimes) {
			long minDiff = tapTime / 60000;
			long secDiff = tapTime / 1000 - minDiff * 60;
			long milliDiff = tapTime - secDiff * 1000 - minDiff * 60000;
			resultText.append("\n" + minDiff + " " + res.getString(R.string.minutes) + ", " + 
					 		  secDiff + " " + res.getString(R.string.seconds) + ", " + 
					 		  milliDiff + " " + res.getString(R.string.milliseconds));
		}
		TextView lastResultTv = (TextView) root.findViewById(R.id.mainCompetition_tvLastResult);
		lastResultTv.setText(resultText);
	}

	@Override
	public void onTabVisible() {
		if (competing) {
			wakeLock.acquire();
		}
	}

	@Override
	public void onTabInvisible() {
		if (competing) {
			wakeLock.release();
		}
	}
}
