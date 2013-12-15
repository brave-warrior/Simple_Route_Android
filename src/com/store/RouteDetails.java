/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;


/**
 * Contains route details information
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class RouteDetails {

	private String iCopyrights = "";
	private String iSummary = "";
	private String iWarnings = "";
	
	/**
	 * Default constructor
	 */
	public RouteDetails() {
	}

	/**
	 * Sets copyrights
	 * 
	 * @param aCopyrights
	 *            Copyrights
	 */
	public void setCopyrights(String aCopyrights) {
		iCopyrights = aCopyrights;
	}

	/**
	 * Sets route summary
	 * 
	 * @param aSummary
	 *            Summary
	 */
	public void setSummary(String aSummary) {
		iSummary = aSummary;
	}

	/**
	 * Sets route warnings
	 * 
	 * @param aWarnings
	 *            Warnings
	 */
	public void setWarnings(String aWarnings) {
		iWarnings = aWarnings;
	}

	/**
	 * Gets copyrights
	 * 
	 * @return Copyrights
	 */
	public String getCopyrights() {
		return iCopyrights;
	}

	/**
	 * Gets route summary
	 * 
	 * @return Summary
	 */
	public String getSummary() {
		return iSummary;
	}

	/**
	 * Gets route warnings
	 * 
	 * @return Warnings
	 */
	public String getWarnings() {
		return iWarnings;
	}
}
