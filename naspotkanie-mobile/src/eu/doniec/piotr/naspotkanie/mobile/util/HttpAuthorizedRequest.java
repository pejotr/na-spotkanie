package eu.doniec.piotr.naspotkanie.mobile.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;
import eu.doniec.piotr.naspotkanie.mobile.NaSpotkanieApplication;

public class HttpAuthorizedRequest {
	
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_OK = 200;
	public static final int HTTP_NOTFOUND = 404;
	public static final int HTTP_NOTIMPLEMENTED = 501;
	
	protected DefaultHttpClient mHttpClient = new DefaultHttpClient();
	protected HttpHost mTargetHost;
	protected BasicHttpContext mContext; 
	protected String mUsername;
	protected String mPassword;
	
	
	public HttpAuthorizedRequest(HttpHost targetHost, String username, String password) {
		
		mUsername = username;
		mPassword = password;
		mTargetHost = targetHost;
		
		mHttpClient.getCredentialsProvider().setCredentials(
				new AuthScope(targetHost.getHostName(), targetHost.getPort()), 
				new UsernamePasswordCredentials(username, password));
		
		mContext = new BasicHttpContext();
		BasicScheme basicAuth = new BasicScheme();
		
		mContext.setAttribute("preemptive-auth", basicAuth);
		mHttpClient.addRequestInterceptor(new PreemptiveAuthInceptor(), 0);
		
	}
	
	public HttpHost getTargetHost() {
		return mTargetHost;
	}

	public void setTargetHost(HttpHost mTargetHost) {
		this.mTargetHost = mTargetHost;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String mUsername) {
		this.mUsername = mUsername;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public HttpEntity makeRequest(HttpPost httppost) 
			throws ClientProtocolException, IOException, HttpException {
		HttpResponse resp = mHttpClient.execute(mTargetHost, httppost, mContext);
		
		if( resp.getStatusLine().getStatusCode() == HTTP_UNAUTHORIZED) {
			Log.w(NaSpotkanieApplication.APPTAG, "Request rejected - error 401");
			throw new HttpException("Unauthorized request !!!");
		}
		
		HttpEntity entity = resp.getEntity();
		return entity;
	}
	
	public void close() {
		mHttpClient.getConnectionManager().shutdown();
	}
	
	private static class PreemptiveAuthInceptor implements HttpRequestInterceptor {

		public void process(HttpRequest request, HttpContext context)
				throws HttpException, IOException {
			
			AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);
			
			if(authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
						ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(
						ExecutionContext.HTTP_TARGET_HOST);

				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(
							new AuthScope(
									targetHost.getHostName(), 
									targetHost.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

			
		}
		
	}
	
}
