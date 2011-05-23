package eu.doniec.piotr.naspotkanie.mobile.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar.Attendee;

public class TrackingManager extends IntentService implements LocationListener {

	private static boolean mLocationManagerSet = false;
	private static boolean mAllowPostionSharing = false;
	private static double mLastKnownLat = 0.0;
	private static double mLastKnownLgt = 0.0;
	
	protected int	  mRunningEvents;
	protected boolean mIsTrackingServiceRunning;
	protected LocationManager mLocationManager;
	
	protected ArrayList<Integer> mSharingPositionEventsIds;
	
	public TrackingManager() {
		super("TrackingManager");
		Log.d(NaSpotkanieApplication.APPTAG, "TrackingManager: Creating service...");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(NaSpotkanieApplication.APPTAG, "TrackingManager: onCreate()");
		if(!mLocationManagerSet) {
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			mLocationManagerSet = true;
		}
		
		//mAllowPostionSharing = false;
		mRunningEvents = 0;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mRunningEvents = 0;
		
		AlarmTable tbl = new AlarmTable(this);
		Cursor c =  tbl.getValid();
		Time startTimestamp = new Time();
		Time stopTimestamp  = new Time();
		
		if (c != null && c.getCount() > 0 && c.moveToFirst()) {
			int idCol 	 = c.getColumnIndex("id");
			int startCol = c.getColumnIndex("start");
			int stopCol  = c.getColumnIndex("stop");
			
			long currentTime = System.currentTimeMillis();
			ArrayList<Integer> sharingPosEventsIds = new ArrayList<Integer>();
			
			do {
				Integer eventId = new Integer(c.getInt(idCol));
				startTimestamp.set(c.getLong(startCol) * 1000);
				stopTimestamp.set(c.getLong(stopCol) * 1000);
				
				if( startTimestamp.toMillis(true) < currentTime && stopTimestamp.toMillis(true) > currentTime  ) {
					Log.i(NaSpotkanieApplication.APPTAG, "TrackingManager: Location sharing trigger event [#startTime<" + startTimestamp.format("%d/%m/%Y %H:%M:%S") +"> " + 
							"#stopTime<" + stopTimestamp.format("%d/%m/%Y %H:%M:%S") + ">]");
					
					if( mSharingPositionEventsIds != null && mSharingPositionEventsIds.contains(eventId)) {
						mSharingPositionEventsIds.remove(eventId);
						sharingPosEventsIds.add(eventId);
					} else {
						Log.i(NaSpotkanieApplication.APPTAG, "TrackingManager: Adding new sharing relation");
						
						ArrayList<Attendee> attendees = Calendar.Attendee.getAll(getContentResolver(), eventId.intValue());
						ArrayList<String> emails = new ArrayList<String>();
						EnaDisSharingMessage m = new EnaDisSharingMessage();
						
						for(Attendee a : attendees) {
							emails.add(a.getAttendeeEmail());
						}
						
						String[] str = new String[emails.size()];
						emails.toArray(str);
						
						m.action = 1;
						m.attendees = str;
						
						new SetPositionSharing().execute(m);
					}
							
					mRunningEvents++;
					
				}
				
			} while(c.moveToNext());
			
			c.close();
			tbl.close();
			
			//for(Integer eventId : mSharingPositionEventsIds ) {
			//	new CancelPositionSharing().execute(new EnaDisSharingMessage());
			//}
			
			mSharingPositionEventsIds = sharingPosEventsIds;
			
			Log.i(NaSpotkanieApplication.APPTAG, "TrackingManager: mRunningEvents = " + mRunningEvents);
			
			if(mRunningEvents > 0) {
				mAllowPostionSharing = true;
				sendLastKnownPosition();
			} else {
				mAllowPostionSharing = false;
			}
		}
	}

	public void onLocationChanged(Location location) {
		Log.i(NaSpotkanieApplication.APPTAG, "TrackingManager: Location changed [#lat<" + location.getLatitude() + "> " +
				"#lgt<" + location.getLongitude() + ">]");
			
		if(mAllowPostionSharing) {
			StorePosition pos = new StorePosition();
			SharedPreferences prefs = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
			
			mLastKnownLat = pos.lat = location.getLatitude();
			mLastKnownLgt = pos.lgt = location.getLongitude();
			pos.username = prefs.getString("username", "");
			pos.id = prefs.getLong("id", -1);
			
			Log.i(NaSpotkanieApplication.APPTAG, "TrackingManager: Sending location to server");
			new SharePositionTask().execute(pos);
		}
	}

	public void sendLastKnownPosition() {
		StorePosition pos = new StorePosition();
		SharedPreferences prefs = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
		
		pos.lat = mLastKnownLat;
		pos.lgt = mLastKnownLgt;
		pos.username = prefs.getString("username", "");
		pos.id = prefs.getLong("id", -1);
		
		new SharePositionTask().execute(pos);
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
		
		@Override
		protected Boolean doInBackground(StorePosition... positions) {
			Gson gson 					= new Gson();
			HttpPost post 				= new HttpPost("/PositionLog");
			HttpAuthorizedRequest req 	= ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();

			try {
				StringEntity s = new StringEntity(gson.toJson(positions[0]));
				s.setContentEncoding("application/json");
				post.setEntity(s);
				
				req.makeRequest(post);
				return Boolean.TRUE;
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			} 
		}
		
		protected void onPostExecute(Boolean result) {			
			if(result == Boolean.FALSE) {
				Toast.makeText(TrackingManager.this, "Unable to update position", Toast.LENGTH_LONG);
				return;
			}
			
		}
	}
	
	class SetPositionSharing extends AsyncTask<EnaDisSharingMessage, Void, Boolean> {

		@Override
		protected Boolean doInBackground(EnaDisSharingMessage... params) {
			Gson gson 					= new Gson();
			HttpPost post 				= new HttpPost("/SetLog");
			HttpAuthorizedRequest req 	= ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();
			
			
			StringEntity s;
			try {
				s = new StringEntity(gson.toJson(params[0]));
				
				s.setContentEncoding("application/json");
				post.setEntity(s);
				req.makeRequest(post);
				
				return Boolean.TRUE;
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
		
	}
	
	class CancelPositionSharing extends AsyncTask<EnaDisSharingMessage, Void, Boolean> {

		@Override
		protected Boolean doInBackground(EnaDisSharingMessage... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	class StorePosition {
		double lgt;
		double lat;
		String username;
		long   id;
	}
	
	class EnaDisSharingMessage {
		int action;
		String username;
		String[] attendees;
	}
}
