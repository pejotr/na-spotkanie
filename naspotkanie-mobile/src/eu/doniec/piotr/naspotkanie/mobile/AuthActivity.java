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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

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
						new HttpAuthorizedRequest(new HttpHost(Const.HOST_NAME, Const.HOST_PORT), username, password));
				
				Log.i(NaSpotkanieApplication.APPTAG, 
						"Starting auth task [#username=" + username + ";#password[" + password +"]]");
			
				new AuthTask(AuthActivity.this).execute();
			}
		});
	}
	
	
	class AuthRegisterResponse {
		public long userId;
		public int statusCode;
		public String statusMessage;
	}
	
	protected class AuthTask extends AsyncTask<Void, Void, AuthRegisterResponse> {
		
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
		protected AuthRegisterResponse doInBackground(Void... params) {
			
			boolean isOnline = ((NaSpotkanieApplication)mContext.getApplication()).isOnline(); 

			AuthRegisterResponse arrError = new AuthRegisterResponse();
			arrError.statusCode = 904;
			
			if( !isOnline ) {
				Log.i(NaSpotkanieApplication.APPTAG, "Please turn on WIFI");
				return arrError;
			} else {
				arrError.statusCode = 404;	
			}
			
			HttpAuthorizedRequest req = ((NaSpotkanieApplication)mContext.getApplication()).getHttAuthorizedRequest();
			HttpPost post = new HttpPost("/AuthRegister");
			HttpEntity entity;
			try {
				
				entity = req.makeRequest(post);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(entity.getContent()));	
				String json = reader.readLine();
			
				Gson gson = new Gson();
				AuthRegisterResponse arr = gson.fromJson(json, AuthRegisterResponse.class);
				
				Log.i(NaSpotkanieApplication.APPTAG, "Received response [#statusCode=" + arr.statusCode + 
						" #statusMessage=" + arr.statusMessage + "]");
				return arr;
				
			} catch (Exception e) {
				Log.e(NaSpotkanieApplication.APPTAG, "Somethig gone wrong " + e.getMessage());
				return arrError;
			} 
		}
		
		@Override
		protected void onPostExecute(AuthRegisterResponse response) {
			mAuthProgess.dismiss();
			
			switch(response.statusCode) {
				case 904:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							"Cannot connect to network - please turn on WiFi", Toast.LENGTH_LONG).show();
					break;			
					
				case HttpAuthorizedRequest.HTTP_NOTFOUND:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							"Host not found", Toast.LENGTH_LONG).show();
					break;
			
				case HttpAuthorizedRequest.HTTP_OK:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							response.statusMessage, Toast.LENGTH_SHORT).show();
					
					SharedPreferences settings = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					HttpAuthorizedRequest req = ((NaSpotkanieApplication)mContext.getApplication()).getHttAuthorizedRequest();
					
					editor.putString("username", req.getUsername());
					editor.putString("password", req.getPassword());
					editor.putLong("id", response.userId);
					
					Log.i(NaSpotkanieApplication.APPTAG, 
							"Storing credentials [#username<" + req.getUsername() + "> #password<" + req.getPassword() +"> #id<" + response.userId + ">]");
					editor.commit();
					
					startActivity(new Intent(mContext, eu.doniec.piotr.naspotkanie.mobile.MeetingsListActivity.class) );
					break;
					
				default:
					Toast.makeText(AuthActivity.this.getApplicationContext(), 
							response.statusMessage, Toast.LENGTH_SHORT).show();
					break;
			}

		}		
	}	
}
