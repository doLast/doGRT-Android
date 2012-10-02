package com.doLast.doGRT;
import java.util.Calendar;

import com.doLast.doGRT.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doLast.doGRT.database.DatabaseSchema;
import com.doLast.doGRT.database.DatabaseSchema.CalendarColumns;
import com.doLast.doGRT.database.DatabaseSchema.RoutesColumns;
import com.doLast.doGRT.database.DatabaseSchema.StopTimesColumns;
import com.doLast.doGRT.database.DatabaseSchema.TripsColumns;

public class RoutesActivity extends SherlockListActivity {
	// For choosing between different view from other activities
	public static final String STOP_NAME = "stop_name";
	public static final String CHOOSE_ROUTES = "choose_routes";
	public static final String MIXED_SCHEDULE = "mixed_schedule";
	
	private SimpleCursorAdapter adapter = null;	
	
	// Stop id and stop name
	private String stop_id = null;
	private String stop_name = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        // Use the "navigate up" button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
        
        /* If no data is given in the Intent that started this Activity, then this Activity
         * was started when the intent filter matched a MAIN action. We should use the default
         * provider URI.
         */
        // Gets the intent that started this Activity.
        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(StopTimesColumns.CONTENT_URI);
        }
        //setContentView(R.layout.stop_time);
        
        // Retrieve stop id
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	stop_id = extras.getString(MIXED_SCHEDULE);
        	stop_name = extras.getString(STOP_NAME);
        	// Display schedule
        	displaySchedule(stop_id);
        }
        
        // Assign adapter to ListView
        setListAdapter(adapter);
        
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.route_option_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent main_intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
        case R.id.add_option:        	
			// Switch to main activity
			// Pack the stop id and name with the intent
			main_intent.putExtra(MainActivity.ADD_STOP, stop_id);
			main_intent.putExtra(MainActivity.ADD_STOP_NAME, stop_name);
        case android.R.id.home:
        	main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(main_intent);
        	return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
    
    private String getServiecId() {
    	String service_id = new String("");
    	String selection = new String("");
        switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        case Calendar.SUNDAY:
        	selection = "sunday";
        	break;
        case Calendar.THURSDAY:
        	selection = "thursday";
        	break;
        case Calendar.MONDAY:
        	selection = "monday";
        	break;
        case Calendar.TUESDAY:
        	selection = "tuesday";
        	break;
        case Calendar.WEDNESDAY:
        	selection = "wednesday";
        	break;
        case Calendar.FRIDAY:
        	selection = "friday";
        	break;
        case Calendar.SATURDAY:
        	selection = "saturday";
        	break;
        default:
        	break;
        }
        
        String[] projection = { CalendarColumns.SERVICE_ID };
        Cursor services = managedQuery(CalendarColumns.CONTENT_URI, projection, selection, null, null);
        services.moveToFirst();
        if (services.getCount() > 1) {
        	service_id = " OR " + TripsColumns.SERVICE_ID + " = '" + services.getString(0) + "'";
        	services.moveToNext();
        }
        service_id = "'" + services.getString(0) + "'" + service_id;
                
        return service_id;
    }
    
    private void displaySchedule(String stop_id) {
        // Determine service id
    	String service_id = getServiecId();
    	
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { StopTimesColumns.TABLE_NAME + "." + StopTimesColumns.TRIP_ID + " as _id", 
        						StopTimesColumns.DEPART,
        						RoutesColumns.LONG_NAME,
        						RoutesColumns.TABLE_NAME + "." + RoutesColumns.ROUTE_ID };
        // Some complex selection for selecting from 3 tables
        String stop_time_id = StopTimesColumns.TABLE_NAME + "." + StopTimesColumns.STOP_ID;
        String stop_time_trip_id = StopTimesColumns.TABLE_NAME + "." + StopTimesColumns.TRIP_ID;
        String trip_trip_id = TripsColumns.TABLE_NAME + "." + TripsColumns.TRIP_ID;
        String trip_route_id = TripsColumns.TABLE_NAME + "." + TripsColumns.ROUTE_ID;
        String trip_service_id = TripsColumns.TABLE_NAME + "." + TripsColumns.SERVICE_ID;
        String route_route_id = RoutesColumns.TABLE_NAME + "." + RoutesColumns.ROUTE_ID;
        
        String selection =  stop_time_id + " = " + stop_id + " AND " +
        					stop_time_trip_id + " = " + trip_trip_id + " AND " +
        					trip_route_id + " = " + route_route_id + " AND " +
        					"(" + trip_service_id + " = " + service_id + ")";
        String orderBy = StopTimesColumns.DEPART;
        Cursor stop_times = managedQuery(
        		DatabaseSchema.STTRJ_CONTENT_URI, projection, selection, null, orderBy);
        
        String[] uiBindFrom = { StopTimesColumns.DEPART, RoutesColumns.ROUTE_ID, RoutesColumns.LONG_NAME };
        int[] uiBindTo = { R.id.depart_time, R.id.route_name };
        adapter = new ScheduleAdapter(this, R.layout.schedule, stop_times,
                uiBindFrom, uiBindTo);        
    }

    public class ScheduleAdapter extends SimpleCursorAdapter {
    	private Context mContext;
    	private int mLayout;
    	private LayoutInflater mInflater;
    	
    	public ScheduleAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
	        super(context, layout, c, from, to);
	        mContext = context;
	        mLayout = layout;
	        mInflater = LayoutInflater.from(mContext);
    	}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView time_view = (TextView)view.findViewById(R.id.depart_time);
			// Truncate the time into a readable format
			String time = cursor.getString(cursor.getColumnIndex(StopTimesColumns.DEPART));
			String second = time.substring(time.length() - 2, time.length());
			String minute = time.substring(time.length() - 4, time.length() - 2);
			String hour = time.substring(0, time.length() - 4);
			// Check if hour is greater than 24, change it to 0
			if (Integer.valueOf(hour) >= 24)
				hour = String.valueOf((Integer.valueOf(hour) - 24));
			// Add a prefix 0 to hour less than 10
			if (Integer.valueOf(hour) < 10)
				hour = "0" + hour;
			time_view.setText(hour + ":" + minute);			

			// Keep original route name
			TextView route_view = (TextView)view.findViewById(R.id.route_name);
			route_view.setText(cursor.getString(cursor.getColumnIndex(RoutesColumns.ROUTE_ID)) + " " +
								cursor.getString(cursor.getColumnIndex(RoutesColumns.LONG_NAME)));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
	        final View view = mInflater.inflate(R.layout.schedule, parent, false); 
	        return view;
		}
    	
    	
    }
}
