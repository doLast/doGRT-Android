package com.doLast.doGRT;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import com.doLast.doGRT.database.*;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;
import com.doLast.doGRT.R;

import android.net.Uri;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends SherlockListActivity {
	// For adding a new stop from other activities
	public static final String ADD_STOP = "add_stop";
	public static final String ADD_STOP_NAME = "add_stop_name";
	
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
            intent.setData(UserBusStopsColumns.CONTENT_URI);
        }
        
        // Check if the user has added a new stop
        // Retrieve stop id
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	String stop_id = extras.getString(ADD_STOP);
        	String stop_name = extras.getString(ADD_STOP_NAME);
        	// Pack id and name into values
        	ContentValues values = new ContentValues();
        	values.put(UserBusStopsColumns.STOP_ID, stop_id);
        	values.put(UserBusStopsColumns.TITLE, stop_name);
        	
        	// Add the stop id to database with stop name as default title
        	Uri new_stop = getContentResolver().insert(UserBusStopsColumns.CONTENT_URI, values);
        }
        
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { UserBusStopsColumns.USER_ID + " as _id", UserBusStopsColumns.STOP_ID, UserBusStopsColumns.TITLE };
        String[] uiBindFrom = { UserBusStopsColumns.TITLE };
        int[] uiBindTo = { android.R.id.text1 };
        Cursor user_stops = managedQuery(
        		UserBusStopsColumns.CONTENT_URI, projection, null, null, null);
        Log.v("Query result", "Count:" + user_stops.getCount());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, user_stops,
                uiBindFrom, uiBindTo);

        // Assign adapter to ListView
        setListAdapter(adapter);
              
        // Forcing the overflow menu (3 dots menu)
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
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
        	// Go to GMapsActivity
        	Intent gmap_intent = new Intent(this, GMapsActivity.class);
        	startActivity(gmap_intent);
            return true; 
        case R.id.reset_option:
        	Toast.makeText(this, "Haven't implemented yet", Toast.LENGTH_SHORT).show();
            return true;
        case R.id.about_option:
            Toast.makeText(this, "doGRT Beta in process...", Toast.LENGTH_SHORT).show();
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		// Find the information about the stop
        String[] projection = { UserBusStopsColumns.STOP_ID };
        String selection = UserBusStopsColumns.USER_ID + " = " + id;
        Cursor user_stop = managedQuery(
        		UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
        // Should have exactly one entry
        if (user_stop.getCount() == 1) {
        	user_stop.moveToFirst();
            String stop_id = user_stop.getString(0);
    		// Switch to routes display
    		Intent route_intent = new Intent(this, RoutesActivity.class);
    		route_intent.putExtra(RoutesActivity.MIXED_SCHEDULE, stop_id);
    		startActivity(route_intent);
        } else {
        	Log.e("Wrong id", "wrong id?");        	
        }				
		//super.onListItemClick(l, v, position, id);
	}        
}
