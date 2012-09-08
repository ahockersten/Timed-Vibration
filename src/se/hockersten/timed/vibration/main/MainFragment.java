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
import android.content.res.Resources;
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
    private String currentTab = TAB_PRACTICE;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.main_fragment, container, false);
        host = (TabHost) root.findViewById(android.R.id.tabhost);

        host.setup();
        Resources res = getResources();
        host.addTab(newTab(TAB_PRACTICE, res.getString(R.string.Practice), R.id.mainFragment_tabPractice)); 
        host.addTab(newTab(TAB_COMPETITION, res.getString(R.string.Competition), R.id.mainFragment_tabCompetition));

		return root;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        
        host.setOnTabChangedListener(this);
		host.setCurrentTabByTag(currentTab);
		
        updateTab(currentTab);
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
            updateTab(tabId);
            currentTab = TAB_PRACTICE;
        }
        if (TAB_COMPETITION.equals(tabId)) {
            updateTab(tabId);
            currentTab = TAB_COMPETITION;
        }
    }
 
    private void updateTab(String tabId) {
        FragmentManager fm = getFragmentManager();
        Tab oldTab = (Tab) fm.findFragmentByTag(currentTab);
        Tab newTab = (Tab) fm.findFragmentByTag(tabId);
        if (oldTab != null && oldTab != newTab) {
        	oldTab.onTabInvisible();
        }
        if (newTab == null) {
        	if (TAB_PRACTICE.equals(tabId)) {
        		PracticeTab practiceTab = new PracticeTab(); 
                fm.beginTransaction()
                	.replace(R.id.mainFragment_tabPractice, practiceTab, tabId)
                	.commit();
                newTab = practiceTab;
        	}
        	if (TAB_COMPETITION.equals(tabId)) {
        		CompetitionTab competitionTab = new CompetitionTab(); 
                fm.beginTransaction()
                	.replace(R.id.mainFragment_tabCompetition, competitionTab, tabId)
                	.commit();
                newTab = competitionTab;
        	}
        }
        if (oldTab != newTab) {
        	newTab.onTabVisible();
        }
    }
}
