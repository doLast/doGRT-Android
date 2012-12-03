package com.doLast.doGRT;

import java.util.regex.Pattern;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.doLast.doGRT.database.DatabaseSchema.StopsColumns;

public class SearchableActivity extends SherlockListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        // Use the "navigate up" button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
	    
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    handleIntent(intent);
	}
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {		
    	new MenuInflater(this).inflate(R.menu.search_option_menu, menu);
    	
		// Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    
		return super.onCreateOptionsMenu(menu);
	}

	@Override
    protected void onNewIntent(Intent intent) {
    	setIntent(intent);
        handleIntent(intent);
    }
	
	// Perform a search on the stops
	private void searchStops(String query) {
		// Clean up any leading or trailing space in the given string
		query = query.trim();
		String queries[] = query.split(" ");
		
		if (queries.length <= 0) return;
	    String[] projection = { StopsColumns.STOP_NAME + " as _id", StopsColumns.STOP_ID };
	    String selection = parseWhereClause(queries);
	    Cursor stops = managedQuery(
	        		StopsColumns.CONTENT_URI, projection, selection, null, null);
	    
	    String[] uiBindFrom = { "_id", StopsColumns.STOP_ID };
        int[] uiBindTo = { android.R.id.text1, android.R.id.text2 };        
	    
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, stops, uiBindFrom, uiBindTo);
	    
	    setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TwoLineListItem text_view = (TwoLineListItem)v;
		
		// Retrieve stop id
		String stop_id = text_view.getText2().getText().toString();
		
		// Deliver stop id to display route
    	Intent map_intent = new Intent(this, GMapsActivity.class);
    	map_intent.putExtra(GMapsActivity.LOCATE, stop_id);
    	//map_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(map_intent); 
		
		super.onListItemClick(l, v, position, id);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	// Go back to favourite list
        	Intent main_intent = new Intent(this, MainActivity.class);
        	main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(main_intent);
        	return true;
        }
		
		return super.onOptionsItemSelected(item);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		      String query = intent.getStringExtra(SearchManager.QUERY);
		      searchStops(query);
		}
	}
	
	private String parseWhereClause(String queries[]) {
		// Commented out query is for use when FTS3 and FTS4 are available for better performance
		//String where_clause = StopsColumns.STOP_NAME + " MATCH '";
		String where_clause = "";
		boolean isInteger = false;
		for(int i = 0; i < queries.length; i += 1) {
			// Check if user want to search an stop id
			isInteger = Pattern.matches("^\\d*$", queries[i]);
			if (isInteger) {
				where_clause += StopsColumns.STOP_ID;
			} else {
				where_clause += StopsColumns.STOP_NAME ;
			}
			where_clause += " LIKE '%" + queries[i] + "%'";
			if (i < queries.length - 1) where_clause += " AND ";
			//where_clause += queries[i] + " ";
		}
		return where_clause;
	}

}
