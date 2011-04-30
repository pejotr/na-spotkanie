package eu.doniec.piotr.naspotkanie.mobile;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		intent = new Intent().setClass(this, SettingsActivity.class);	
		spec = tabHost.newTabSpec("settings").setIndicator("Settings", res.getDrawable(R.drawable.ic_tab_settings))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, MeetingsListActivity.class);	
		spec = tabHost.newTabSpec("meetings").setIndicator("Meetings", res.getDrawable(R.drawable.ic_tab_meetings))
				.setContent(intent);
		tabHost.addTab(spec);
		
		
	}
	
}
