/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.network;

import com.khmelenko.lab.simpleroute.R;

/**
 * Contains response status
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class ResponseStatus {

	// status codes
	private static final String STATUS_OK = "OK";
	private static final String STATUS_NOT_FOUND = "NOT_FOUND";
	private static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
	private static final String STATUS_MAX_WAYPOINTS_EXCEEDED = "MAX_WAYPOINTS_EXCEEDED";
	private static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
	private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
	private static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
	private static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";

	/** Status string */
	private final int iStatusId;

	/** Success flag */
	private final boolean iSuccess;

	/**
	 * Constructor
	 * 
	 * @param aStatus
	 *            Status string
	 * @param aSuccess
	 *            True, if response is successful. Otherwise, false.
	 */
	ResponseStatus(String aStatus) {
		iStatusId = determineStatus(aStatus);

		iSuccess = STATUS_OK.equalsIgnoreCase(aStatus);
	}

	/**
	 * Determines response status
	 * 
	 * @param aRawStatus
	 *            Raw response status
	 * @return Detailed response status ID
	 */
	private int determineStatus(String aRawStatus) {
		int status = R.string.status_ok;

		if (STATUS_OK.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_ok;
		} else if (STATUS_NOT_FOUND.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_not_found;
		} else if (STATUS_ZERO_RESULTS.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_zero_results;
		} else if (STATUS_MAX_WAYPOINTS_EXCEEDED.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_waypoints_exceeded;
		} else if (STATUS_INVALID_REQUEST.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_invalid_request;
		} else if (STATUS_OVER_QUERY_LIMIT.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_over_query_limit;
		} else if (STATUS_REQUEST_DENIED.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_request_denied;
		} else if (STATUS_UNKNOWN_ERROR.equalsIgnoreCase(aRawStatus)) {
			status = R.string.status_unknown_error;
		}

		return status;
	}

	public int getStatus() {
		return iStatusId;
	}

	public boolean isSuccess() {
		return iSuccess;
	}
}
