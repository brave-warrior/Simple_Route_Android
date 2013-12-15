/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.store.City;
import com.store.Route;
import com.store.RouteBounds;
import com.store.RouteDetails;
import com.store.RouteStep;

/**
 * Parses different response packets
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class PacketParser {

	// keys for Cities
	private static final String KEY_STATUS = "status";
	private static final String KEY_PREDICTIONS = "predictions";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_ID = "id";

	// keys for Routes
	private static final String KEY_ROUTES = "routes";
	private static final String KEY_LEGS = "legs";
	private static final String KEY_STEPS = "steps";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_DURATION = "duration";
	private static final String KEY_HTML_INSTRUCTIONS = "html_instructions";
	private static final String KEY_TRAVEL_MODE = "travel_mode";
	private static final String KEY_POLYLINE = "polyline";
	private static final String KEY_TEXT = "text";
	private static final String KEY_VALUE = "value";
	private static final String KEY_POINTS = "points";
	private static final String KEY_START_LOCATION = "start_location";
	private static final String KEY_END_LOCATION = "end_location";
	private static final String KEY_LATITUDE = "lat";
	private static final String KEY_LONGITUDE = "lng";
	private static final String KEY_START_ADDRESS = "start_address";
	private static final String KEY_END_ADDRESS = "end_address";
	private static final String KEY_SUMMARY = "summary";
	private static final String KEY_COPYRIGHTS = "copyrights";
	private static final String KEY_OVERVIEW_POLYLINE = "overview_polyline";
	private static final String KEY_BOUNDS = "bounds";
	private static final String KEY_NORTHEAST = "northeast";
	private static final String KEY_SOUTHWEST = "southwest";
	private static final String KEY_WARNINGS = "warnings";

	/**
	 * Parses cities from the response string
	 * 
	 * @param aResponse
	 *            Response
	 * @return List of cities
	 */
	public static List<City> parseCities(String aResponse) {
		List<City> results = new ArrayList<City>();
		try {
			JSONObject root = new JSONObject(aResponse);

			JSONArray predictions = root.getJSONArray(KEY_PREDICTIONS);
			// go through all predictions
			for (int i = 0; i < predictions.length(); i++) {
				JSONObject item = predictions.getJSONObject(i);
				String description = item.getString(KEY_DESCRIPTION);
				String id = item.getString(KEY_ID);

				City city = new City(id, description);
				results.add(city);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// if the results is empty,
		// return empty collection
		if (results.isEmpty()) {
			results = Collections.emptyList();
		}

		return results;
	}

	/**
	 * Parses the response with the routes
	 * 
	 * @param aResponse
	 *            Response string
	 * @return List of routes
	 */
	public static List<Route> parseRoutes(String aResponse) {
		List<Route> routes = new ArrayList<Route>();

		try {
			JSONObject root = new JSONObject(aResponse);

			JSONArray routesArray = root.getJSONArray(KEY_ROUTES);
			for (int i = 0; i < routesArray.length(); i++) {
				// go through all routes
				JSONObject routeItem = routesArray.getJSONObject(i);
				Route route = parseRoute(routeItem);

				routes.add(route);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (routes.isEmpty()) {
			routes = Collections.emptyList();
		}

		return routes;
	}

	/**
	 * Parses route from a JSONObject
	 * 
	 * @param aRoute
	 *            Object for parsing
	 * @return Route object
	 * @throws JSONException
	 *             If parsing error occurred
	 */
	private static Route parseRoute(JSONObject aRoute) throws JSONException {
		Route route = new Route();

		JSONArray legs = aRoute.getJSONArray(KEY_LEGS);

		// NOTE: According to the Google Maps API:
		// "A route with no waypoints will contain
		// exactly one leg within the legs array"
		JSONObject legObj = legs.getJSONObject(0);

		JSONArray steps = legObj.getJSONArray(KEY_STEPS);
		route.iSteps = parseRouteSteps(steps);

		route.iDistance = parseIntFromObject(legObj, KEY_DISTANCE, KEY_VALUE);
		route.iDuration = parseIntFromObject(legObj, KEY_DURATION, KEY_VALUE);

		route.iStartAddress = legObj.optString(KEY_START_ADDRESS);
		route.iStartLocation = parseLocation(legObj, KEY_START_LOCATION);

		route.iEndAddress = legObj.optString(KEY_END_ADDRESS);
		route.iEndLocation = parseLocation(legObj, KEY_END_LOCATION);
		
		route.iEncodedPolyline = parseStringFromObject(aRoute,
				KEY_OVERVIEW_POLYLINE, KEY_POINTS);

		route.iBounds = parseRouteBounds(aRoute);
		route.iDetails = parseRouteDetails(aRoute);

		return route;
	}

	/**
	 * Parses route details
	 * 
	 * @param aRoute
	 *            Route
	 * @return Route details
	 */
	private static RouteDetails parseRouteDetails(JSONObject aRoute) {
		String summary = aRoute.optString(KEY_SUMMARY);
		String copyrights = aRoute.optString(KEY_COPYRIGHTS);

		// Handle warnings
		JSONArray warningArr = aRoute.optJSONArray(KEY_WARNINGS);
		StringBuilder warnings = new StringBuilder();
		for (int i = 0; i < warningArr.length(); i++) {
			String warning = warningArr.optString(i);
			warnings.append(warning);
			warnings.append("\n");
		}

		RouteDetails details = new RouteDetails();
		details.setCopyrights(copyrights);
		details.setSummary(summary);
		details.setWarnings(warnings.toString());

		return details;
	}

	/**
	 * Parses route bounds from the JSON object
	 * 
	 * @param aRoute
	 *            Route
	 * @return Bounds for the route
	 * @throws JSONException
	 *             If parsing error occurred
	 */
	private static RouteBounds parseRouteBounds(JSONObject aRoute)
			throws JSONException {

		JSONObject boundsObj = aRoute.getJSONObject(KEY_BOUNDS);
		Location northEast = parseLocation(boundsObj, KEY_NORTHEAST);
		Location southwest = parseLocation(boundsObj, KEY_SOUTHWEST);

		RouteBounds bounds = new RouteBounds(northEast, southwest);

		return bounds;
	}

	/**
	 * Parses route steps from the JSON array
	 * 
	 * @param aSteps
	 *            JSON array for parsing
	 * @return List of route steps
	 */
	private static List<RouteStep> parseRouteSteps(JSONArray aSteps) {

		List<RouteStep> steps = new ArrayList<RouteStep>();

		for (int i = 0; i < aSteps.length(); i++) {
			JSONObject item = aSteps.optJSONObject(i);

			if (item != null) {
				// route data
				int distance = parseIntFromObject(item, KEY_DISTANCE, KEY_VALUE);
				int duration = parseIntFromObject(item, KEY_DURATION, KEY_VALUE);
				String instruction = item.optString(KEY_HTML_INSTRUCTIONS);
				String travelMode = item.optString(KEY_TRAVEL_MODE);

				// points
				String polyline = parseStringFromObject(item, KEY_POLYLINE,
						KEY_POINTS);

				// locations
				Location start = parseLocation(item, KEY_START_LOCATION);
				Location end = parseLocation(item, KEY_END_LOCATION);

				// build step
				RouteStep step = new RouteStep.Builder(start, end)
						.distance(distance).duration(duration)
						.instructions(instruction).travelMode(travelMode)
						.points(polyline).build();

				steps.add(step);
			}
		}

		return steps;
	}

	/**
	 * Parses string from the JSON object in a parent object
	 * 
	 * @param aParent
	 *            Parent object
	 * @param aObjectName
	 *            Object name for parsing
	 * @param aItemName
	 *            Item in object for parsing
	 * @return Parsed or empty string
	 */
	private static String parseStringFromObject(JSONObject aParent,
			String aObjectName, String aItemName) {
		JSONObject object = aParent.optJSONObject(aObjectName);
		String result = "";
		if (object != null) {
			result = object.optString(aItemName);
		}
		return result;
	}

	/**
	 * Parses integer from the JSON object in a parent object
	 * 
	 * @param aParent
	 *            Parent object
	 * @param aObjectName
	 *            Object name for parsing
	 * @param aItemName
	 *            Item in object for parsing
	 * @return Parsed integer value or 0
	 */
	private static int parseIntFromObject(JSONObject aParent,
			String aObjectName, String aItemName) {
		JSONObject object = aParent.optJSONObject(aObjectName);
		int result = 0;
		if (object != null) {
			result = object.optInt(aItemName);
		}
		return result;
	}

	/**
	 * Parses location from the JSON object in a parent object
	 * 
	 * @param aParent
	 *            Parent object
	 * @param aObjectName
	 *            Object name
	 * @return Location object
	 */
	private static Location parseLocation(JSONObject aParent, String aObjectName) {
		Location location = new Location(LocationManager.GPS_PROVIDER);

		JSONObject object = aParent.optJSONObject(aObjectName);
		if (object != null) {
			double lat = object.optDouble(KEY_LATITUDE, 0.0);
			double lng = object.optDouble(KEY_LONGITUDE, 0.0);

			location.setLatitude(lat);
			location.setLongitude(lng);
		}
		return location;
	}

	/**
	 * Parses response status from the response
	 * 
	 * @param aResponse
	 *            Response
	 * @return Response status object
	 */
	public static ResponseStatus parseStatus(String aResponse) {
		ResponseStatus responseStatus = null;
		try {
			JSONObject root = new JSONObject(aResponse);
			String status = root.optString(KEY_STATUS);

			responseStatus = new ResponseStatus(status);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return responseStatus;
	}

	/**
	 * Decodes polyline from the encoded string
	 * 
	 * @param aEncoded
	 *            Encoded string
	 * @return List of points
	 */
	public static List<LatLng> decodePoly(String aEncoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = aEncoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = aEncoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = aEncoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

}
