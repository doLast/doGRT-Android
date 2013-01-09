package com.doLast.doGRT.route;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doLast.doGRT.R;
import com.doLast.doGRT.database.DatabaseSchema.StopTimesColumns;
import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;
import com.doLast.doGRT.main.MainActivity;
import com.doLast.doGRT.map.GMapsActivity;

public class RoutesActivity extends SherlockFragmentActivity {
	// For choosing between different view from other activities
	public static final String STOP_ID = "stop_id";
	public static final String STOP_NAME = "stop_name";
	public static final String STOP_TITLE = "stop_title";
	public static final String SCHEDULE_TYPE = "schedule_type";
	public static final int SCHEDULE_MIXED = 0;
	public static final int SCHEDULE_SELECT = 1;
	public static final int SCHEDULE_SINGLE = 2;
	public static final int NUM_TABS = 2;
			
	// Stop id, name and title
	private String stop_id = null;
	private String stop_name = null;
	private String stop_title = null;
	
	// Tab listener
	private TabListener<ScheduleListFragment> tab_listener = null;		
	private ViewPager mViewPager;
	private ActionBar mActionBar;
	
	// Option menu
	private Menu option_menu = null;
	
	// Constants for save instance
	private final String SELECTED_TAB = "selected_tab";
	
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
        
        // Set content view and view pager
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.layout.schedule);
        setContentView(mViewPager);        
        
        // Retrieve extra data
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	stop_id = extras.getString(STOP_ID);
        	stop_name = extras.getString(STOP_NAME);
        	stop_title = extras.getString(STOP_TITLE);
        }                
        
        // Use the "navigate up" button
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //mActionBar.setTitle(stop_title);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        // Custom title view with a textview and days spinner
        View custom_title_view = getLayoutInflater().inflate(R.layout.route_spinner, null, true);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        
	    // Setup the tabs
	    tab_listener = new TabListener<ScheduleListFragment>(this, "Tab Listener", ScheduleListFragment.class, mViewPager, mActionBar);
        // Check if tabs are already created
        if (mActionBar.getTabCount() == 0) {
        	// Mixed schedule tab
        	tab_listener.addTab(R.string.mixed_schedule, SCHEDULE_MIXED, tab_listener);
	        // Route selection tab
        	tab_listener.addTab(R.string.route_select, SCHEDULE_SELECT, tab_listener);
        }
        
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        
        // Setup spinner
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Spinner spinner = (Spinner)custom_title_view.findViewById(R.id.days_spinner);
        RouteSpinnerAdapter spinner_adapter = new RouteSpinnerAdapter(this,
                R.layout.route_spinner_title, days, stop_title, today);
        //ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tab_listener.setServiceId(position);				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub			
			}        	
        });
        spinner.setSelection(today);
                
        // Set custom view
        mActionBar.setCustomView(custom_title_view);
        
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
        
        // Restore previous tab selection
        if (savedInstanceState != null) {
        	mActionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB, 0));
        }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save previously selected tab
		outState.putInt(SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());
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
	        } else {
	        	menu.removeItem(R.id.delete_option);
	        }
	        Log.v("Stop count", user.getCount() + "");
			//user.close();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Always create a new menu
		menu.clear();
		new MenuInflater(this).inflate(R.menu.schedule_option_menu, menu);
		option_menu = menu;
		
		return super.onCreateOptionsMenu(menu);
	}	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent main_intent = new Intent(this, MainActivity.class);
        switch (item.getItemId()) {
        case R.id.add_option:        	
        	// Add to favourite
    	    String[] projection = { UserBusStopsColumns.STOP_ID };
    	    String selection = UserBusStopsColumns.STOP_ID + " = " + stop_id;
    	    Cursor user_stop = managedQuery(
    	        		UserBusStopsColumns.CONTENT_URI, projection, selection, null, null);
    	    
           	// Pack id and name into values
        	ContentValues values = new ContentValues();
        	values.put(UserBusStopsColumns.STOP_ID, stop_id);
        	values.put(UserBusStopsColumns.STOP_NAME, stop_name);
        	values.put(UserBusStopsColumns.TITLE, stop_title);
        	
    	    // Check if the stop id already exists
    	    if (user_stop.getCount() == 0) {
	        	// Add the stop id to database with stop name as default title
	        	Uri new_stop = getContentResolver().insert(UserBusStopsColumns.CONTENT_URI, values);
    	    }
    	    
    	    Toast.makeText(this, "Stop added", Toast.LENGTH_SHORT).show();
			break;
        case android.R.id.home:
        	main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(main_intent);
        	return true;
        case R.id.locate:
        	Intent map_intent = new Intent(this, GMapsActivity.class);
        	map_intent.putExtra(GMapsActivity.LOCATE, stop_id);
        	map_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(map_intent);            
        	return true;
        case R.id.delete_option:
    	    getContentResolver().delete(UserBusStopsColumns.CONTENT_URI, 
    	            UserBusStopsColumns.STOP_ID + " = " + stop_id, null);
    	    Toast.makeText(this, "Stop deleted", Toast.LENGTH_SHORT).show();
        	break;
        }
        
        // Update option menu
        updateOptionMenu();
        
        return super.onOptionsItemSelected(item);
    }        
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		updateOptionMenu();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		int type = (Integer)getSupportActionBar().getSelectedTab().getTag();
		if (!tab_listener.isSingleRouteDisplayed(type)) {
			super.onBackPressed();
		}
	}
	
	public void updateOptionMenu() {
		onCreateOptionsMenu(option_menu);
		onPrepareOptionsMenu(option_menu);
	}

	public String getStopId() { return stop_id; }
	public String getStopName() { return stop_name; }
	public String getStopTitle() { return stop_title; }	

    private class TabListener<T extends SherlockListFragment> extends FragmentPagerAdapter 
    	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private ScheduleListFragment ScheduleFragments[];
        private final SherlockFragmentActivity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final ViewPager mViewPager;
        private final ActionBar mActionBar;
    	
        private int cur_tab = 0; // Current selected tab
        
        /** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
        public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clz, ViewPager pager, ActionBar action_bar) {
        	super(activity.getSupportFragmentManager());
        	mActivity = activity;
        	mTag = tag;
        	mClass = clz;
        	mViewPager = pager;
        	mViewPager.setAdapter(this);
        	mViewPager.setOnPageChangeListener(this);
        	mActionBar = action_bar;
        	ScheduleFragments = new ScheduleListFragment[NUM_TABS];
        }
    	
        public void addTab(int text, int type, TabListener tab_listener) {
	        Tab tab = mActionBar.newTab()
	        		.setText(text)
	        		.setTag(type)
	        		.setTabListener(tab_listener);
	        mActionBar.addTab(tab);
	        ScheduleFragments[type] = ScheduleListFragment.newInstance(type);
        }
        
        public void setServiceId(int position) {
        	for(int i = 0; i < NUM_TABS; i += 1) 
        		ScheduleFragments[i].setServiceId(position);
    		// Display the new schedule on selected tab
        	ScheduleFragments[cur_tab].displaySchedule();
        }
        
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			int type = (Integer)tab.getTag();
			// Check if the fragment exist
			/*if (ScheduleFragments[type] == null) {
				// If not, create and add it
				ScheduleFragments[type] = ScheduleListFragment.newInstance(type);
				// Notice here that we use replace instead of add since add would duplicate existing
				// fragment causing overlay views
				ft.replace(android.R.id.content, ScheduleFragments[type], mTag);
			} else {
				// or just simply attach it
				ft.attach(ScheduleFragments[type]);
			}*/		
			mViewPager.setCurrentItem(type);
			cur_tab = type;
		}
	
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// int type = (Integer)tab.getTag();	
			// ft.detach(ScheduleFragments[type]);
		}
	
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// Nothing to do here
		}
		
		/**
		 * Ask if the a single route is being displayed
		 * If yes, back press is changed to display routes
		 * 
		 * */
		private boolean isSingleRouteDisplayed(int type) {
			boolean single_route = true;
			if (ScheduleFragments[type] != null) {
				single_route = ScheduleFragments[type].isSingleRouteDisplayed();
				if (single_route) ScheduleFragments[type].backKeyPressed();
			}
			
			return single_route;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// Nothing to do here
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// Nothing to do here
		}

		@Override
		public void onPageSelected(int type) {
			mActionBar.setSelectedNavigationItem(type);
			ScheduleFragments[type].displaySchedule();
		}

		@Override
		public Fragment getItem(int type) {
			if (ScheduleFragments[type] == null) 
				ScheduleFragments[type] = ScheduleListFragment.newInstance(type);
			return ScheduleFragments[type];
		}

		@Override
		public int getCount() {
			return NUM_TABS;
		}
    }	
}
