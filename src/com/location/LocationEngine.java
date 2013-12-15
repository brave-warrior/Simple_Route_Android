/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Provides methods for retrieving location
 * 
 * @author Dmytro Khmelenko
 * 
 */
public final class LocationEngine implements LocationListener {

	/** Observer for retrieved location */
	private LocationObserver iObserver;

	private LocationManager iLocationManager;

	/**
	 * Constructor
	 * 
	 * @param aContext
	 *            App context
	 * @param aObserver
	 *            Observer
	 */
	public LocationEngine(Context aContext, LocationObserver aObserver) {
		// check retrieved parameters
		if (aContext == null || aObserver == null) {
			throw new NullPointerException(
					"Neither Context nor LocationObserver can't be null");
		}

		iObserver = aObserver;

		iLocationManager = (LocationManager) aContext
				.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * Starts retrieving location
	 */
	public void retrieveLocation() {

		// retrieve location for both providers
		iLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);

		iLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);
	}

	/**
	 * Stops retrieving location
	 */
	public void stopRetrieving() {
		iLocationManager.removeUpdates(this);
	}

	/**
	 * Gets the last retrieved location
	 * 
	 * @return Last retrieved location or null
	 */
	public Location getLastLocation() {
		Location lastLocation = iLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastLocation == null) {
			lastLocation = iLocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

		return lastLocation;
	}

	/*
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	@Override
	public void onLocationChanged(Location aLocation) {
		iObserver.locationUpdated(aLocation);
	}

	/*
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String aProvider) {
		// do nothing
	}

	/*
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String aProvider) {
		// do nothing
	}

	/*
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String aProvider, int aStatus, Bundle aExtras) {
		// do nothing, provider status is changed
	}
}
