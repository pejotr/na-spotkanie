package eu.doniec.piotr.naspotkanie.mobile;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		/*
		 * Button b = (Button)findViewById(R.id.startButton);
		 * 
		 * 
		 * b.setOnClickListener(new OnClickListener() {
		 * 
		 * public void onClick(View v) { java.util.Calendar calendar =
		 * java.util.Calendar.getInstance();
		 * calendar.setTimeInMillis(System.currentTimeMillis());
		 * calendar.add(java.util.Calendar.SECOND, 10);
		 * 
		 * Intent i = new Intent(SettingsActivity.this, LoggingService.class);
		 * PendingIntent pi = PendingIntent.getService(SettingsActivity.this, 0,
		 * i, 0); AlarmManager manager =
		 * (AlarmManager)getSystemService(ALARM_SERVICE);
		 * 
		 * manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pi);
		 * 
		 * } });
		 */

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.
				//Log.i(NaSpotkanieApplication.APPTAG, "TETTTETETET");
				Toast.makeText(SettingsActivity.this, "LOCATION CHANGED",
						Toast.LENGTH_SHORT).show();
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		//Location ll = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		//Toast.makeText(this, "LAST LOC IS " + ll.getAltitude() , Toast.LENGTH_SHORT).show();

	}

}
