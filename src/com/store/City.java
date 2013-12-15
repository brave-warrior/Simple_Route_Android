/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

/**
 * Describes the city
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class City {

	/** City ID */
	private final String iId;

	/** City description */
	private final String iDescription;

	/**
	 * Constructor
	 * 
	 * @param aId
	 *            ID
	 * @param aDescription
	 *            Description
	 */
	public City(String aId, String aDescription) {
		iId = aId;
		iDescription = aDescription;
	}

	/**
	 * Gets city ID
	 * 
	 * @return City ID
	 */
	public String getId() {
		return iId;
	}

	/**
	 * Gets city description
	 * 
	 * @return City description
	 */
	public String getDescription() {
		return iDescription;
	}

}
