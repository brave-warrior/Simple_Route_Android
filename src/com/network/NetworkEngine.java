/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.network;

import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * Provides methods for working with the network
 * 
 * @author Dmytro Khmelenko
 * 
 */
public final class NetworkEngine {

	/** Network connection timeout */
	public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds

	/** URL for autocomplete places */
	public static final String AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

	/** URL for query autocomplete */
	public static final String QUERYAUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/queryautocomplete/json";

	/** URL for requesting directions */
	public static final String DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json";

	// request keys
	private static final String KEY_USER_INPUT = "input";
	private static final String KEY_SENSOR = "sensor";
	private static final String KEY_API_KEY = "key";

	private static final String KEY_ORIGIN = "origin";
	private static final String KEY_DESTINATION = "destination";
	private static final String KEY_LANGUAGE = "language";
	private static final String KEY_TRAVEL_MODE = "mode";

	/**
	 * Checks whether network is available or not
	 * 
	 * @return True is network is available. Otherwise, false
	 */
	public static boolean isNetworkAvailable(Context aContext) {
		ConnectivityManager cm = (ConnectivityManager) aContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * Requests the cities which are matched with the inputed city
	 * 
	 * @param aInputCity
	 *            City for search
	 * @param aApiKey
	 *            API key
	 * @return response string
	 */
	public String requestCities(String aInputCity, String aApiKey) {
		Uri uri = Uri.parse(AUTOCOMPLETE_URL).buildUpon()
				.appendQueryParameter(KEY_USER_INPUT, aInputCity)
				.appendQueryParameter(KEY_SENSOR, Boolean.toString(true))
				.appendQueryParameter(KEY_API_KEY, aApiKey).build();

		HttpGet httpget = new HttpGet(uri.toString());
		httpget.setHeader("Content-Type", "application/json");

		String response = doRequest(httpget);
		return response;
	}

	/**
	 * Requests the directions between 2 places
	 * 
	 * @param aFrom
	 *            Origin city
	 * @param aTo
	 *            Destination city
	 * @return response string
	 */
	public String requestDirections(String aFrom, String aTo, String aTravelMode) {
		
		// get the language code. If it's not found, 
		// default (english) is used 
		String lang = Locale.getDefault().getLanguage();
		if (lang.isEmpty()) {
			lang = "en";
		}
		
		Uri uri = Uri.parse(DIRECTIONS_URL).buildUpon()
				.appendQueryParameter(KEY_ORIGIN, aFrom)
				.appendQueryParameter(KEY_DESTINATION, aTo)
				.appendQueryParameter(KEY_SENSOR, Boolean.toString(true))
				.appendQueryParameter(KEY_LANGUAGE, lang)
				.appendQueryParameter(KEY_TRAVEL_MODE, aTravelMode)
				.build();

		HttpGet httpget = new HttpGet(uri.toString());
		httpget.setHeader("Content-Type", "application/json");

		String response = doRequest(httpget);
		return response;
	}

	/**
	 * Executes request to the server
	 * 
	 * @param aRequest
	 *            Request object
	 * @return Server response as a string
	 */
	private String doRequest(HttpUriRequest aRequest) {
		String result = "";

		// setting connection timeout
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
		// Create a new HttpClient
		HttpClient httpclient = new DefaultHttpClient(httpParameters);

		try {
			// Execute HTTP Request
			HttpResponse response = httpclient.execute(aRequest);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);
				result = responseString;
			} else {
				result = response.getStatusLine().getReasonPhrase();
			}

		} catch (ClientProtocolException e) {
			String ex = e.toString();
			result = ex;
		} catch (IOException e) {
			String ex = e.toString();
			result = ex;
		}
		return result;
	}

}
