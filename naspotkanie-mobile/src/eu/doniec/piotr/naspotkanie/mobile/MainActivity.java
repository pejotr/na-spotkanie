package eu.doniec.piotr.naspotkanie.mobile;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import eu.doniec.piotr.naspotkanie.mobile.service.TrackingManager;

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meetings);
		
		AlarmManager mgr =(AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(MainActivity.this, TrackingManager.class);
		PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, i, 0);
		mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000, 60000, pi);		
	}
	
}
