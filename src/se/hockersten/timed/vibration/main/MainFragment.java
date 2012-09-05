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

import se.hockersten.timed.vibration.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainFragment extends Fragment implements OnTabChangeListener {
    private static final String TAB_PRACTICE = "PRACTICE";
    private static final String TAB_COMPETITION = "COMPETITION";

    private View root;
    private TabHost host;
    private int currentTab;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
		}

		root = inflater.inflate(R.layout.main_fragment, container, false);
		
        host = (TabHost) root.findViewById(android.R.id.tabhost);

        host.setup();
        host.addTab(newTab(TAB_PRACTICE, "Practice", R.id.mainFragment_tabPractice)); // FIXME magic string 
        host.addTab(newTab(TAB_COMPETITION, "Competition", R.id.mainFragment_tabCompetition)); // FIXME magic string

		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle b) {
		super.onSaveInstanceState(b);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        
        host.setOnTabChangedListener(this);
		host.setCurrentTab(currentTab);
        updateTab(TAB_PRACTICE, R.id.mainFragment_tabPractice);
		updateTab(TAB_COMPETITION, R.id.mainFragment_tabCompetition);
    }
    
    private TabSpec newTab(String tag, String labelId, int tabContentId) {
        TabSpec tabSpec = host.newTabSpec(tag);
        tabSpec.setIndicator(labelId);
        tabSpec.setContent(tabContentId);
        return tabSpec;
    }

    @Override
    public void onTabChanged(String tabId) {
        if (TAB_PRACTICE.equals(tabId)) {
            updateTab(tabId, R.id.mainFragment_tabPractice);
            currentTab = 0;
            return;
        }
        if (TAB_COMPETITION.equals(tabId)) {
            updateTab(tabId, R.id.mainFragment_tabCompetition);
            currentTab = 1;
            return;
        }
    }
 
    private void updateTab(String tabId, int placeholder) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(tabId) == null) {
        	if (TAB_PRACTICE.equals(tabId)) {
                fm.beginTransaction()
                	.replace(placeholder, new PracticeTab(), tabId)
                	.commit();
        	}
        	if (TAB_COMPETITION.equals(tabId)) {
                fm.beginTransaction()
                	.replace(placeholder, new CompetitionTab(), tabId)
                	.commit();
        	}
        }
    }
}
