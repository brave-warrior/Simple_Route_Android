/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

import android.location.Location;

/**
 * Contains route bounds information
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class RouteBounds {

	/** Top left coordinates of the bounding box */
	private final Location iNorthEast;

	/** Bottom right coordinates of the bounding box */
	private final Location iSouthWest;

	/**
	 * Constructor
	 * 
	 * @param aNorthEast
	 *            North east point
	 * @param aSouthWest
	 *            South west point
	 * @param aPolyline
	 *            Polyline
	 */
	public RouteBounds(Location aNorthEast, Location aSouthWest) {
		iNorthEast = new Location(aNorthEast);
		iSouthWest = new Location(aSouthWest);
	}

	/**
	 * Gets north east point
	 * 
	 * @return Northeast
	 */
	public Location getNorthEast() {
		return iNorthEast;
	}

	/**
	 * Gets southwest point
	 * 
	 * @return Southwest
	 */
	public Location getSouthWest() {
		return iSouthWest;
	}
}
