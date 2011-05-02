package eu.doniec.piotr.naspotkanie.mobile.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class Calendar {

	public static final class Event {
		
		public static final Uri CONTENT_URI = Uri.parse("content://com.android.calendar/events");
		
		protected int 		mId;
		protected String 	mTitle;
		protected String	mDescription;
		protected int		mAttendeesCnt;

		public int getId() {
			return mId;
		}

		public void setId(int mId) {
			this.mId = mId;
		}

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String mTitle) {
			this.mTitle = mTitle;
		}

		public String getDescription() {
			return mDescription;
		}

		public void setDescription(String mDescription) {
			this.mDescription = mDescription;
		}
		
		public int getAttendeesCnt() {
			return mAttendeesCnt;
		}

		public void setAttendeesCnt(int mAttendeesCnt) {
			this.mAttendeesCnt = mAttendeesCnt;
		}

		public static ArrayList<Event> getAllEvents(ContentResolver resolver) {
			
			ArrayList<Event> events = new ArrayList<Event>();
			String[] projection = new String[] {"_id","title", "description"};
			
			Cursor cursor =  resolver.query(Calendar.Event.CONTENT_URI, projection, null, null, null);
			
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				
				int idCol = cursor.getColumnIndex("_id");
				int titleCol = cursor.getColumnIndex("title");
				int descCol = cursor.getColumnIndex("description");
				
				do {
					
					Calendar.Event e = new Calendar.Event();
					
					e.setId(cursor.getInt(idCol));
					e.setTitle(cursor.getString(titleCol));
					e.setDescription(cursor.getString(descCol));
					e.setAttendeesCnt(Calendar.Attendee.getCount(resolver, cursor.getInt(idCol)));
					
					events.add(e);
					
				} while(cursor.moveToNext());
			}
			
			return events;
			
		}
		
	}
	
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
	
		public static ArrayList<Attendee> getAll(ContentResolver resolver,  int eventId) {
			
			ArrayList<Attendee> attendees = new ArrayList<Attendee>();
			String[] projection = new String[] {"_id", "attendeeName", "attendeeEmail"};
			String[] selectionArgs = new String[] { Integer.toString(eventId) };
			
			Cursor cursor =  resolver.query(Calendar.Attendee.CONTENT_URI, projection, "event_id = ?", selectionArgs, null);
			
			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				
				int nameCol = cursor.getColumnIndex("attendeeName");
				int emailCol = cursor.getColumnIndex("attendeeEmail");
				
				do {
					
					Calendar.Attendee a = new Calendar.Attendee();
					
					a.setAttendeeName(cursor.getString(nameCol));
					a.setAttendeeEmail(cursor.getString(emailCol));
					attendees.add(a);
					
				} while(cursor.moveToNext());
			}
			
			return attendees;
			
		}	
		
		public static int getCount(ContentResolver resolver,  int eventId) {
			
			String[] projection = new String[] {"_id"};
			String[] selectionArgs = new String[] { Integer.toString(eventId) };
			
			Cursor cursor =  resolver.query(Calendar.Attendee.CONTENT_URI, projection, "event_id = ?", selectionArgs, null);
			int cnt =  cursor.getCount();
			return cnt;
		}
	}
}
