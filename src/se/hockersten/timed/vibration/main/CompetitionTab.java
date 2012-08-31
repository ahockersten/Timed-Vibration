package se.hockersten.timed.vibration.main;


import se.hockersten.timed.vibration.R;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CompetitionTab extends Fragment {
	private View root;
	private boolean competing;
	private PowerManager.WakeLock wakeLock;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
		}

		root = inflater.inflate(R.layout.main_competition, container, false);
		updateUI();
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
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
		super.onResume();
	}
	
	public void startCompetition() {
		 wakeLock.acquire();
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
