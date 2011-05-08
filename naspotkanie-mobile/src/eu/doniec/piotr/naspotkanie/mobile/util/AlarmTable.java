package eu.doniec.piotr.naspotkanie.mobile.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import eu.doniec.piotr.naspotkanie.mobile.NaSpotkanieApplication;

public class AlarmTable {

	protected AlarmsOpenHelper mDBHelper;
	private static final String ALARMS_TABLE_NAME = "alarms";
	
	public AlarmTable(Context context) {
		mDBHelper = new AlarmsOpenHelper(context);
	}
	
	public void add(int eventId, long start, long stop, int valid) {
		ContentValues values = new ContentValues();
		values.put("id", eventId);
		values.put("start", start);
		values.put("stop", stop);
		values.put("valid", 1);
		
		mDBHelper.getWritableDatabase().insert("alarms", null, values);
		Log.i(NaSpotkanieApplication.APPTAG, "New row added to \"" + ALARMS_TABLE_NAME + "\" table");
	}
	
	public void update(int eventId, long start, long stop, int valid) {
		ContentValues values = new ContentValues();

		if( count(new String[] {"id"}, "id = ?", new String[] {Integer.toString(eventId)}) == 0 ) {
			add(eventId, start, stop, valid);
			Log.i(NaSpotkanieApplication.APPTAG, "Alarm for event does not exists yet");
			return;
		}
		
		values.put("start", start);
		values.put("stop", stop);
		values.put("valid", valid);
		
		mDBHelper.getWritableDatabase().update(ALARMS_TABLE_NAME, values, "id = ?", new String[]{ Integer.toString(eventId) });
		Log.i(NaSpotkanieApplication.APPTAG, "Table \"" + ALARMS_TABLE_NAME + "\" has been updated");
	}
	
	public int count(String[] columns, String selection, String[] selectionArgs ) {
		
		Cursor c = mDBHelper.getReadableDatabase().query(ALARMS_TABLE_NAME, columns, selection, 
								selectionArgs, null, null, null);
		
		int cnt = c.getCount();
		c.close();
		
		return cnt;
	}
	
	public Cursor getAll() {
		
		String[] columns = new String[] { "id", "start", "stop", "valid" };
		
		
		Cursor c = mDBHelper.getReadableDatabase().query(ALARMS_TABLE_NAME, columns, null, null, null, null, null);
		return c;
		
	}
	
	public void close() {
		mDBHelper.close();
	}
	
	public class AlarmsOpenHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "applicationdata";

		private static final int DATABASE_VERSION = 2;
	    private static final String ALARMS_TABLE_CREATE =
	                "create table alarms (id integer primary key,"
	    				+ "start timestamp not null, stop timestamp not null, valid integer);";

	    public AlarmsOpenHelper(Context context) {
	    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(NaSpotkanieApplication.APPTAG, "Creating database");
			db.execSQL(ALARMS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}

	}
	
	
}
