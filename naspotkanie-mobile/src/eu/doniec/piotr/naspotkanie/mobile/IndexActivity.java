package eu.doniec.piotr.naspotkanie.mobile;

import org.apache.http.HttpHost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class IndexActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((NaSpotkanieApplication) this.getApplication()).setHttpAuthorizedRequest(
				new HttpAuthorizedRequest(new HttpHost("192.168.10.153", 8888), "test", "test"));
		
		//SharedPreferences settings = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
		//String username = settings.getString("username", "");
		//String password = settings.getString("password", "");
		
		//if( username.equals("") || password.equals("") ) {
		//	Log.i(NaSpotkanieApplication.APPTAG, "Credentials han not been found");
			// jesli nie ma ustawionego zadnego konta dla aplikacji to:
		//	startActivity(new Intent(this, eu.doniec.piotr.naspotkanie.mobile.AuthActivity.class) );	
		//} else {
		//	Log.i(NaSpotkanieApplication.APPTAG, 
		//			"Read credentials [#username=" + username + ";#password[" + password +"]]");
			// prztestuj polaczenie - jesli fail odpalic AuthActivity
			startActivity(new Intent(this, eu.doniec.piotr.naspotkanie.mobile.MainActivity.class) );
		//}
	}	
}
