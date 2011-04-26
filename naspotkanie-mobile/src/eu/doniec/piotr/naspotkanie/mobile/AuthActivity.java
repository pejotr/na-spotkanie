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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;

public class AuthActivity extends Activity {

	private ProgressDialog mAuthProgess;
	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			mAuthProgess.dismiss();
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerauth);
		
		
		Button submit = (Button)findViewById(R.id.btnSubmit);
		submit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				mAuthProgess = ProgressDialog.show(AuthActivity.this, "Authenticating...", 
						"Please wait, while authentiction is in progress");

				String username = ((EditText)AuthActivity.this.findViewById(R.id.etEmail)).getText().toString();
				String password = ((EditText)AuthActivity.this.findViewById(R.id.etPassword)).getText().toString();

				((NaSpotkanieApplication) AuthActivity.this.getApplication()).setHttpAuthorizedRequest(
						new HttpAuthorizedRequest(new HttpHost("192.168.10.153", 8888), username, password));
				
				Log.i(NaSpotkanieApplication.APPTAG, 
						"Starting auth task [#username=" + username + ";#password[" + password +"]]");
				
				Thread thread = new Thread(new AuthTask(AuthActivity.this));
				thread.start();
			}
		});
	}
		
	protected class AuthTask implements Runnable {
		private AuthActivity mContext;
		
		public AuthTask(AuthActivity context) {
			mContext = context;
		}
		
		public void run() {
			HttpAuthorizedRequest req = ((NaSpotkanieApplication)mContext.getApplication()).getHttAuthorizedRequest();
			HttpPost post = new HttpPost("/PositionLog");
			HttpEntity entity;
			try {
				entity = req.makeRequest(post);
				
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
			
			mHandler.sendEmptyMessage(0);
		}
		
	}
	
}
