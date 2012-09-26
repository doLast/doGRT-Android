package com.doLast.doGRT.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The name of table and field should match the database
 * */

public final class DatabaseSchema {
	public static final String AUTHORITY = "com.doLast.doGRT.database";
	public static final String URI_PREFIX = "content://" + AUTHORITY;		
    // The default sort order for this table
    public static final String DEFAULT_SORT_ORDER = "";
    
    public static final String STOP_TIME_TRIP_ROUTE_JOINT = "stop_time_trip_route_joint";
    public static final Uri STTRJ_CONTENT_URI = Uri.parse(URI_PREFIX + "/" + STOP_TIME_TRIP_ROUTE_JOINT);
    
	// This class cannot be instantiated
	private DatabaseSchema() {}
	/** Definition for convenience use */
	
	public static final class UserBusStopsColumns implements BaseColumns {
		public static final String TABLE_NAME = "user";
		// This class cannot be instantiated
		private UserBusStopsColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
		
        /**
         * User id (Primary Key)
         * Type: INTEGER
         */
        public static final String USER_ID = "user_id";
	}
	
	public static final class AgencyColumns implements BaseColumns {
		public static final String TABLE_NAME = "agency";
		
		// This class cannot be instantiated
		private AgencyColumns() {}
		
		// The content:// style URL for this table
		public static final Uri CONETENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
		
		// I don't know what the following two are. I am just putting it there
		// so I can revisit them later
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;
		
        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;       
        
        // The default sort order for this table
        public static final String DEFAULT_SORT_ORDER = "";
        
        /** 
         * Name of the agency 
         * Type: TEXT
         */
        public static final String NAME = "name";
        
        /**
         * Website of the agency 
         * Type: TEXT
         */
        public static final String WEBSITE = "website";
        
        /**
         * Timezone of the agency
         * Type: TEXT
         */
        public static final String TIMEZONE = "timezone";
        
        /**
         * Id of the agency
         * Type: TEXT
         */
        public static final String ID = "id";
        
        /**
         * Language of the agency
         * Type: TEXT
         */
        public static final String LANG = "language";
        
        /**
         * Phone number of the agency
         * Type: TEXT
         */
        public static final String PHONE = "phone";
        
        /**
         * Fare information of the agency
         * Type: TEXT
         */
        public static final String FARE = "fare";
	}

	public static final class CalendarDatesColumns implements BaseColumns {
		public static final String TABLE_NAME = "calendar_dates";
		
		// This class cannot be instantiated
		private CalendarDatesColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "";
        
        /**
         * Service id
         * Type: TEXT
         */
        public static final String SERVICE_ID = "service_id";
        
        /**
         * Date 
         * Type: TEXT
         */
        public static final String DATE = "date";
        
        /**
         * Exception Type
         * Type: INTEGER
         */
        public static final String EXCEPTION_TYPE = "exception_type";
	}
	
	public static final class CalendarColumns implements BaseColumns {
		public static final String TABLE_NAME = "Calendar";
		
		// This class cannot be instantiated
		private CalendarColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "";
		
        /**
         * Service id (Primary Key)
         * Type: VARCHAR
         */
        public static final String SERVICE_ID = "serviceId";
        
        /**
         * Monday, Tuesday, Wednesday, Thursday, Friday, Saturday and Sunday
         * Type: BOOLEAN (0-available, 1-unavailable)
         */
        public static final String MONDAY = "monday";
        public static final String TUESDAY = "tuesday";
        public static final String WEDNESDAY = "wednesday";
        public static final String THURSDAY = "thursday";
        public static final String FRIDAY = "fridday";
        public static final String SATURDAY = "saturday";
        public static final String SUNDAY = "sunday";
        
        /**
         * Start date and end date (valid period of the schedule)
         * Type: INTEGER (Example: 14:30:00 -> 143000) 
         */
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        
	}
	
	public static final class FareColumns implements BaseColumns {
		public static final String TABLE_NAME = "fare";
		
		// This class cannot be instantiated
		private FareColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "";
        
        /**
         * Fare id
         * Type: TEXT
         */
        public static final String FARE_ID = "fare_id";
        
        /**
         * Price
         * Type: TEXT (can be double)
         */
        public static final String PRICE = "price";
        
        /**
         * Currency type
         * Type: TEXT
         */
        public static final String CURRENCY = "currency";
        
        /**
         * Payment method
         * Type: INTEGER (0-paid on board, 1-paid before boarding)
         */
        public static final String PAYMENT = "payment";
        
        /**
         * Transfer times
         * Type: INTEGER (0-no transfer, 1-may transfer once, 2-twice, (empty)-unlimited) 
         */
        public static final String TRANSFER = "transfer";
        
        /**
         * Transfer duration
         * TYPE: INTEGER (in seconds)
         */
        public static final String TRANSFER_DURATION = "transfer_duration";
	}
	
	public static final class RoutesColumns implements BaseColumns {
		public static final String TABLE_NAME = "Route";
		
		// This class cannot be instantiated
		private RoutesColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * Route id (Primary Key)
         * Type: VARCHAR
         */
        public static final String ROUTE_ID = "routeId";
        
        /**
         * Agency id
         * Type: TEXT
         */
        public static final String AGENCY_ID = "agency_id";
        
        /**
         * Route short name
         * Type: VARCHAR
         */
        public static final String SHORT_NAME = "routeShortName";
        
        /**
         * Route long name
         * Type: VARCHAR
         */
        public static final String LONG_NAME = "routeLongName";
        
        /**
         * Route description
         * Type: TEXT
         */
        public static final String DESCRIPTION = "description";
        
        /**
         * Route type 
         * Type: INTEGER
         */
        public static final String TYPE = "type";
        
        /**
         * Route url
         * Type: TEXT
         */
        public static final String URL = "url";
        
        /**
         * Route colour
         * Type: TEXT
         */
        public static final String COLOUR = "colour";
        
        /**
         * Route Text Colour
         * Type: TEXT
         */
        public static final String TEXT_COLOUR = "text_colour";
        
	}
	
	public static final class ShapesColumns implements BaseColumns {
		public static final String TABLE_NAME = "shapes";
		
		// This class cannot be instantiated
		private ShapesColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * Shape id
         * Type: TEXT
         */
        public static final String SHAPE_ID = "shape_id";
        
        /**
         * Shape point's latitude
         * Type: TEXT 
         */
        public static final String SHAPE_LAT = "shape_latitude";
        
        /**
         * Shape point's longitude
         * Type: TEXT
         */
        public static final String SHAPE_LON = "shape_longitude";
        
        /**
         * Shape point's sequence
         * Type: INTEGER 
         */
        public static final String SHAPE_SEQ = "shape_sequence";
        
        /**
         * Shape distance traveled
         * Type: TEXT
         */
        public static final String SHAPE_DIST = "shape_distance";
	}
	
	public static final class StopTimesColumns implements BaseColumns {
		public static final String TABLE_NAME = "StopTime";
		
		// This class cannot be instantiated
		private StopTimesColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * Trip id (Primary Key)
         * Type: INTEGER
         */
        public static final String TRIP_ID = "tripId";
        
        /**
         * Arrival time
         * Type: INTEGER
         */
        public static final String ARRIVAL = "arrivalTime";
        
        /**
         * Departure time
         * Type: INTEGER
         */
        public static final String DEPART = "departureTime";
        
        /**
         * Stop id
         * Type: INTEGER
         */
        public static final String STOP_ID = "stopId";
                
        /**
         * Stop sequence (Primary Key)
         * Type: INTEGER
         */
        public static final String STOP_SEQ = "stopSequence";
        
        /**
         * Stop headsign
         * Type: TEXT
         */
        public static final String HEADSIGN = "headsign";
        
        /**
         * Pickup type
         * Type: INTEGER
         */
        public static final String PICKUP = "pickup";
        
        /**
         * Drop off type
         * Type: INTEGER
         */
        public static final String DROPOFF = "dropoff";
        
        /**
         * Shape distance traveled
         * Type: TEXT
         */
        public static final String DISTANCE = "distance";
	}
	
	public static final class StopsColumns implements BaseColumns {
		public static final String TABLE_NAME = "BusStop";
		
		// This class cannot be instantiated
		private StopsColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * Stop id (Primary Key)
         * Type: INTEGER
         */
        public static final String STOP_ID = "stopId";
        
        /**
         * Stop code
         * Type: TEXT
         */
        public static final String STOP_CODE = "stop_code";
        
        /**
         * Stop name
         * Type: VARCHAR 
         */
        public static final String STOP_NAME = "stopName";
        
        /**
         * Stop description
         * Type: TEXT
         */
        public static final String STOP_DESC = "stop_description";
        
        /**
         * Stop latitude
         * Type: DOUBLE
         */
        public static final String STOP_LAT = "stopLat";
        
        /**
         * Stop longitude
         * Type: DOUBLE
         */
        public static final String STOP_LON = "stopLon";
        
        /**
         * Zone id
         * Type: TEXT
         */
        public static final String ZONE_ID = "zone_id";
        
        /**
         * Stop url
         * Type: TEXT
         */
        public static final String STOP_URL = "stop_url";
        
        /**
         * Location type
         * Type: INTEGER
         */
        public static final String LOC_TYPE = "location_type";
        
        /**
         * Parent station
         * Type: INTEGER
         */
        public static final String PARENT_STATION = "parent_station";
        
        /**
         * Stop timezone
         * Type: TEXT
         */
        public static final String TIMEZONE = "timezone";
        
        /**
         * Stop wheelchair boarding
         * Type: INTEGER
         */
        public static final String WHEELCHAIR = "wheelchair";        
	}
	
	public static final class TripsColumns implements BaseColumns {
		public static final String TABLE_NAME = "Trip";
		
		// This class cannot be instantiated
		private TripsColumns() {}
		
	    /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + "/" + TABLE_NAME);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + TABLE_NAME;

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + TABLE_NAME;
        
        /**
         * Route id
         * Type: VARCHAR
         */
        public static final String ROUTE_ID = "routeId";
        
        /**
         * Service id
         * Type: VARCHAR
         */
        public static final String SERVICE_ID = "serviceId";
        
        /**
         * Trip id (Primary Key)
         * Type: INTEGER
         */
        public static final String TRIP_ID = "tripId";
        
        /**
         * Trip headsign
         * Type: VARCHAR
         */
        public static final String HEADSIGN = "tripHeadsign";
        
        /**
         * Trip short name
         * Type: TEXT
         */
        public static final String SHORT_NAME = "short_name";
        
        /**
         * Direction id
         * Type: INTEGER
         */
        public static final String DIRECTION_ID = "direction_id";
        
        /**
         * Block id
         * Type: TEXT
         */
        public static final String BLOCK_ID = "block_id";
        
        /**
         * Shape id
         * Type: TEXT
         */
        public static final String SHAPE_ID = "shape_id";
	}
}
