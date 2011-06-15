package eu.doniec.piotr.naspotkanie.mobile;

import eu.doniec.piotr.naspotkanie.mobile.service.TrackingManager;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import eu.doniec.piotr.naspotkanie.mobile.view.MettingsListAdapter;
import greendroid.app.GDListActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MeetingsListActivity extends GDListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		addActionBarItem(Type.Trashcan, R.id.action_bar_stop);

		AlarmManager mgr =(AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(MeetingsListActivity.this, TrackingManager.class);
		PendingIntent pi = PendingIntent.getService(MeetingsListActivity.this, 0, i, 0);
		mgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000, 60000, pi);	
		
		ArrayList<Calendar.Event> events = Calendar.Event.getAllEvents(getContentResolver());
		this.setListAdapter(new MettingsListAdapter(this, R.layout.naspotkanie_image_2_rows, events));
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Calendar.Event e = (Calendar.Event)this.getListAdapter().getItem(position);
		
		Intent i = new Intent(this, eu.doniec.piotr.naspotkanie.mobile.MeetingDetailsActivity.class); 
		i.putExtra("event_id", e.getId());
		
		startActivity(i);	
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
        switch (item.getItemId()) {
        	case R.id.action_bar_map:
        		AlarmManager mgr =(AlarmManager)getSystemService(ALARM_SERVICE);
        		Intent i = new Intent(MeetingsListActivity.this, TrackingManager.class);
        		PendingIntent pi = PendingIntent.getService(MeetingsListActivity.this, 0, i, 0);
        		mgr.cancel(pi);
        		break;
            
        	default:
        		return super.onHandleActionBarItemClick(item, position);
        }
        
        return true;
	}
	
}
