package eu.doniec.piotr.naspotkanie.mobile;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.MapActivity;

import eu.doniec.piotr.naspotkanie.mobile.service.MyTestService;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;

public class MeetingDetailsActivity extends MapActivity {

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	
	private int mEventId;
	private TextView mEventStartDatetimeValue;
	private CheckBox mAllowLogging;
	private Button mChooseLoggingStartDate;
	private Button mChooseLoggingStartTime;
	private ListView mAttendeeList; 
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHours;
	private int mMinutes;
	
	private DatePickerDialog.OnDateSetListener mChooseLoggingStartDateListener = 
		new DatePickerDialog.OnDateSetListener() {
			
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                
                updateUI();				
			}
		};
	
	private TimePickerDialog.OnTimeSetListener mChooseLoogingStartTimeListener = 
		new TimePickerDialog.OnTimeSetListener() {
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mHours = hourOfDay;
				mMinutes = minute;
				
				updateUI();
			}
		};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		mEventStartDatetimeValue = (TextView)findViewById(R.id.event_start_datetime_value);
		mAllowLogging			 = (CheckBox)findViewById(R.id.allow_logging);
		mChooseLoggingStartDate  = (Button)findViewById(R.id.choose_logging_start_date);
		mChooseLoggingStartTime  = (Button)findViewById(R.id.choose_logging_start_time);
		mAttendeeList 			 = (ListView)findViewById(R.id.attendee_list);
		
		Intent i = getIntent();
		mEventId = i.getExtras().getInt("event_id");
		
		mAllowLogging.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(((CheckBox)mAllowLogging).isChecked()) {
					mChooseLoggingStartDate.setClickable(true);
					mChooseLoggingStartDate.setEnabled(true);
					mChooseLoggingStartTime.setClickable(true);
					mChooseLoggingStartTime.setEnabled(true);
					
					
				} else {
					mChooseLoggingStartDate.setClickable(false);
					mChooseLoggingStartDate.setEnabled(false);
					mChooseLoggingStartTime.setClickable(false);
					mChooseLoggingStartTime.setEnabled(false);
				}
			}
		});
		
		mChooseLoggingStartDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		mChooseLoggingStartTime.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		final java.util.Calendar c = java.util.Calendar.getInstance();
		mDay = c.get(java.util.Calendar.DAY_OF_MONTH);
		mMonth = c.get(java.util.Calendar.MONTH);
		mYear = c.get(java.util.Calendar.YEAR);
		
		final Time t = new Time();
		t.setToNow();
		mHours = t.hour;
		mMinutes = t.minute;
		
		prepareUI();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.i(NaSpotkanieApplication.APPTAG, "onPause:)");
		Toast.makeText(MeetingDetailsActivity.this, "TEST", Toast.LENGTH_SHORT).show();
		
		Intent i = new Intent(MeetingDetailsActivity.this, MyTestService.class);
		PendingIntent pi = PendingIntent.getService(MeetingDetailsActivity.this, 0, i, 0);
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		
		java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(java.util.Calendar.SECOND, 10);
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mChooseLoggingStartDateListener,
	                    mYear, mMonth, mDay);
	        
	    case TIME_DIALOG_ID:
	    	return new TimePickerDialog(this,
	    				mChooseLoogingStartTimeListener,
	    				mHours, mMinutes, true);
	    }
	    return null;
	}
	
	private void prepareUI() {
		
		Time t = new Time();
		t.set(Calendar.Event.getEvent(getContentResolver(), mEventId).getDateStart());
		mEventStartDatetimeValue.setText(t.format("%d/%m/%Y %H:%M:%S"));
		
		ArrayList<Calendar.Attendee> attendees =  Calendar.Attendee.getAll(getContentResolver(), mEventId);
		ArrayList<String> attendeesNames  = new ArrayList<String>();
				
		for(Calendar.Attendee a : attendees) {
			attendeesNames.add(a.getAttendeeEmail());
		}
		
		mAttendeeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attendeesNames));
		
		updateUI();
	}
	
	private void updateUI() {
		
		mChooseLoggingStartDate.setText(new StringBuilder()
											.append(mDay).append("/")
											.append(mMonth+1).append("/")
											.append(mYear));
		
		mChooseLoggingStartTime.setText(new StringBuilder()
											.append(mHours).append(":")
											.append(mMinutes));
		
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
