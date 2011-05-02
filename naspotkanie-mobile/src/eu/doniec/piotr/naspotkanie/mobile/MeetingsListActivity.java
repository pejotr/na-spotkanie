package eu.doniec.piotr.naspotkanie.mobile;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import eu.doniec.piotr.naspotkanie.mobile.view.MettingsListAdapter;

public class MeetingsListActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

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
	
}
