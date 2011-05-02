package eu.doniec.piotr.naspotkanie.mobile;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;

public class MeetingDetailsActivity extends MapActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meetingdetails);
		
		
		Intent i = getIntent();
		int eventId = i.getExtras().getInt("event_id");
		
		ArrayList<Calendar.Attendee> attendees =  Calendar.Attendee.getAll(getContentResolver(), eventId);
		ArrayList<String> attendeesNames  = new ArrayList<String>();
		
		for(Calendar.Attendee a : attendees) {
			attendeesNames.add(a.getAttendeeEmail());
		}
		
		ListView lv = (ListView)findViewById(R.id.attendeeList);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attendeesNames));
		
		TextView tv = (TextView)findViewById(R.id.text1);
		tv.setText(Integer.toString(eventId));
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
