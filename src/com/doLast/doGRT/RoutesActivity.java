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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doLast.doGRT.database.DatabaseSchema;
import com.doLast.doGRT.database.DatabaseSchema.CalendarColumns;
import com.doLast.doGRT.database.DatabaseSchema.RoutesColumns;
import com.doLast.doGRT.database.DatabaseSchema.StopTimesColumns;
import com.doLast.doGRT.database.DatabaseSchema.StopsColumns;
import com.doLast.doGRT.database.DatabaseSchema.TripsColumns;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;

public class RoutesActivity extends SherlockFragmentActivity {
	// For choosing between different view from other activities
	public static final String STOP_NAME = "stop_name";
	public static final String STOP_TITLE = "stop_title";
	public static final String CHOOSE_ROUTES = "choose_routes";
	public static final String MIXED_SCHEDULE = "mixed_schedule";
	
	private SimpleCursorAdapter adapter = null;	
	
	// Stop id and stop name
	private String stop_id = null;
	private String stop_name = null;
	private String stop_title = null;
	
	// List view of the activity
	private ListView list_view = null;
	
	// Left buses display offset
	private final int LEFT_BUSES_OFFSET = 2; 
	
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
        
        // Set content view and find list view
        setContentView(R.layout.schedule);
        list_view = (ListView)findViewById(R.id.schedule_list_view);
        // Set up empty view
    	TextView empty_view = (TextView)findViewById(R.id.schedule_empty_view);
        list_view.setEmptyView(empty_view); 
        
        // Retrieve extra data
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	stop_id = extras.getString(MIXED_SCHEDULE);
        	stop_name = extras.getString(STOP_NAME);
        	stop_title = extras.getString(STOP_TITLE);
        	// Display schedule
        	displaySchedule(stop_id);
        }                
    }	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        // Check if stop_id already exist, do not display add button
		if (stop_id != null) {
	        String[] projection = { UserBusStopsColumns.USER_ID };
	        String selection = UserBusStopsColumns.STOP_ID + " = " + stop_id;
	        Cursor user = managedQuery(UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
	        if (user.getCount() > 0) {
	        	menu.removeItem(R.id.add_option);
	        }
			//user.close();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {				
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.schedule_option_menu, menu);
		
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
        	main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(main_intent);
        	return true;
        case R.id.route:
        	Intent map_intent = new Intent(this, GMapsActivity.class);
        	map_intent.putExtra(GMapsActivity.LOCATE, stop_id);
        	map_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(map_intent);            
        	return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
    
    private String getServiecId() {
    	String service_ids = new String("");
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
        for(int i = 0; i < services.getCount() - 1; i += 1){
        	service_ids += " OR " + TripsColumns.SERVICE_ID + " = '" + services.getString(0) + "'";
        	services.moveToNext();
        }
        service_ids = TripsColumns.SERVICE_ID + " = '" + services.getString(0) + "'" + service_ids;
        //services.close();
        return service_ids;
    }
    
    private void displaySchedule(String stop_id) {
        // Determine service id
    	String service_ids = getServiecId();
    	
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { StopTimesColumns.TABLE_NAME + "." + StopTimesColumns.TRIP_ID + " as _id", 
        						StopTimesColumns.DEPART,
        						RoutesColumns.LONG_NAME,
        						RoutesColumns.TABLE_NAME + "." + RoutesColumns.ROUTE_ID,
        						TripsColumns.TABLE_NAME + "." + TripsColumns.HEADSIGN };
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
        					"(" + service_ids + ")";
        String orderBy = StopTimesColumns.DEPART;
        Cursor stop_times = managedQuery(
        		DatabaseSchema.STTRJ_CONTENT_URI, projection, selection, null, orderBy);
        
        String[] uiBindFrom = { StopTimesColumns.DEPART, RoutesColumns.ROUTE_ID, RoutesColumns.LONG_NAME };
        int[] uiBindTo = { R.id.depart_time, R.id.route_name };                 
                
        // Move adapter to schedule close to current time
        Calendar time = Calendar.getInstance();
        int cur_time = time.get(Calendar.HOUR_OF_DAY) * 10000;
        cur_time += time.get(Calendar.MINUTE) * 100;
        cur_time += time.get(Calendar.SECOND);
        
        // Iterate through cursor
        int cur_pos = 0;
        stop_times.moveToFirst();
        for(int i = 0; i < stop_times.getCount(); i += 1) {        	
        	int depart = stop_times.getInt(1); // Get the departure time from cursor as and integer
        	if ( depart > cur_time ) {
        		cur_pos = i;
        		//section = COMING_BUSES;
        		break;
        	}	
        	stop_times.moveToNext();
        }            
        
        // Assign adapter to ListView
        adapter = new ScheduleAdapter(this, R.layout.schedule, stop_times,
                uiBindFrom, uiBindTo, cur_pos);
        list_view.setAdapter(adapter);
        if (cur_pos >= LEFT_BUSES_OFFSET) cur_pos -= LEFT_BUSES_OFFSET;
        list_view.setSelection(cur_pos);        
    }
}
