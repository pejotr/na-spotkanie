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
				new HttpAuthorizedRequest(new HttpHost("192.168.10.153", 8888), "admin", "mypass"));
		
		// jesli nie ma ustawionego zadnego konta dla aplikacji to:
		startActivity(new Intent(this, eu.doniec.piotr.naspotkanie.mobile.AuthActivity.class) );
	}
	
}
