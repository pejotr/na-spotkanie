package eu.doniec.piotr.naspotkanie.mobile;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class MeetingsListActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.meetings);
		
		/*
		String[] names = new String[] { "Linux", "Windows7", "Eclipse", "Suse",
				"Ubuntu", "Solaris", "Android", "iPhone"};
		*/

		/*
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));
		*/

		/*
		// Events retriving
		String[] projection = new String[] {"_id", "title"};
		Uri uri = Uri.parse("content://com.android.calendar/events");
		Cursor cursor =  getContentResolver().query(uri, projection, null, null, null);
		*/
		
		String[] projection = new String[] {"attendeeName", "attendeeEmail"};
		Uri uri = Uri.parse("content://com.android.calendar/attendees");
		Cursor cursor =  getContentResolver().query(uri, projection, null, null, null);
		
		ArrayList<String> cosik = new ArrayList<String>();
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String columnNames[] = cursor.getColumnNames();
			String value = "";
			
			do {
				value = "";

				for (String colName : columnNames) {
					value += colName + " = ";
					value += cursor.getString(cursor.getColumnIndex(colName))
			       + " ||";
				}

				Log.i("INFO : ", value);
				cosik.add(value);
			} while (cursor.moveToNext());

		}
		
		String[] calendars = new String[cosik.size()];
		cosik.toArray(calendars);
		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, calendars ));
		
	}
	
}
