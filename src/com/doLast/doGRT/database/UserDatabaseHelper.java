package com.doLast.doGRT.database;

import com.doLast.doGRT.database.DatabaseSchema.UserBusStopsColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    // The Database from user database
    private static String DB_PATH = "/data/data/com.doLast.doGRT/databases/"; 
    private static String DB_NAME = "USER.sqlite";
    private static final int DB_VERSION = 2;
    
    private final String CREATE_TABLE = "create table " + UserBusStopsColumns.TABLE_NAME +
    		" (" + UserBusStopsColumns.USER_ID + " integer primary key autoincrement, " +
    		UserBusStopsColumns.STOP_ID + " integer, " +
    		UserBusStopsColumns.TITLE + " text not null);";
    
    public UserDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	    if (newVersion > oldVersion)
	        Log.v("Database Upgrade", "Database version higher than old.");
	    db.execSQL("DROP TABLE IF EXISTS " + UserBusStopsColumns.TABLE_NAME);
	    onCreate(db);
	}
}
