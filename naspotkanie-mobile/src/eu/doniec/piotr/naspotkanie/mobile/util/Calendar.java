package eu.doniec.piotr.naspotkanie.mobile.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class Calendar {

	/**
	 * Represents of event attendees
	 * @author Piotr Doniec
	 */
	public static final class Attendee {
		
		public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/attendees");
		
		public String attendeeName;
		public String attendeeEmail;
			
		public String getAttendeeName() {
			return attendeeName;
		}
	
		public void setAttendeeName(String attendeeName) {
			this.attendeeName = attendeeName;
		}
	
		public String getAttendeeEmail() {
			return attendeeEmail;
		}
	
		public void setAttendeeEmail(String attendeeEmail) {
			this.attendeeEmail = attendeeEmail;
		}
	
		public static ArrayList<Calendar> getAllAttendees(ContentResolver resolver,  int eventId) {
			
			ArrayList<Calendar> attendees = new ArrayList<Calendar>();
			String[] projection = new String[] {"attendeeName", "attendeeEmail"};
			String[] selectionArgs = new String[] { Integer.toString(eventId) };
			
			Cursor cursor =  resolver.query(Calendar.Attendee.CONTENT_URI, projection, "_id = ?", selectionArgs, null);
			
			if (cursor != null && cursor.getCount() > 0) {
				
				int nameCol = cursor.getColumnIndex("attedeeName");
				int emailCol = cursor.getColumnIndex("attedeeEmail");
				
				do {
					
					Calendar.Attendee a = new Calendar.Attendee();
					a.setAttendeeName(cursor.getString(nameCol));
					a.setAttendeeEmail(cursor.getString(emailCol));
					
				} while(cursor.moveToNext());
			}
			return attendees;
		}	
	}
}
