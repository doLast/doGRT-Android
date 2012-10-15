package com.doLast.doGRT;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import com.doLast.doGRT.database.*;
import com.doLast.doGRT.database.DatabaseSchema.RoutesColumns;
import com.doLast.doGRT.database.DatabaseSchema.StopTimesColumns;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;
import com.doLast.doGRT.R;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends SherlockFragmentActivity {
	// For adding a new stop from other activities
	public static final String ADD_STOP = "add_stop";
	public static final String ADD_STOP_NAME = "add_stop_name";
	
	private com.actionbarsherlock.view.ActionMode action_mode = null;
	private ListView list_view = null;
	
	// For action mode to update or delete
	private String stop_id = null;
	private String stop_title = null;
	
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
        	
        	// Remove the extras
        	getIntent().removeExtra(ADD_STOP);
        	getIntent().removeExtra(ADD_STOP_NAME);
        }
        
        setContentView(R.layout.activity_main);
        
        list_view = (ListView)findViewById(R.id.main_list_view);
        // Register long press event
        list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
        	    if (action_mode == null) action_mode = startActionMode(new UserCallback());
        	    action_mode.invalidate();
        	    // Find the title of the item clicked
        	    String[] projection = { UserBusStopsColumns.TITLE, UserBusStopsColumns.STOP_ID };
        	    String selection = UserBusStopsColumns.USER_ID + " = " + id;
        	    Cursor user_stop = managedQuery(
        	        		UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
        	    user_stop.moveToFirst();
        	    // Set the id and title to let the dialog fragment to do update        	    
        	    stop_id = user_stop.getString(1);
        	    stop_title = user_stop.getString(0);
        	    
        	    // Set the title
        	    action_mode.setTitle(stop_title);
        	    // Make the view selected
        	    view.setSelected(true);
				return true;
			}        	
		});
        // Register click listener
        list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				// TODO Auto-generated method stub
			    String[] projection = { UserBusStopsColumns.STOP_ID };
			    String selection = UserBusStopsColumns.USER_ID + " = " + id;
			    Cursor user_stop = managedQuery(
			        		UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
			    // Should have exactly one entry
			    if (user_stop.getCount() == 1) {
			    	user_stop.moveToFirst();
			        String stop_id = user_stop.getString(0);
			        // Switch to routes display
			        Intent route_intent = new Intent(MainActivity.this, RoutesActivity.class);
			        route_intent.putExtra(RoutesActivity.MIXED_SCHEDULE, stop_id);
			        TextView view = (TextView)v;
			        route_intent.putExtra(RoutesActivity.STOP_TITLE, view.getText());
			        startActivity(route_intent);
			    } else {
			      	Log.e("Wrong id", "wrong id?");        	
			    }
				//super.onListItemClick(l, v, position, id);
			}          	
        });
        
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { UserBusStopsColumns.USER_ID + " as _id", UserBusStopsColumns.STOP_ID, UserBusStopsColumns.TITLE };
        String[] uiBindFrom = { UserBusStopsColumns.TITLE };
        int[] uiBindTo = { android.R.id.text1 };
        Cursor user_stops = managedQuery(
        		UserBusStopsColumns.CONTENT_URI, projection, null, null, null);
        Log.d("Query result", "Count:" + user_stops.getCount());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, user_stops,
                uiBindFrom, uiBindTo);

        // Assign adapter to ListView
        list_view.setAdapter(adapter);
               
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
        case R.id.about_option:
            Toast.makeText(this, "doGRT Beta in process...", Toast.LENGTH_SHORT).show();
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }

/*	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {	
		// TODO Auto-generated method stub					

	}*/    
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Subtitle of the action bar
        getSupportActionBar().setSubtitle("Long press to start selection");
    }

	@TargetApi(11)
	private void showFragmentDialog(int dialog_id) {
        SherlockDialogFragment newFragment = MyDialogFragment.newInstance(dialog_id, stop_id, stop_title);
        newFragment.show(getSupportFragmentManager(), String.valueOf(dialog_id));
    }
    
	/** 
     * The CAB of edit option 
     * NOTE: This is only available in API 11. Need to modify
     */
    private class UserCallback implements com.actionbarsherlock.view.ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(
				com.actionbarsherlock.view.ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
	        // Inflate a menu resource providing context menu items
			com.actionbarsherlock.view.MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.edit_option_menu, menu);
	        return true;
		}

		@Override
		public boolean onPrepareActionMode(
				com.actionbarsherlock.view.ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(
				com.actionbarsherlock.view.ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			// Get the id of the item from the tag
	        switch (item.getItemId()) {
            case R.id.edit_option:
            	// Edit the title of the stop using a dialog
            	showFragmentDialog(MyDialogFragment.EDIT_DIALOG_ID);
                mode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.delete_option:
        	    // Perform a delete using a dialog
            	showFragmentDialog(MyDialogFragment.DELETE_DIALOG_ID);
        	    mode.finish();
            	return true;
            default:
                return false;
	        }
	    }

		@Override
		public void onDestroyActionMode(
				com.actionbarsherlock.view.ActionMode mode) {
			// TODO Auto-generated method stub
			action_mode = null;			
		}
    }
}
