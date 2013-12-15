/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.location;

import android.location.Location;

/**
 * Observer for monitoring updated location
 * 
 * @author Dmytro Khmelenko
 * 
 */
public interface LocationObserver {

	/**
	 * Called when the location is updated
	 * 
	 * @param aLocation
	 *            New updated location. Can be null
	 */
	public void locationUpdated(Location aLocation);

}
