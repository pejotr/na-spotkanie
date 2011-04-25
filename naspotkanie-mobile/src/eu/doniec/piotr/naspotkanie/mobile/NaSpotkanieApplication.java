package eu.doniec.piotr.naspotkanie.mobile;

import android.app.Application;

import eu.doniec.piotr.naspotkanie.mobile.util.*;

public class NaSpotkanieApplication extends Application {
	
	public static final String APPTAG = "NaSpotkanie"; 
	protected HttpAuthorizedRequest mHttpAuthorizedRequest;
	

	public void setHttpAuthorizedRequest(HttpAuthorizedRequest har) {
		mHttpAuthorizedRequest = har;
	}
	
	public HttpAuthorizedRequest getHttAuthorizedRequest() {
		return mHttpAuthorizedRequest;
	}
	
}
