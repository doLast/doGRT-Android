/**
 * The usage of database is referenced from 
 * http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/ 
 * */

package com.doLast.doGRT.database;

import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DatabaseProvider extends ContentProvider {
	private static final String TAG = "DatabaseProvider";
	
    // Database
    private DatabaseHelper mOpenHelper;
    
    // For UriMatcher
    private static final int BUS_STOP = 100;
    private static final int CALENDAR = 200;
    private static final int ROUTE = 300;
    private static final int TRIP = 400;
    private static final int STOP_TIME = 500;
    private static final int STOP_TIME_TRIP_ROUTE = 600;
    private static final int TRIP_SHAPE = 700;
     
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.StopsColumns.TABLE_NAME, BUS_STOP);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.CalendarColumns.TABLE_NAME, CALENDAR);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.RoutesColumns.TABLE_NAME, ROUTE);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.TripsColumns.TABLE_NAME, TRIP);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.StopTimesColumns.TABLE_NAME, STOP_TIME);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.STOP_TIME_TRIP_ROUTE_JOINT, STOP_TIME_TRIP_ROUTE);
    	sUriMatcher.addURI(DatabaseSchema.AUTHORITY, DatabaseSchema.TRIP_SHAPE_JOINT, TRIP_SHAPE);
    }
    
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// Nothing to do here, this database is only readable
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// Nothing to do here, this database is only readable
		return null;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		try {
			mOpenHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}
		
		try {
			mOpenHelper.openDataBase();
		} catch (SQLException e) {
			throw e;
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
	    // Using SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Select a table
	    String table = null;
	    switch(sUriMatcher.match(uri)) {
	    case BUS_STOP:
	    	table = DatabaseSchema.StopsColumns.TABLE_NAME;
	    	break;
	    case CALENDAR:
	    	table = DatabaseSchema.CalendarColumns.TABLE_NAME;
	    	break;
	    case ROUTE:
	    	table = DatabaseSchema.RoutesColumns.TABLE_NAME;
	    	break;
	    case TRIP:
	    	table = DatabaseSchema.TripsColumns.TABLE_NAME;
	    	break;
	    case STOP_TIME:
	    	table = DatabaseSchema.StopTimesColumns.TABLE_NAME;
	    	break;
	    case STOP_TIME_TRIP_ROUTE:
	    	table = DatabaseSchema.StopTimesColumns.TABLE_NAME + ", " + 
	    			DatabaseSchema.TripsColumns.TABLE_NAME + ", " +
	    			DatabaseSchema.RoutesColumns.TABLE_NAME;
	    	break;
	    case TRIP_SHAPE:
	    	table = DatabaseSchema.TripsColumns.TABLE_NAME + " , " +
	    			DatabaseSchema.ShapesColumns.TABLE_NAME;
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	    queryBuilder.setTables(table);
	    
	    
        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = DatabaseSchema.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        
        // Opens the database object in "read" mode, since no writes need to be done.
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        /*
         * Performs the query. If no problems occur trying to read the database, then a Cursor
         * object is returned; otherwise, the cursor variable contains null. If no records were
         * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
         */
        queryBuilder.setDistinct(true);
        Cursor c = queryBuilder.query(
            db,            // The database to query
            projection,    // The columns to return from the query
            selection,     // The columns for the where clause
            selectionArgs, // The values for the where clause
            null,          // don't group the rows
            null,          // don't filter by row groups
            orderBy        // The sort order
        );
	    
        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// Nothing to do, does not allow update
		return 0;
	}
}