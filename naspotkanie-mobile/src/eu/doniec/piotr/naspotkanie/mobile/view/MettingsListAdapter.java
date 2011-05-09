package eu.doniec.piotr.naspotkanie.mobile.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.doniec.piotr.naspotkanie.mobile.R;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar.Event;

public class MettingsListAdapter extends ArrayAdapter<Event> {

	protected ArrayList<Event> mEvents;
	protected Context mContext;
	
	public MettingsListAdapter(Context context, int textViewResourceId, ArrayList<Event> events) {
		super(context, textViewResourceId, events);
		
		mEvents = events;
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if( v == null ) {
			LayoutInflater inf =(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.naspotkanie_image_2_rows, null);
		}
		
		Event e = mEvents.get(position);
		
		if( e != null ) {
			TextView tt = (TextView)v.findViewById(R.id.FirstLine);
			tt.setText(e.getTitle());
			
			TextView tt2 = (TextView)v.findViewById(R.id.SecondLine);
			tt2.setText("Attendees: " + Integer.toString(e.getAttendeesCnt()));
		}
		
		return v;
		
	}
	
}
