package com.doLast.doGRT.main;

import java.lang.reflect.Method;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doLast.doGRT.R;
import com.doLast.doGRT.R.id;
import com.doLast.doGRT.R.layout;
import com.doLast.doGRT.R.menu;
import com.doLast.doGRT.custom.MyDialogFragment;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;
import com.doLast.doGRT.map.GMapsActivity;
import com.doLast.doGRT.route.RoutesActivity;

public class MainActivity extends SherlockFragmentActivity {
	// For adding a new stop from other activities
	public static final String ADD_STOP = "add_stop";
	public static final String ADD_STOP_NAME = "add_stop_name";
	
	// For CAB
	private com.actionbarsherlock.view.ActionMode action_mode = null;
	private UserCallback callback = null;
	
	// List view
	private ListView list_view = null;
	
	// For action mode to update or delete
	private String stop_id = null;
	private String stop_name = null;
	private String stop_title = null;
	
	// Option menu
	private Menu option_menu = null;
	
	// Version information
	private String version = "doGRT 1.0";
	
	// Number of user stops
	private int num_user_stop = 0;
	
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
        
        // Retrieve info
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	stop_id = extras.getString(ADD_STOP);
        	stop_name = extras.getString(ADD_STOP_NAME);
        	stop_title = stop_name;
        	
        	// Remove the extras
        	getIntent().removeExtra(ADD_STOP);
        	getIntent().removeExtra(ADD_STOP_NAME);
        }
        
        setContentView(R.layout.activity_main);
        
        list_view = (ListView)findViewById(R.id.main_list_view);
        list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list_view.setEmptyView((TextView)findViewById(android.R.id.empty));                     
        
        // Setup list view
        setupListView();
               
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
		// Recreate the menu
		menu.clear();
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.main_option_menu, menu);
		option_menu = menu;
		
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
            Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }    
	
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
    	onCreateOptionsMenu(option_menu);
    	onPrepareOptionsMenu(option_menu);
		super.onConfigurationChanged(newConfig);
	}

	private void showFragmentDialog(int dialog_id) {
        SherlockDialogFragment newFragment = MyDialogFragment.newInstance(dialog_id, stop_id, stop_title);
        newFragment.show(getSupportFragmentManager(), String.valueOf(dialog_id));
    }
	
	private void setupListView() {
        // Register long press event
        list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				callback = new UserCallback();
				callback.setListView(list_view);
        	    if (action_mode == null) action_mode = startActionMode(callback);
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
        	    list_view.setItemChecked(position, true);
				// Remind the callback the last selected position
        	    callback.setLastPosition(position);
				return true;
			}        	
		});
        // Register click listener
        list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				// TODO Auto-generated method stub
			    String[] projection = { UserBusStopsColumns.STOP_ID, UserBusStopsColumns.TITLE, UserBusStopsColumns.STOP_NAME };
			    String selection = UserBusStopsColumns.USER_ID + " = " + id;
			    Cursor user_stop = managedQuery(
			        		UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
			    // Should have exactly one entry
			    if (user_stop.getCount() == 1) {
			    	user_stop.moveToFirst();
			        stop_id = user_stop.getString(0);
			        stop_title = user_stop.getString(1);
			        stop_name = user_stop.getString(2);
			    }
			    
			    // If action_mode is active, do not switch to schedule view
				if (action_mode != null) {
					action_mode.setTitle(stop_title);
				} else {
			        // Switch to routes display
			        Intent route_intent = new Intent(MainActivity.this, RoutesActivity.class);
			        route_intent.putExtra(RoutesActivity.STOP_ID, stop_id);
			        route_intent.putExtra(RoutesActivity.STOP_NAME, stop_name);
			        route_intent.putExtra(RoutesActivity.STOP_TITLE, stop_title);
			        startActivity(route_intent);
				}
				
				// Remind the callback the last selected position
        	    if (callback != null ) callback.setLastPosition(position);
				//super.onListItemClick(l, v, position, id);
			}          	
        });
        
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { UserBusStopsColumns.USER_ID + " as _id", UserBusStopsColumns.STOP_ID, UserBusStopsColumns.TITLE };
        String[] uiBindFrom = { UserBusStopsColumns.TITLE, UserBusStopsColumns.STOP_ID };
        int[] uiBindTo = { android.R.id.text1, android.R.id.text2 };
        Cursor user_stops = managedQuery(
        		UserBusStopsColumns.CONTENT_URI, projection, null, null, null);
        Log.d("Query result", "Count:" + user_stops.getCount());
        SimpleCursorAdapter adapter = new UserAdapter(this, android.R.layout.simple_list_item_single_choice, user_stops,
                uiBindFrom, uiBindTo);
        
        // Hide edit instruction when there are no user stops
        if (user_stops.getCount() == 0) ((TextView)findViewById(R.id.edit_instruction)).setVisibility(View.GONE);        

        // Assign adapter to ListView
        list_view.setAdapter(adapter);
	}		
	
	private class UserAdapter extends SimpleCursorAdapter {
		private Context mContext;
		private int mLayout;
		private LayoutInflater mInflater;
		private Cursor cursor;
		
		public UserAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
	        super(context, layout, c, from, to);
	        mContext = context;
	        mLayout = layout;
	        mInflater = LayoutInflater.from(mContext);
	        cursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			CheckedTextView check_view = (CheckedTextView)view;
			String title = cursor.getString(cursor.getColumnIndex(UserBusStopsColumns.TITLE));
			String stop_id = cursor.getString(cursor.getColumnIndex(UserBusStopsColumns.STOP_ID));
			
			// Sneaky way to display different style in one view
			check_view.setText(Html.fromHtml(title + "<br>" + 
											"<font color=\"grey\"><small>" + stop_id + "</small></color>"));
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			super.notifyDataSetChanged();
			
			// Hide edit instruction
			TextView edit_view = (TextView)findViewById(R.id.edit_instruction);
	        if (cursor.getCount() == 0) {
	        	edit_view.setVisibility(View.GONE);  
	        } else {
	        	edit_view.setVisibility(View.VISIBLE);
	        }
		}
		
		
	}
    
	/** 
     * The CAB of edit option 
     */
    private class UserCallback implements com.actionbarsherlock.view.ActionMode.Callback {
    	private ListView list_view = null;
    	private int last_position = 0;

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
			return false;
		}

		@Override
		public boolean onActionItemClicked(
				com.actionbarsherlock.view.ActionMode mode, MenuItem item) {
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
			list_view.setItemChecked(last_position, false);
			action_mode = null;			
		}				
		
		public void setListView(ListView v) { list_view = v; }
		
		public void setLastPosition(int pos) { last_position = pos; }
    }
}
