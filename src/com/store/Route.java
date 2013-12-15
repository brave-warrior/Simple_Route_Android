/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

import java.util.List;

import android.location.Location;

/**
 * Contains route data
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class Route {

	/** Distance in meters */
	public int iDistance;

	/** Duration is seconds */
	public int iDuration;

	/** Readable end address */
	public String iEndAddress;

	/** Points of the end location */
	public Location iEndLocation;

	/** Readable start address */
	public String iStartAddress;

	/** Points of the start location */
	public Location iStartLocation;

	/** Route bounds */
	public RouteBounds iBounds;
	
	/** Encoded polyline */
	public String iEncodedPolyline;

	/** Route details */
	public RouteDetails iDetails;

	/** List of the route steps */
	public List<RouteStep> iSteps;

	/**
	 * Default constructor
	 */
	public Route() {
	}

}
