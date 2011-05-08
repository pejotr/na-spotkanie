package eu.doniec.piotr.naspotkanie.mobile.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import eu.doniec.piotr.naspotkanie.mobile.NaSpotkanieApplication;
import eu.doniec.piotr.naspotkanie.mobile.util.AlarmTable;

public class LoggingService extends IntentService implements LocationListener {

	LocationManager mLocationManager;
	long			mStopTime;
	
	public LoggingService() {
		super("SERVICE_PEJOTRA");
		// TODO Auto-generated constructor stub
	}
	
	public void onCreate() {
		super.onCreate();
		
		Log.d(NaSpotkanieApplication.APPTAG, "LoggingService - created");
		
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.i(NaSpotkanieApplication.APPTAG, "IN MyTestService::onHandleIntent");
		
		AlarmTable tbl = new AlarmTable(this);
		Cursor c =  tbl.getAll();

		if (c != null && c.getCount() > 0 && c.moveToFirst()) {
			Time startTimestamp = new Time();
			Time stopTimestamp = new Time();
			
			int idCol = c.getColumnIndex("id");
			int startCol = c.getColumnIndex("start");
			int stopCol = c.getColumnIndex("stop");
			int validCol = c.getColumnIndex("valid");
			
			long currentTime = System.currentTimeMillis();
			
			do {
				int isValid = c.getInt(validCol);
				startTimestamp.set(c.getLong(startCol) * 1000);
				stopTimestamp.set(c.getLong(stopCol) * 1000);
				
				if(isValid == 1 && startTimestamp.toMillis(false) <= currentTime + 1000 ) {
					mStopTime = Math.max(stopTimestamp.toMillis(false), mStopTime);
				}
				
				Log.i(NaSpotkanieApplication.APPTAG, "Event from DB: [#id<" + c.getInt(idCol) + "> " +
																		"#startData<" + startTimestamp.format("%d/%m/%Y %H:%M:%S") +"> " + 
																		"#endDate<" + stopTimestamp.format("%d/%m/%Y %H:%M:%S") + "> " + 
																		"#valid<" + isValid +  ">]");
			} while(c.moveToNext());
			
		} else {
			Log.w(NaSpotkanieApplication.APPTAG, "MyTestService > Somethig is wrong with cursor");
			tbl.close();
			c.close();
			return;
		}
		
		tbl.close();
		c.close();
	}

	public void onLocationChanged(Location location) {
		long currentTime = System.currentTimeMillis();
				
		if( currentTime > mStopTime ) {
			Log.i(NaSpotkanieApplication.APPTAG, "LoggingService >> Stopping logging service");
			stopSelf();
		} else {
			Log.i(NaSpotkanieApplication.APPTAG, "Location changed - propagating to servers:" + location.getLatitude() + " | " + location.getLongitude());
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
		
}
