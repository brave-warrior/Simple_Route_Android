/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.utils;

import com.khmelenko.lab.simpleroute.R;

import android.content.Context;

/**
 * Provides utilities related to the date/time
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class DateTimeUtils {

	public static final int SECOND = 1;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;

	/**
	 * Denied constructor
	 */
	private DateTimeUtils() {

	}

	/**
	 * Converts time in seconds to the readable time
	 * 
	 * @param aContext
	 *            Context
	 * @param aSeconds
	 *            Time in seconds
	 * @param aShowSeconds
	 *            True, if the seconds should be shown
	 * @return Readable time
	 */
	public static String toReadableTime(Context aContext, int aSeconds,
			boolean aShowSeconds) {
		StringBuilder builder = new StringBuilder();

		// process days
		if (aSeconds > DAY) {
			int days = aSeconds / DAY;
			String format = aContext.getResources().getString(
					R.string.utils_days, days);
			builder.append(format);
			builder.append(", ");

			aSeconds -= DAY * days;
		}

		// process hours
		if (aSeconds > HOUR) {
			int hours = aSeconds / HOUR;
			String format = aContext.getResources().getString(
					R.string.utils_hours, hours);
			builder.append(format);
			builder.append(", ");

			aSeconds -= HOUR * hours;
		}

		// process minutes
		int minutes = aSeconds / MINUTE;
		aSeconds -= MINUTE * minutes;

		// round to minutes, if seconds should not be shown
		if (aSeconds > 0 && !aShowSeconds) {
			minutes++;
		}

		String format = aContext.getResources().getString(
				R.string.utils_minutes, minutes);
		builder.append(format);

		// show seconds only if required
		if (aShowSeconds && aSeconds > 0) {
			int seconds = aSeconds;
			builder.append(", ");
			String secondsFormat = aContext.getResources().getString(
					R.string.utils_seconds, seconds);
			builder.append(secondsFormat);
		}

		return builder.toString();
	}

}
