package eu.doniec.piotr.naspotkanie.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class AuthActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		HttpAuthorizedRequest req = 
			((NaSpotkanieApplication) this.getApplication()).getHttAuthorizedRequest();
		
		
		try {
			Log.i(NaSpotkanieApplication.APPTAG, "Creating request");
			HttpPost post = new HttpPost("/PositionLog");
			HttpEntity entity = req.makeRequest(post);
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			Log.i(NaSpotkanieApplication.APPTAG, reader.readLine());
			
			
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
		
	}
	
}
