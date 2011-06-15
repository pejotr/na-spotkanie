package eu.doniec.piotr.naspotkanie.mobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import eu.doniec.piotr.naspotkanie.mobile.service.TrackingManager;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem.Type;

public class MainActivity extends GDActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.meetings);
		addActionBarItem(Type.Locate, R.id.action_bar_map);
		
		AlarmManager mgr =(AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(MainActivity.this, TrackingManager.class);
		PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, i, 0);
		mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000, 60000, pi);		
	}
	
}
