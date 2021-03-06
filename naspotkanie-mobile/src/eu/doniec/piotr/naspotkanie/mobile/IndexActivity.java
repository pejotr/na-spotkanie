package eu.doniec.piotr.naspotkanie.mobile;

import org.apache.http.HttpHost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class IndexActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((NaSpotkanieApplication) this.getApplication()).setHttpAuthorizedRequest(
				new HttpAuthorizedRequest(new HttpHost(Const.HOST_NAME, Const.HOST_PORT), "none", "none"));
		
		SharedPreferences settings = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");
		long id			= settings.getLong("id", -1);
		
		id = -1;
		
		if( username.equals("") || password.equals("") || id == -1 ) {
			Log.i(NaSpotkanieApplication.APPTAG, "Credentials han not been found");
			startActivity(new Intent(this, eu.doniec.piotr.naspotkanie.mobile.AuthActivity.class) );	
		} else {
			Log.i(NaSpotkanieApplication.APPTAG, 
					"Read credentials [#username=" + username + ";#password[" + password +"]]");
			startActivity(new Intent(this, eu.doniec.piotr.naspotkanie.mobile.MeetingsListActivity.class) );
		}
	}	
}
