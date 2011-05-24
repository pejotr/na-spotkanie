package eu.doniec.piotr.naspotkanie.mobile;

import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar.Attendee;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;
import greendroid.app.GDApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NaSpotkanieApplication extends GDApplication {
	
	public static final String APPTAG = "NaSpotkanie";
	public static final String PREFS  = "NaSpotkaniePrefs";
	
	protected HttpAuthorizedRequest mHttpAuthorizedRequest;
	Map<String, Calendar.Attendee> mAttendeesPositions = new HashMap<String, Calendar.Attendee>();
	

	public void setHttpAuthorizedRequest(HttpAuthorizedRequest har) {
		mHttpAuthorizedRequest = har;
	}
	
	public HttpAuthorizedRequest getHttAuthorizedRequest() {
		return mHttpAuthorizedRequest;
	}
	
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;

	}
	
	public void updateAttendeesMap(ArrayList<Attendee> attendees) {
		
		for(Attendee a : attendees) {
			
			if(mAttendeesPositions.containsKey(a.getAttendeeEmail())) {
				
				if(a.getLattitude() == 0.0 || a.getLongitude() == 0.0) {
					continue;	
				}
				
			} 
			
			mAttendeesPositions.put(a.getAttendeeEmail(), a);
			
		}
		
	}
	
}
