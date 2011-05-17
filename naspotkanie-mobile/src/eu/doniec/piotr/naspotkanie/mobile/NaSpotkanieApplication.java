package eu.doniec.piotr.naspotkanie.mobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;
import greendroid.app.GDApplication;

public class NaSpotkanieApplication extends GDApplication {
	
	public static final String APPTAG = "NaSpotkanie";
	public static final String PREFS  = "NaSpotkaniePrefs";
	
	protected HttpAuthorizedRequest mHttpAuthorizedRequest;
	

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
	
}
