package eu.doniec.piotr.naspotkanie.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class AuthActivity extends Activity {

	private ProgressDialog mAuthProgess;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerauth);
		
		
		Button submit = (Button)findViewById(R.id.btnSubmit);
		submit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				String username = ((EditText)AuthActivity.this.findViewById(R.id.etEmail)).getText().toString();
				String password = ((EditText)AuthActivity.this.findViewById(R.id.etPassword)).getText().toString();

				((NaSpotkanieApplication) AuthActivity.this.getApplication()).setHttpAuthorizedRequest(
						new HttpAuthorizedRequest(new HttpHost("192.168.10.153", 8888), username, password));
				
				Log.i(NaSpotkanieApplication.APPTAG, 
						"Starting auth task [#username=" + username + ";#password[" + password +"]]");
			
				new AuthTask(AuthActivity.this).execute();
			}
		});
	}
	
	protected class AuthTask extends AsyncTask<Void, Void, Integer> {
		
		protected Activity mContext;
		public static final int NO_NETWORK = 1001;
		
		public AuthTask(Activity ctx) {
			super();
			this.mContext = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mAuthProgess = ProgressDialog.show(AuthActivity.this, "Authenticating...", 
					"Please wait, while authentiction is in progress");
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			boolean isOnline = ((NaSpotkanieApplication)mContext.getApplication()).isOnline(); 
			
			if( !isOnline ) {
				return AuthTask.NO_NETWORK;
			}
			
			HttpAuthorizedRequest req = ((NaSpotkanieApplication)mContext.getApplication()).getHttAuthorizedRequest();
			HttpPost post = new HttpPost("/PositionLog");
			HttpEntity entity;
			try {
				
				entity = req.makeRequest(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent()));
				Log.i(NaSpotkanieApplication.APPTAG, reader.readLine());
				return HttpAuthorizedRequest.HTTP_OK;
				
			} catch (ClientProtocolException e) {
				return HttpAuthorizedRequest.HTTP_NOTIMPLEMENTED;
			} catch (IOException e) {
				return HttpAuthorizedRequest.HTTP_NOTIMPLEMENTED;
			} catch (HttpException e) {
				return HttpAuthorizedRequest.HTTP_UNAUTHORIZED;
			}
		}
		
		@Override
		protected void onPostExecute(Integer code) {
			mAuthProgess.dismiss();
			
			switch(code) {
				case AuthTask.NO_NETWORK:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							"Cannot connect to server - please turn on network", Toast.LENGTH_LONG).show();
					break;
			
				case HttpAuthorizedRequest.HTTP_OK:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							"Authorization OK", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(mContext, eu.doniec.piotr.naspotkanie.mobile.MainActivity.class) );
					break;
				default:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							"Authorization failed", Toast.LENGTH_SHORT).show();
					break;
			}

		}		
	}	
}
