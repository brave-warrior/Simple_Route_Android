/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Provides work with database
 * 
 * @author Dmytro Khmelenko
 * 
 */
public final class DbEngine {

	private static final String DATABASE_NAME = "routes.db";

	// tables
	private static final String ROUTE_TABLE = "route";
	private static final String STEPS_TABLE = "steps";
	private static final String LOCATIONS_TABLE = "locations";

	// fields for route table
	private static final String KEY_ROWID = "_id";
	private static final String KEY_ROUTE_DIST = "distance";
	private static final String KEY_ROUTE_DURATION = "duration";
	private static final String KEY_ROUTE_END_ADDR = "end_addr";
	private static final String KEY_ROUTE_END_LOC = "end_loc";
	private static final String KEY_ROUTE_START_ADDR = "start_addr";
	private static final String KEY_ROUTE_START_LOC = "start_loc";
	private static final String KEY_BOUNDS_TL = "bounds_tl";
	private static final String KEY_BOUNDS_BR = "bounds_br";
	private static final String KEY_ROUTE_POLYLINE = "polyline";
	private static final String KEY_ROUTE_COPYRIGHTS = "copyrights";
	private static final String KEY_ROUTE_SUMMARY = "summary";
	private static final String KEY_ROUTE_WARNINGS = "warnings";

	// fields for steps table
	private static final String KEY_STEP_ROUTE = "route_id";
	private static final String KEY_STEP_DIST = "distance";
	private static final String KEY_STEP_DURATION = "duration";
	private static final String KEY_STEP_START_LOC = "start_loc";
	private static final String KEY_STEP_END_LOC = "end_loc";
	private static final String KEY_STEP_TRAVEL_MODE = "travel_mode";
	private static final String KEY_STEP_INSTRUCTIONS = "instr";
	private static final String KEY_STEP_POINTS = "points";

	// fields for Location table
	private static final String KEY_LOCATION_LAT = "lat";
	private static final String KEY_LOCATION_LNG = "lng";

	/**
	 * Database helper class
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	public static class DbEngineHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;

		// creation table of schedule
		private static final String ROUTE_TABLE_CREATE = "create table IF NOT EXISTS "
				+ ROUTE_TABLE
				+ " ("
				+ KEY_ROWID
				+ " integer primary key autoincrement, "
				+ KEY_ROUTE_DIST
				+ " integer, "
				+ KEY_ROUTE_DURATION
				+ " integer, "
				+ KEY_ROUTE_END_ADDR
				+ " text, "
				+ KEY_ROUTE_END_LOC
				+ " integer, "
				+ KEY_ROUTE_START_ADDR
				+ " text, "
				+ KEY_ROUTE_START_LOC
				+ " integer, "
				+ KEY_BOUNDS_TL
				+ " integer, "
				+ KEY_BOUNDS_BR
				+ " integer, "
				+ KEY_ROUTE_POLYLINE
				+ " text not null, "
				+ KEY_ROUTE_COPYRIGHTS
				+ " text, "
				+ KEY_ROUTE_SUMMARY
				+ " text, " + KEY_ROUTE_WARNINGS + " text);";

		private static final String STEPS_TABLE_CREATE = "create table IF NOT EXISTS "
				+ STEPS_TABLE
				+ " ("
				+ KEY_ROWID
				+ " integer primary key autoincrement, "
				+ KEY_STEP_ROUTE
				+ " integer, "
				+ KEY_STEP_DIST
				+ " integer, "
				+ KEY_STEP_DURATION
				+ " integer, "
				+ KEY_STEP_END_LOC
				+ " integer, "
				+ KEY_STEP_START_LOC
				+ " integer, "
				+ KEY_STEP_TRAVEL_MODE
				+ " text, "
				+ KEY_STEP_INSTRUCTIONS
				+ " text, " + KEY_STEP_POINTS + " text);";

		private static final String LOCATION_TABLE_CREATE = "create table IF NOT EXISTS "
				+ LOCATIONS_TABLE
				+ " ("
				+ KEY_ROWID
				+ " integer primary key autoincrement, "
				+ KEY_LOCATION_LAT
				+ " integer, " + KEY_LOCATION_LNG + " integer);";

		/**
		 * Constructor
		 * 
		 * @param aContext
		 *            Context
		 */
		public DbEngineHelper(Context aContext) {
			super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/*
		 * Called during creation of the database
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(ROUTE_TABLE_CREATE);
			database.execSQL(STEPS_TABLE_CREATE);
			database.execSQL(LOCATION_TABLE_CREATE);
		}

		/*
		 * Called during an upgrade of the database
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(DbEngineHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			onCreate(database);
		}
	}

	private Context iContext;
	private SQLiteDatabase iDatabase;
	private DbEngineHelper iDbHelper;

	/**
	 * Constructor
	 * 
	 * @param aContext
	 *            Context
	 */
	public DbEngine(Context aContext) {
		this.iContext = aContext;
		open();
	}

	/**
	 * Opens database
	 * 
	 * @throws SQLException
	 *             Exception if error
	 */
	private void open() throws SQLiteException {
		iDbHelper = new DbEngineHelper(iContext);
		iDatabase = iDbHelper.getWritableDatabase();
	}

	/**
	 * Closes DB
	 */
	public void close() {
		iDbHelper.close();
	}

	/**
	 * Create a new route item
	 * 
	 * @param aRoute
	 *            Route
	 * @return Row id, otherwise return a -1 to indicate failure
	 */
	public long insertRoute(Route aRoute) {

		// Insert locations first
		long endLocId = insertLocation(aRoute.iEndLocation);
		long startLocId = insertLocation(aRoute.iStartLocation);
		long boundsTlId = insertLocation(aRoute.iBounds.getNorthEast());
		long boundsBrId = insertLocation(aRoute.iBounds.getSouthWest());

		// insert route data
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROUTE_DIST, aRoute.iDistance);
		initialValues.put(KEY_ROUTE_DURATION, aRoute.iDuration);
		initialValues.put(KEY_ROUTE_END_ADDR, aRoute.iEndAddress);
		initialValues.put(KEY_ROUTE_END_LOC, endLocId);
		initialValues.put(KEY_ROUTE_START_ADDR, aRoute.iStartAddress);
		initialValues.put(KEY_ROUTE_START_LOC, startLocId);
		initialValues.put(KEY_BOUNDS_TL, boundsTlId);
		initialValues.put(KEY_BOUNDS_BR, boundsBrId);
		initialValues.put(KEY_ROUTE_POLYLINE, aRoute.iEncodedPolyline);
		initialValues
				.put(KEY_ROUTE_COPYRIGHTS, aRoute.iDetails.getCopyrights());
		initialValues.put(KEY_ROUTE_SUMMARY, aRoute.iDetails.getSummary());
		initialValues.put(KEY_ROUTE_WARNINGS, aRoute.iDetails.getWarnings());

		long id = iDatabase.insert(ROUTE_TABLE, null, initialValues);

		// insert steps
		for (RouteStep step : aRoute.iSteps) {
			insertStep(step, id);
		}

		return id;
	}

	/**
	 * Inserts location to the DB
	 * 
	 * @param aLocation
	 *            Location for storing
	 * @return Row id, otherwise return a -1 to indicate failure
	 */
	private long insertLocation(Location aLocation) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LOCATION_LAT, aLocation.getLatitude());
		initialValues.put(KEY_LOCATION_LNG, aLocation.getLongitude());

		long id = iDatabase.insert(LOCATIONS_TABLE, null, initialValues);

		return id;
	}

	/**
	 * Inserts route steps to the DB
	 * 
	 * @param aStep
	 *            Step for storing
	 * @param aRouteId
	 *            Related route
	 * @return Row id, otherwise return a -1 to indicate failure
	 */
	private long insertStep(RouteStep aStep, long aRouteId) {
		// insert location first
		long endLocId = insertLocation(aStep.getEndLocation());
		long startLocId = insertLocation(aStep.getStartLocation());

		// insert step values
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_STEP_ROUTE, aRouteId);
		initialValues.put(KEY_STEP_DIST, aStep.getDistance());
		initialValues.put(KEY_STEP_DURATION, aStep.getDuration());
		initialValues.put(KEY_STEP_END_LOC, endLocId);
		initialValues.put(KEY_STEP_START_LOC, startLocId);
		initialValues.put(KEY_STEP_TRAVEL_MODE, aStep.getTravelMode());
		initialValues.put(KEY_STEP_INSTRUCTIONS, aStep.getInstructions());
		initialValues.put(KEY_STEP_POINTS, aStep.getPoints());

		long id = iDatabase.insert(STEPS_TABLE, null, initialValues);

		return id;
	}

	/**
	 * Deletes all tables
	 * 
	 * @return True if succeed. Otherwise false.
	 */
	public boolean deleteAll() {
		boolean result = iDatabase.delete(LOCATIONS_TABLE, null, null) > 0;
		result &= iDatabase.delete(STEPS_TABLE, null, null) > 0;
		result &= iDatabase.delete(ROUTE_TABLE, null, null) > 0;
		return result;
	}

	/**
	 * Gets the list of all routes in the DB
	 * 
	 * @return List of routes
	 */
	public List<Route> getAllRoutes() {
		String[] routeColumns = { KEY_ROWID, KEY_ROUTE_DIST,
				KEY_ROUTE_DURATION, KEY_ROUTE_END_ADDR, KEY_ROUTE_END_LOC,
				KEY_ROUTE_START_ADDR, KEY_ROUTE_START_LOC, KEY_BOUNDS_TL,
				KEY_BOUNDS_BR, KEY_ROUTE_POLYLINE, KEY_ROUTE_COPYRIGHTS,
				KEY_ROUTE_SUMMARY, KEY_ROUTE_WARNINGS };

		Cursor routeCursor = iDatabase.query(ROUTE_TABLE, routeColumns, null,
				null, null, null, null);

		if (routeCursor.getCount() == 0) {
			return Collections.emptyList();
		}

		routeCursor.moveToFirst();
		List<Route> routes = new ArrayList<Route>();

		do {
			// getting item id
			int columnIndex = routeCursor.getColumnIndex(KEY_ROWID);
			int id = routeCursor.getInt(columnIndex);
			routes.add(getRoute(id));
		} while (routeCursor.moveToNext());

		routeCursor.close();

		return routes;
	}

	/**
	 * Gets object Route from the DB by id
	 * 
	 * @param aItemId
	 *            Id for search
	 * @return Filled object Route
	 */
	public Route getRoute(int aItemId) {
		String[] columns = { KEY_ROWID, KEY_ROUTE_DIST, KEY_ROUTE_DURATION,
				KEY_ROUTE_END_ADDR, KEY_ROUTE_END_LOC, KEY_ROUTE_START_ADDR,
				KEY_ROUTE_START_LOC, KEY_BOUNDS_TL, KEY_BOUNDS_BR,
				KEY_ROUTE_POLYLINE, KEY_ROUTE_COPYRIGHTS, KEY_ROUTE_SUMMARY,
				KEY_ROUTE_WARNINGS };

		Cursor cursor = iDatabase.query(true, ROUTE_TABLE, columns, KEY_ROWID
				+ "=" + aItemId, null, null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return null;
		}

		Route route = new Route();

		// getting route distance
		int columnIndex = cursor.getColumnIndex(KEY_ROUTE_DIST);
		route.iDistance = cursor.getInt(columnIndex);

		// getting route duration
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_DURATION);
		route.iDuration = cursor.getInt(columnIndex);

		// getting route end address
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_END_ADDR);
		route.iEndAddress = cursor.getString(columnIndex);

		// getting route end location
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_END_LOC);
		int endLocationId = cursor.getInt(columnIndex);
		route.iEndLocation = getLocation(endLocationId);

		// getting route start address
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_START_ADDR);
		route.iStartAddress = cursor.getString(columnIndex);

		// getting route start location
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_START_LOC);
		int startLocationId = cursor.getInt(columnIndex);
		route.iStartLocation = getLocation(startLocationId);
		
		// getting route polyline
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_POLYLINE);
		route.iEncodedPolyline = cursor.getString(columnIndex);

		// getting route bounds TL
		columnIndex = cursor.getColumnIndex(KEY_BOUNDS_TL);
		int boundsTlId = cursor.getInt(columnIndex);
		Location northEast = getLocation(boundsTlId);

		// getting route bounds BR
		columnIndex = cursor.getColumnIndex(KEY_BOUNDS_BR);
		int boundsBrId = cursor.getInt(columnIndex);
		Location southWest = getLocation(boundsBrId);

		route.iBounds = new RouteBounds(northEast, southWest);

		// getting route copyrights
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_COPYRIGHTS);
		String copyrights = cursor.getString(columnIndex);

		// getting route summary
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_SUMMARY);
		String summary = cursor.getString(columnIndex);

		// getting route warnings
		columnIndex = cursor.getColumnIndex(KEY_ROUTE_WARNINGS);
		String warnings = cursor.getString(columnIndex);

		// filling route details
		RouteDetails details = new RouteDetails();
		details.setCopyrights(copyrights);
		details.setSummary(summary);
		details.setWarnings(warnings);
		route.iDetails = details;

		// getting route steps
		columnIndex = cursor.getColumnIndex(KEY_ROWID);
		int routeId = cursor.getInt(columnIndex);
		route.iSteps = getSteps(routeId);

		cursor.close();

		return route;
	}

	/**
	 * Gets the list of {@link RouteStep} objects from the DB by Route ID
	 * 
	 * @param aRouteId
	 *            Route ID
	 * @return List of RouteSteps
	 */
	private List<RouteStep> getSteps(int aRouteId) {
		String[] columns = { KEY_ROWID, KEY_STEP_ROUTE, KEY_STEP_DIST,
				KEY_STEP_DURATION, KEY_STEP_START_LOC, KEY_STEP_END_LOC,
				KEY_STEP_TRAVEL_MODE, KEY_STEP_INSTRUCTIONS, KEY_STEP_POINTS };

		Cursor cursor = iDatabase.query(true, STEPS_TABLE, columns,
				KEY_STEP_ROUTE + "=" + aRouteId, null, null, null, null, null);

		if (cursor.getCount() == 0) {
			return Collections.emptyList();
		}

		cursor.moveToFirst();

		List<RouteStep> steps = new ArrayList<RouteStep>();

		do {
			// getting step end location
			int columnIndex = cursor.getColumnIndex(KEY_STEP_END_LOC);
			int endLocationId = cursor.getInt(columnIndex);
			Location endLocation = getLocation(endLocationId);

			// getting route start location
			columnIndex = cursor.getColumnIndex(KEY_STEP_START_LOC);
			int startLocationId = cursor.getInt(columnIndex);
			Location startLocation = getLocation(startLocationId);

			RouteStep.Builder step = new RouteStep.Builder(startLocation,
					endLocation);

			// getting step distance
			columnIndex = cursor.getColumnIndex(KEY_STEP_DIST);
			int distance = cursor.getInt(columnIndex);
			step.distance(distance);

			// getting step duration
			columnIndex = cursor.getColumnIndex(KEY_STEP_DURATION);
			int duration = cursor.getInt(columnIndex);
			step.duration(duration);

			// getting step travel mode
			columnIndex = cursor.getColumnIndex(KEY_STEP_TRAVEL_MODE);
			String travelMode = cursor.getString(columnIndex);
			step.travelMode(travelMode);

			// getting route duration
			columnIndex = cursor.getColumnIndex(KEY_STEP_INSTRUCTIONS);
			String instructions = cursor.getString(columnIndex);
			step.instructions(instructions);

			// getting route duration
			columnIndex = cursor.getColumnIndex(KEY_STEP_POINTS);
			String points = cursor.getString(columnIndex);
			step.points(points);

			steps.add(step.build());

		} while (cursor.moveToNext());

		cursor.close();

		return steps;
	}

	/**
	 * Gets the location from the store
	 * 
	 * @param aLocationId
	 *            Location ID in DB
	 * @return Location object
	 */
	private Location getLocation(int aLocationId) {

		String[] columns = { KEY_ROWID, KEY_LOCATION_LAT, KEY_LOCATION_LNG };

		Cursor cursor = iDatabase.query(true, LOCATIONS_TABLE, columns,
				KEY_ROWID + "=" + aLocationId, null, null, null, null, null);

		Location location = new Location(LocationManager.GPS_PROVIDER);
		if (cursor.getCount() == 0) {
			return location;
		}

		cursor.moveToFirst();

		// latitude
		int columnIndex = cursor.getColumnIndex(KEY_LOCATION_LAT);
		double latitude = cursor.getDouble(columnIndex);
		location.setLatitude(latitude);

		// longitude
		columnIndex = cursor.getColumnIndex(KEY_LOCATION_LNG);
		double longitude = cursor.getDouble(columnIndex);
		location.setLongitude(longitude);

		cursor.close();

		return location;
	}

}
