package com.doLast.doGRT.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;

public class UserDatabaseProvider extends ContentProvider {
	private UserDatabaseHelper mOpenHelper;
	
	private static final int USER = 100;
	private static final int USER_ID = 110;
	
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
    	sUriMatcher.addURI(DatabaseSchema.USER_AUTHORITY, DatabaseSchema.UserBusStopsColumns.TABLE_NAME, USER);
    	sUriMatcher.addURI(DatabaseSchema.USER_AUTHORITY, DatabaseSchema.UserBusStopsColumns.TABLE_NAME + "/#", USER_ID);
    }
    
	@Override
	public String getType(Uri uri) {
		return null;
	}
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	    int rows_deleted = 0;
	    // Match Uri
	    switch (sUriMatcher.match(uri)) {
	    case USER:
	    	rows_deleted = db.delete(UserBusStopsColumns.TABLE_NAME, selection, selectionArgs);
	    	break;
	    case USER_ID:
	        String id = uri.getLastPathSegment();
	        // Deal with having selection or not
	        if (TextUtils.isEmpty(selection)) {
	          rows_deleted = db.delete(UserBusStopsColumns.TABLE_NAME,
	              UserBusStopsColumns.USER_ID + "=" + id, 
	              null);
	        } else {
		          rows_deleted = db.delete(UserBusStopsColumns.TABLE_NAME,
			              UserBusStopsColumns.USER_ID + "=" + id +
	              " and " + selection, selectionArgs);
	        }
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
		return rows_deleted;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// Very much similar to delete
	    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	    int rows_updated = 0;
	    // Match Uri
	    switch (sUriMatcher.match(uri)) {
	    case USER:
	    	rows_updated = db.update(UserBusStopsColumns.TABLE_NAME, values, selection, selectionArgs);
	    	break;
	    case USER_ID:
	        String id = uri.getLastPathSegment();
	        // Deal with having selection or not
	        if (TextUtils.isEmpty(selection)) {
	          rows_updated = db.update(UserBusStopsColumns.TABLE_NAME, values,
	              UserBusStopsColumns.USER_ID + "=" + id, 
	              null);
	        } else {
		          rows_updated = db.update(UserBusStopsColumns.TABLE_NAME, values,
			              UserBusStopsColumns.USER_ID + "=" + id +
	              " and " + selection, selectionArgs);
	        }
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
		return rows_updated;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	    long id = 0;
	    // Match Uri
	    switch (sUriMatcher.match(uri)) {
	    case USER:
	    	id = db.insert(UserBusStopsColumns.TABLE_NAME, null, values);
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(UserBusStopsColumns.TABLE_NAME + "/" + id);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new UserDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	    // Using SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	    
	    // Select a table, well now it's always user
	    String table = UserBusStopsColumns.TABLE_NAME;
	    switch(sUriMatcher.match(uri)) {
	    case USER:
	    	// No filter
	    	break;
	    case USER_ID:
	        queryBuilder.appendWhere(UserBusStopsColumns.USER_ID + "="
	                + uri.getLastPathSegment());
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
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
}