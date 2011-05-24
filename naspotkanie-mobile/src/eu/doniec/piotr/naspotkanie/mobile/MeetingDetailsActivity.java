package eu.doniec.piotr.naspotkanie.mobile;

import eu.doniec.piotr.naspotkanie.mobile.AuthActivity.AuthRegisterResponse;
import eu.doniec.piotr.naspotkanie.mobile.util.AlarmTable;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar;
import eu.doniec.piotr.naspotkanie.mobile.util.HttpAuthorizedRequest;
import eu.doniec.piotr.naspotkanie.mobile.util.Calendar.Attendee;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.LoaderActionBarItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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

import com.google.gson.Gson;

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
		
		((NaSpotkanieApplication)getApplication()).updateAttendeesMap(Calendar.Attendee.getAll(getContentResolver(), mEventId));
		prepareUI();
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
        switch (item.getItemId()) {
	        case R.id.action_bar_map:
	        	Intent i = new Intent(this, MeetingMapActivity.class);
	        	i.putExtra("event_id", mEventId);
	            startActivity(i);
	            break;
	
	        case R.id.action_bar_refresh:
	            new RefreshPositions().execute();
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
	    		
	            if( mAllowLogging.isChecked() ) {
	            	setupSharing(mEventId);
	            }
	                   
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
		
		ArrayList<String> attendeesNames = getAttendeesEmails();
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
	
	private ArrayList<String> getAttendeesEmails() {
		ArrayList<Calendar.Attendee> attendees =  Calendar.Attendee.getAll(getContentResolver(), mEventId);
		ArrayList<String> attendeesNames  = new ArrayList<String>();
				
		for(Calendar.Attendee a : attendees) {
			attendeesNames.add(a.getAttendeeEmail());
		}
		
		return attendeesNames;
	}
	
	private void setupSharing(int eventId) {
		ArrayList<Attendee> attendees = Calendar.Attendee.getAll(getContentResolver(), eventId);
		ArrayList<String> emails = new ArrayList<String>();
		EnaDisSharingMessage m = new EnaDisSharingMessage();
		
		for(Attendee a : attendees) {
			emails.add(a.getAttendeeEmail());
		}
		
		String[] str = new String[emails.size()];
		emails.toArray(str);
		
		m.action = 1;
		m.username = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE).getString("username", "");
		m.attendees = str;
		
		new SetPositionSharing().execute(m);
	}
	
	
	class SetPositionSharing extends AsyncTask<EnaDisSharingMessage, Void, Boolean> {

		@Override
		protected Boolean doInBackground(EnaDisSharingMessage... params) {
			Gson gson 					= new Gson();
			HttpPost post 				= new HttpPost("/SetLog");
			HttpAuthorizedRequest req 	= ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();
			StringEntity s;
			
			try {
				s = new StringEntity(gson.toJson(params[0]));
				
				s.setContentEncoding("application/json");
				post.setEntity(s);
				req.makeRequest(post);
				
				return Boolean.TRUE;
			} catch (Exception e1) {
				e1.printStackTrace();
				return Boolean.FALSE;
			}

		}
		
	}
	
	class RefreshPositions extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			Gson gson 					= new Gson();
			HttpPost post 				= new HttpPost("/PosReq");
			HttpAuthorizedRequest req 	= ((NaSpotkanieApplication)getApplication()).getHttAuthorizedRequest();
			StringEntity s;
			HttpEntity entity;
			
			AttendeesPositionReqMessage reqmsg = new AttendeesPositionReqMessage();
			ArrayList<String> emailsList = getAttendeesEmails();
			String[] emailsArray = new String[emailsList.size()];
			
			emailsList.toArray(emailsArray);
			
			reqmsg.email = getSharedPreferences(NaSpotkanieApplication.PREFS, Context.MODE_PRIVATE).getString("username", "");
			reqmsg.emails = emailsArray;
			
			Log.d(NaSpotkanieApplication.APPTAG, "ASDASDASDASDASD");
			
			try {				
				s = new StringEntity(gson.toJson(reqmsg));
				s.setContentEncoding("application/json");
				post.setEntity(s);
				entity = req.makeRequest(post);
				
				NaSpotkanieApplication app = (NaSpotkanieApplication)getApplication();
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));	
				String json = reader.readLine();
				AttendeesPositionRespMessage arr = gson.fromJson(json, AttendeesPositionRespMessage.class);
				
				Log.d(NaSpotkanieApplication.APPTAG, "JSON read from server:" + json);				
				
				for(int i = 0; i < arr.getEmails().length; i++) {
					String email = arr.getEmails()[i];
					Attendee a = app.mAttendeesPositions.get(email);
					a.setLattitude(arr.getLat()[i]);
					a.setLattitude(arr.getLgt()[i]);
					app.mAttendeesPositions.put(email, a);
				}
				
				return Boolean.TRUE;
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(MeetingDetailsActivity.this, "Refresh completed", Toast.LENGTH_SHORT).show();
			
			LoaderActionBarItem loader = (LoaderActionBarItem)getActionBar().getItem(1);
			loader.setLoading(false);
		}
		
	}
	
	/*
	 * Reqest message to serer for last known positions
	 * of specified users 
	 */
	static class AttendeesPositionReqMessage {
		String email;
		String[] emails;
		
		public AttendeesPositionReqMessage() {
			// TODO Auto-generated constructor stub
		}
	}
	
	class AttendeesPositionRespMessage {
		String[] emails;
		double[] lgt;
		double[] lat;
		
		public String[] getEmails() {
			return emails;
		}
		public void setEmails(String[] emails) {
			this.emails = emails;
		}
		public double[] getLgt() {
			return lgt;
		}
		public void setLgt(double[] lgt) {
			this.lgt = lgt;
		}
		public double[] getLat() {
			return lat;
		}
		public void setLat(double[] lat) {
			this.lat = lat;
		}
		
	}
	
	class EnaDisSharingMessage {
		int action;
		String username;
		String[] attendees;
	}
	
	 
}
