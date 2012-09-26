package com.doLast.doGRT;
import java.util.Calendar;

import com.doLast.doGRT.R;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

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
	SimpleCursorAdapter adapter = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                
        
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
        String stop_id = extras.getString(android.content.Intent.EXTRA_TEXT);
                
        // Display schedule
        displaySchedule(stop_id);
        
        // Assign adapter to ListView
        setListAdapter(adapter); 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.main_option_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_option:        	
            return true; 
        case R.id.reset_option:
            return true;
        case R.id.about_option:        	
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
    
    private String getServiecId() {
    	String service_id = new String("");
    	String selection = new String("");
        
        switch(Calendar.DAY_OF_WEEK) {
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
        	service_id = " AND " + TripsColumns.SERVICE_ID + " = '" + services.getString(0) + "'";
        	services.moveToNext();
        } 
        service_id = "'" + services.getString(0) + "'" + service_id;
        Log.v("Service id", service_id);
                
        return service_id;
    }
    
    private void displaySchedule(String stop_id) {
        // Determine service id
    	String service_id = getServiecId();
    	
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { StopTimesColumns.TABLE_NAME + "." + StopTimesColumns.TRIP_ID + " as _id", 
        						StopTimesColumns.DEPART,
        						RoutesColumns.LONG_NAME };
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
        					trip_service_id + " = " + service_id;
        String orderBy = StopTimesColumns.DEPART;
        Cursor stop_times = managedQuery(
        		DatabaseSchema.STTRJ_CONTENT_URI, projection, selection, null, orderBy);
        Log.v("Query counts", "counts: " + stop_times.getCount());
        
        String[] uiBindFrom = { StopTimesColumns.DEPART, RoutesColumns.LONG_NAME };
        int[] uiBindTo = { R.id.depart_time, R.id.route_name };
        adapter = new SimpleCursorAdapter(this, R.layout.stop_time, stop_times,
                uiBindFrom, uiBindTo);
    	
    }
}
