package eu.doniec.piotr.naspotkanie.mobile.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import eu.doniec.piotr.naspotkanie.mobile.NaSpotkanieApplication;
import eu.doniec.piotr.naspotkanie.mobile.util.AlarmTable;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class TrackingService extends IntentService implements LocationListener {

	protected static boolean mIsRunning = false;
	protected SharedPreferences mPrefs;
	protected LocationManager mLocationManager;
	protected long			mStopTime;
	
	public TrackingService() {
		super("TrackingService");
		
		Log.d(NaSpotkanieApplication.APPTAG, "TrackingService: Service construction");
		
		
	}
	
	public void onCreate() {
		super.onCreate();
		
		mPrefs = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
		Log.d(NaSpotkanieApplication.APPTAG, "TrackingService: Service onCreate");
		
		if(!mIsRunning) {
			mIsRunning = true;
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.i(NaSpotkanieApplication.APPTAG, "IN MyTestService::onHandleIntent");
		
		AlarmTable tbl = new AlarmTable(this);
		Cursor c =  tbl.getAll();

		if (c != null && c.getCount() > 0 && c.moveToFirst()) {
			Time startTimestamp = new Time();
			Time stopTimestamp = new Time();
			
			int idCol 	 = c.getColumnIndex("id");
			int startCol = c.getColumnIndex("start");
			int stopCol  = c.getColumnIndex("stop");
			int validCol = c.getColumnIndex("valid");
			
			long currentTime = System.currentTimeMillis();
			
			do {
				int isValid = c.getInt(validCol);
				startTimestamp.set(c.getLong(startCol) * 1000);
				stopTimestamp.set(c.getLong(stopCol) * 1000);
				
				if(isValid == 1 && startTimestamp.toMillis(false) < currentTime + 1000 && startTimestamp.toMillis(false) > currentTime - 1000 ) {
					mStopTime = Math.max(stopTimestamp.toMillis(false), mStopTime);
					
					EnaDisSharing set = new EnaDisSharing();
					set.action = 1;
					set.emails = new String[] { "test@wp.pl", "ziomek@wp.pl", "qwerty@wp.pl" };
					
					new SettingsTask().execute(set);
					
				}
				
				Log.i(NaSpotkanieApplication.APPTAG, "TrackingService: Event from DB: [#id<" + c.getInt(idCol) + "> " +
																		"#startData<" + startTimestamp.format("%d/%m/%Y %H:%M:%S") +"> " + 
																		"#endDate<" + stopTimestamp.format("%d/%m/%Y %H:%M:%S") + "> " + 
																		"#valid<" + isValid +  ">]");
			} while(c.moveToNext());
			
		} else {
			Log.w(NaSpotkanieApplication.APPTAG, "TrackingService: Somethig is wrong with cursor");
		}
		
		tbl.close();
		c.close();
	}
	
	public void onLocationChanged(Location location) {
		long currentTime = System.currentTimeMillis();
				
		if( currentTime > mStopTime ) {
			Log.i(NaSpotkanieApplication.APPTAG, "TrackingService: Stopping logging service");
			stopSelf();
		} else {			
			Log.i(NaSpotkanieApplication.APPTAG, "TrackingService: Location changed - propagating to servers:" + location.getLatitude() + " | " + location.getLongitude());
			
			StorePosition pos = new StorePosition();
			pos.lat = location.getLatitude();
			pos.lgt = location.getLongitude();
			pos.username = mPrefs.getString("username", "");
			pos.id = mPrefs.getLong("id", -1);
			
			new SharePositionTask().execute(pos);
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
	
	class SharePositionTask extends AsyncTask<StorePosition, Void, Boolean> {
		
		HttpAuthorizedRequest req;
		
		@Override
		protected Boolean doInBackground(StorePosition... positions) {
			Gson gson = new Gson();
			req = ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();
			HttpPost post = new HttpPost("/PositionLog");
			
			StringEntity s;
			try {
				s = new StringEntity(gson.toJson(positions[0]));
				s.setContentEncoding("application/json");
				post.setEntity(s);
				
				req.makeRequest(post);
				return Boolean.TRUE;
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (IOException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (HttpException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} 
		}
		
		protected void onPostExecute(Boolean result) {			
			if(result == Boolean.FALSE) {
				Toast.makeText(TrackingService.this, "Unable to update position", Toast.LENGTH_LONG);
				return;
			}
			
		}
	}
	
	class SettingsTask extends AsyncTask<EnaDisSharing, Void, Boolean> {
		
		HttpAuthorizedRequest req;
		
		@Override
		protected Boolean doInBackground(EnaDisSharing... settings) {
			
			Gson gson = new Gson();
			req = ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();
			HttpPost post = new HttpPost("/SetLog");
			StringEntity s;
			
			try {
				s = new StringEntity(gson.toJson(settings[0]));
				s.setContentEncoding("application/json");
				post.setEntity(s);
				
				req.makeRequest(post);
				return Boolean.TRUE;
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (IOException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} catch (HttpException e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {

			if(result == Boolean.FALSE) {
				Toast.makeText(TrackingService.this, "Something gone wrong", Toast.LENGTH_LONG);
				return;
			} 
			
		}
		
	}
	
	class StorePosition {
		double lgt;
		double lat;
		String username;
		long   id;
	}
	
	static class EnaDisSharing {
		int action;
		String[] emails;
	}
	
}
