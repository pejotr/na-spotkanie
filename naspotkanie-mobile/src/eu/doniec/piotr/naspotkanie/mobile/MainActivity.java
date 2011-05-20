package eu.doniec.piotr.naspotkanie.mobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import eu.doniec.piotr.naspotkanie.mobile.service.TrackingManager;

public class MainActivity extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		AlarmManager mgr =(AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(MainActivity.this, TrackingManager.class);
		PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, i, 0);
		mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000, 60000, pi);
		
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
