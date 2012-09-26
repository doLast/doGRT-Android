package com.doLast.doGRT;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.*;

import com.actionbarsherlock.view.MenuInflater;
import com.doLast.doGRT.R;
import com.doLast.doGRT.database.DatabaseSchema;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GMapsActivity extends SherlockMapActivity implements LocationListener {
	/**
	 * Simple itemized overlay item class
	 */	
	public class PinItemizedOverlay extends ItemizedOverlay {
	   	Context mContext = null;
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		public PinItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			// TODO Auto-generated constructor stub
		}
		
		public PinItemizedOverlay(Drawable defaultMarker, Context context) {
			  super(boundCenterBottom(defaultMarker));
			  mContext = context;
			}		
		
		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return mOverlays.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mOverlays.size();
		}
		
		@Override
		protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
		  // This display a dialog
		  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();
		  return true;
		}			
		
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// TODO Auto-generated method stub
			// Activiely update the stops
			dropPins(mapView.getMapCenter());
			return super.onTouchEvent(event, mapView);
		}

		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
	}
	
    private final int MAX_STOPS_IN_MAP = 50;
    // Dialog IDs
	private final int GPS_ALERT_DIALOG_ID = 0;
	private final int BUS_STOPS_DIALOG_ID = 1;
    
    private MapView mapView;   
    private LocationManager location_manager = null;
    private String location_provider = null;
    private MapController map_controller = null;
    private boolean ask_gps = true;
    private GeoPoint waterloo = new GeoPoint(43468798,-80539179); // University of Waterloo
    private int zoom_level = 17;
    private int stop_delta = 7000;
    private List<Overlay> mapOverlays = null;
    private Drawable drawable = null;
    private PinItemizedOverlay itemized_overlay = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setBuiltInZoomControls(true);        
        
        // Use the "navigate up" button
        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);
        
        // Setting center to Waterloo
        map_controller = mapView.getController();
        map_controller.setCenter(waterloo);
        map_controller.setZoom(zoom_level);
        
        // Try getting the current location (Not working)
        location_manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        location_provider = location_manager.getBestProvider(criteria, true);
        if (location_provider != null) {
        	//Log.v("Found provider", location_provider);
        } else {
        	Toast.makeText(this, "Unable to find a proper provider", Toast.LENGTH_SHORT).show();
        }
        
        // Put down stops around the center location
        /* If no data is given in the Intent that started this Activity, then this Activity
         * was started when the intent filter matched a MAIN action. We should use the default
         * provider URI.
         */
        // Gets the intent that started this Activity.
        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(DatabaseSchema.StopsColumns.CONTENT_URI);
        }        
        
        // Setup overlay item
        mapOverlays = mapView.getOverlays();        
        drawable = this.getResources().getDrawable(R.drawable.flag1);
        
        // Check if user enabled GPS
        checkGPS();
    }
        
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		/* Remove the locationlistener updates when Activity is paused */
		location_manager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/* Request updates at startup */
	    location_manager.requestLocationUpdates(location_provider, 400, 1, this);

        // Drop pins around the center area
        dropPins(mapView.getMapCenter()); 
        mapView.postInvalidate();
	}		

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		new MenuInflater(this).inflate(R.menu.map_option_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_option:
            Toast.makeText(this, "Should add to favourite", Toast.LENGTH_SHORT).show();
            return true; 
        case R.id.reset_option:
            return true;
        case android.R.id.home:
        	Intent main_intent = new Intent(this, MainActivity.class);
        	main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(main_intent);
        	return true;
        }
 
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
	    Dialog dialog = null;
	    switch(id) {
	    case GPS_ALERT_DIALOG_ID:
	    	// Create alert dialog for asking user to enable gps
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			CharSequence[] remember = {"Do not display this again"};
			builder.setTitle(R.string.enable_gps)
	        		.setMessage(R.string.enable_gps_dialog)                    
	        		.setNegativeButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				enableLocationSettings();                        	
	        			}
	        		})
	        		.setPositiveButton(R.string.do_not_enable_gps, new DialogInterface.OnClickListener() {
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				// Do nothing
	        			}
	        		});
	        dialog = builder.create();
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}

	/**
     * Drop pins at bus stops on the area around the 0 center point
     */
    private void dropPins(GeoPoint center) {
    	// Update the delta value
    	stop_delta = stop_delta + 1000 * ((int)(Math.pow(2, zoom_level - mapView.getZoomLevel())) - 1);
    	zoom_level = mapView.getZoomLevel();
    	
    	// Setup query projection and selection
        String[] projection = { DatabaseSchema.StopsColumns.STOP_ID + " as _id", DatabaseSchema.StopsColumns.STOP_NAME,
        		DatabaseSchema.StopsColumns.STOP_LAT, DatabaseSchema.StopsColumns.STOP_LON };
        String selection = new String(
        		DatabaseSchema.StopsColumns.STOP_LAT + " >= " + ((center.getLatitudeE6() - stop_delta ) / 1E6) + " and " +
        		DatabaseSchema.StopsColumns.STOP_LAT + " <= " + ((center.getLatitudeE6() + stop_delta ) / 1E6) + " and " +
        		DatabaseSchema.StopsColumns.STOP_LON + " >= " + ((center.getLongitudeE6() - stop_delta ) / 1E6) + " and " +
        		DatabaseSchema.StopsColumns.STOP_LON + " <= " + ((center.getLongitudeE6() + stop_delta ) / 1E6)
        		);
        
        Cursor stops = managedQuery(
        		DatabaseSchema.StopsColumns.CONTENT_URI, projection, selection, null, null);
                      
        mapOverlays.clear();
        itemized_overlay = new PinItemizedOverlay(drawable, this);
        stops.moveToFirst();        
        // Display all bus stops up to MAX_STOPS_IN_MAP stops
        for(int i = 0; i < stops.getCount() && i < MAX_STOPS_IN_MAP; i += 1) {
        	GeoPoint point = new GeoPoint((int)(stops.getDouble(2) * 1E6), (int)(stops.getDouble(3) * 1E6));
            OverlayItem overlayitem = new OverlayItem(point, "Stop", stops.getString(1));            
            itemized_overlay.addOverlay(overlayitem);
        	stops.moveToNext();
        }
        mapOverlays.add(itemized_overlay);
    }
    
    /**
     * Method for checking whether user's GPS is on 
     */
    private void checkGPS() {
    	boolean enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    	// Check if enabled and if not send user to the GSP settings
    	// Better solution would be to display a dialog and suggesting to 
    	// go to the settings
    	if (!enabled && ask_gps) {
            // Build an alert dialog here that requests that the user enable
            // the location services, then when the user clicks the "OK" button,
            // call enableLocationSettings()
    		showDialog(GPS_ALERT_DIALOG_ID);
    	}
    }
    
    // Method to launch Settings
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
    

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	    Toast.makeText(this, "Enabled new provider " + provider,
	            Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}