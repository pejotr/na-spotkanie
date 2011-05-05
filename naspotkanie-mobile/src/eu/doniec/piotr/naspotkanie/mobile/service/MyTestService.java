package eu.doniec.piotr.naspotkanie.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import eu.doniec.piotr.naspotkanie.mobile.NaSpotkanieApplication;

public class MyTestService extends Service {

	@Override
	public void onCreate() {
	 // TODO Auto-generated method stub
	 Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
	 Log.i(NaSpotkanieApplication.APPTAG, "MyAlarmService.onCreate()");
	  
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
	 // TODO Auto-generated method stub
	 super.onStart(intent, startId);
	 Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
	 
	 try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
	 // TODO Auto-generated method stub
	 Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
	 return super.onUnbind(intent);
	}
	
}
