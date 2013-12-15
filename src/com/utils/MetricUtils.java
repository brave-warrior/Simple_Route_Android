/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.utils;

import com.khmelenko.lab.simpleroute.R;

import android.content.Context;

/**
 * Contains methods for work with a metric system
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class MetricUtils {

	public static final int METER = 1;
	public static final int KILOMETER = 1000 * METER;

	/**
	 * Denied constructor
	 */
	private MetricUtils() {
	}

	/**
	 * Converts distance in meters to readable
	 * 
	 * @param aContext
	 *            Context
	 * @param aMeters
	 *            Distance in meters
	 * @return Readable distance
	 */
	public static String toReadableDist(Context aContext, int aMeters) {
		StringBuilder builder = new StringBuilder();

		// show in kilometers or in meters
		if (aMeters > KILOMETER) {
			double kilometer = (double) aMeters / KILOMETER;
			String format = aContext.getResources().getString(
					R.string.utils_kilometers, kilometer);
			builder.append(format);
		} else {

			int meters = aMeters;
			String format = aContext.getResources().getString(
					R.string.utils_meters, meters);
			builder.append(format);
		}

		return builder.toString();
	}

}
