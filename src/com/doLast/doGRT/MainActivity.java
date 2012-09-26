package com.doLast.doGRT;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.*;
import com.doLast.doGRT.database.*;
import com.doLast.doGRT.R;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends SherlockListActivity {

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
            intent.setData(DatabaseSchema.RoutesColumns.CONTENT_URI);
        }
        
        // Remember to perform an alias of our own primary key to _id so the adapter knows what to do
        String[] projection = { DatabaseSchema.RoutesColumns.ROUTE_ID + " as _id", DatabaseSchema.RoutesColumns.LONG_NAME };
        String[] uiBindFrom = { DatabaseSchema.RoutesColumns.LONG_NAME };
        int[] uiBindTo = { android.R.id.text1 };
        Cursor tutorials = managedQuery(
        		DatabaseSchema.RoutesColumns.CONTENT_URI, projection, null, null, null);
        Log.v("Query result", "Count:"+tutorials.getCount());
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, tutorials,
                uiBindFrom, uiBindTo);
        Log.v("Adapter returned", "adapter returned");

        // Assign adapter to ListView
        setListAdapter(adapter);       
              
        // Forcing the overflow menu (3 dots)
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
            return true;
        case R.id.about_option:
            Toast.makeText(this, "doGRT Beta in process...", Toast.LENGTH_SHORT).show();
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
}
