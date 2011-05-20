package eu.doniec.piotr.naspotkanie.mobile;

import eu.doniec.piotr.naspotkanie.mobile.service.TrackingService;
import eu.doniec.piotr.naspotkanie.mobile.util.AlarmTable;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MeetingDetailsActivity extends GDActivity {

	static final int DATE_FROM_DIALOG_ID = 000;
	static final int TIME_FROM_DIALOG_ID = 001;
	static final int DATE_TO_DIALOG_ID = 002;
	static final int TIME_TO_DIALOG_ID = 003;
	
	private int mEventId;
	private TextView mEventStartDatetimeValue;
	private CheckBox mAllowLogging;
	private Button mChooseLoggingStartDate;
	private Button mChooseLoggingStartTime;
	private Button mChooseLoggingEndDate;
	private Button mChooseLoggingEndTime;
	private ListView mAttendeeList; 
	
	private int mFromYear;
	private int mFromMonth;
	private int mFromDay;
	private int mFromHours;
	private int mFromMinutes;
	
	private int mToYear;
	private int mToMonth;
	private int mToDay;
	private int mToHours;
	private int mToMinutes;
	
	private DatePickerDialog.OnDateSetListener mChooseLoggingStartDateListener = 
		new DatePickerDialog.OnDateSetListener() {
			
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
                mFromYear = year;
                mFromMonth = monthOfYear;
                mFromDay = dayOfMonth;
                
                updateUI();				
			}
		};
		
	private DatePickerDialog.OnDateSetListener mChooseLoggingEndDateListener = 
			new DatePickerDialog.OnDateSetListener() {
				
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
	                mToYear = year;
	                mToMonth = monthOfYear;
	                mToDay = dayOfMonth;
	                
	                updateUI();				
				}
			};
	
	private TimePickerDialog.OnTimeSetListener mChooseLoogingStartTimeListener = 
		new TimePickerDialog.OnTimeSetListener() {
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mFromHours = hourOfDay;
				mFromMinutes = minute;
				
				updateUI();
			}
		};

	private TimePickerDialog.OnTimeSetListener mChooseLoogingEndTimeListener = 
		new TimePickerDialog.OnTimeSetListener() {
				
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mToHours = hourOfDay;
				mToMinutes = minute;
					
				updateUI();
			}
		};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.activity_meeting_details);
		addActionBarItem(Type.Locate, R.id.action_bar_map);
		addActionBarItem(Type.Refresh, R.id.action_bar_refresh);
		addActionBarItem(Type.Share, R.id.action_bar_save);
		//setContentView(R.layout.meeting_details);

		mEventStartDatetimeValue = (TextView)findViewById(R.id.event_start_datetime_value);
		mAllowLogging			 = (CheckBox)findViewById(R.id.allow_logging);
		mChooseLoggingStartDate  = (Button)findViewById(R.id.choose_logging_start_date);
		mChooseLoggingStartTime  = (Button)findViewById(R.id.choose_logging_start_time);
		mChooseLoggingEndDate  	 = (Button)findViewById(R.id.choose_logging_end_date);
		mChooseLoggingEndTime  	 = (Button)findViewById(R.id.choose_logging_end_time);
		mAttendeeList 			 = (ListView)findViewById(R.id.attendee_list);
		
		Intent i = getIntent();
		mEventId = i.getExtras().getInt("event_id");
		
		mAllowLogging.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(((CheckBox)mAllowLogging).isChecked()) {
					mChooseLoggingStartDate.setEnabled(true);
					mChooseLoggingStartTime.setEnabled(true);
					mChooseLoggingEndDate.setEnabled(true);
					mChooseLoggingEndTime.setEnabled(true);
				} else {
					mChooseLoggingStartDate.setEnabled(false);
					mChooseLoggingStartTime.setEnabled(false);
					mChooseLoggingEndDate.setEnabled(false);
					mChooseLoggingEndTime.setEnabled(false);
				}
				
			}
		});
		
		mChooseLoggingStartDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_FROM_DIALOG_ID);
			}
		});
		
		mChooseLoggingStartTime.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				showDialog(TIME_FROM_DIALOG_ID);
			}
		});
		
		mChooseLoggingEndDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_TO_DIALOG_ID);
			}
		});
		
		mChooseLoggingEndTime.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				showDialog(TIME_TO_DIALOG_ID);
			}
		});
		
		AlarmTable tbl = new AlarmTable(this);
		Cursor crs = tbl.getById(mEventId);
		
		if( crs != null && crs.getCount() == 1 && crs.moveToFirst() ) {
			int startCol	 = crs.getColumnIndex("start");
			int stopCol	 = crs.getColumnIndex("stop");
			int validCol = crs.getColumnIndex("valid");		
			Time startTimestamp = new Time();
			Time stopTimestamp = new Time();
			final Time t = new Time();			
			int isValid = 0;
			
			isValid = crs.getInt(validCol);
			startTimestamp.set(crs.getLong(startCol) * 1000);
			stopTimestamp.set(crs.getLong(stopCol) * 1000);
			t.setToNow();
			
			if( isValid == 0 || t.toMillis(true) > stopTimestamp.toMillis(true)) {
				resetTime();
			} else {
				mFromDay	 = startTimestamp.monthDay;
				mFromMonth	 = startTimestamp.month;
				mFromYear	 = startTimestamp.year;
				mFromHours   = startTimestamp.hour;
				mFromMinutes = startTimestamp.minute;
				
				mToDay		= stopTimestamp.monthDay;
				mToMonth	= stopTimestamp.month;
				mToYear		= stopTimestamp.year;
				mToHours 	= stopTimestamp.hour;
				mToMinutes 	= stopTimestamp.minute;
				
				mAllowLogging.setChecked(true);
				
				mChooseLoggingStartDate.setEnabled(true);
				mChooseLoggingStartTime.setEnabled(true);
				mChooseLoggingEndDate.setEnabled(true);
				mChooseLoggingEndTime.setEnabled(true);
			}
		} else {
			resetTime();
		}
		
		crs.close();
		tbl.close();
		
		prepareUI();
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
        switch (item.getItemId()) {
	        case R.id.action_bar_map:
	            startActivity(new Intent(this, MeetingMapActivity.class));
	        	//Toast.makeText(this, "MAP", Toast.LENGTH_SHORT).show();
	            break;
	
	        case R.id.action_bar_refresh:
	            Toast.makeText(this, "REFRESH", Toast.LENGTH_SHORT).show();
	            break;
	
	        case R.id.action_bar_save:
	        	java.util.Calendar calendar = java.util.Calendar.getInstance(); 		
	    		calendar.set(mFromYear, mFromMonth, mFromDay, mFromHours, mFromMinutes, 0);
	    		long fromTimestamp = calendar.getTimeInMillis() / 1000;
	    		
	    		calendar.set(mToYear, mToMonth, mToDay, mToHours, mToMinutes, 0);
	    		long toTimestamp = calendar.getTimeInMillis() / 1000;

	            AlarmTable tbl = new AlarmTable(this);
	            tbl.update(mEventId, fromTimestamp, toTimestamp, (mAllowLogging.isChecked()) ? 1 : 0);
	            tbl.close();
	    		
	    		Intent i = new Intent(MeetingDetailsActivity.this, TrackingService.class);
	    		PendingIntent pi = PendingIntent.getService(MeetingDetailsActivity.this, 0, i, 0);
	    		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);       
	            
	            manager.set(AlarmManager.RTC, fromTimestamp*1000, pi);
	            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
	            break;
	
	        default:
	            return super.onHandleActionBarItemClick(item, position);
        }

        return true;
	}
	

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_FROM_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mChooseLoggingStartDateListener,
	                    mFromYear, mFromMonth, mFromDay);
	    case DATE_TO_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mChooseLoggingEndDateListener,
	                    mToYear, mToMonth, mToDay);
	        
	    case TIME_FROM_DIALOG_ID:
	    	return new TimePickerDialog(this,
	    				mChooseLoogingStartTimeListener,
	    				mFromHours, mFromMinutes, true);
	    case TIME_TO_DIALOG_ID:
	    	return new TimePickerDialog(this,
	    				mChooseLoogingEndTimeListener,
	    				mToHours, mToMinutes, true);
	    }
	    return null;
	}
	
	private void prepareUI() {
		Time t = new Time();
		t.set(Calendar.Event.getEvent(getContentResolver(), mEventId).getDateStart());
		mEventStartDatetimeValue.setText(t.format("%d/%m/%Y %H:%M"));
		
		ArrayList<Calendar.Attendee> attendees =  Calendar.Attendee.getAll(getContentResolver(), mEventId);
		ArrayList<String> attendeesNames  = new ArrayList<String>();
				
		for(Calendar.Attendee a : attendees) {
			attendeesNames.add(a.getAttendeeEmail());
		}
		
		mAttendeeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attendeesNames));
		
		updateUI();
	}
	
	private void updateUI() {
		Time t = new Time();
		
		t.set(0, mFromMinutes, mFromHours, mFromDay, mFromMonth, mFromYear);
		mChooseLoggingStartDate.setText(t.format("%d/%m/%Y"));
		mChooseLoggingStartTime.setText(t.format("%H:%M"));
		
		t.set(0, mToMinutes, mToHours, mToDay, mToMonth, mToYear);
		mChooseLoggingEndDate.setText(t.format("%d/%m/%Y"));
		mChooseLoggingEndTime.setText(t.format("%H:%M"));
	}
	
	private void resetTime() {
		final java.util.Calendar c = java.util.Calendar.getInstance();
		mToDay   = mFromDay   = c.get(java.util.Calendar.DAY_OF_MONTH);
		mToMonth = mFromMonth = c.get(java.util.Calendar.MONTH);
		mToYear  = mFromYear  = c.get(java.util.Calendar.YEAR);
		
		final Time t = new Time();
		t.setToNow();
		mToHours   = mFromHours   = t.hour;
		mToMinutes = mFromMinutes = t.minute;
	}
	 
}
