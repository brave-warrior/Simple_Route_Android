/**
 * Copyright Khmelenko Lab
 * Author: Dmytro Khmelenko
 */
package com.store;

import android.location.Location;

/**
 * Contains the steps for the route
 * 
 * @author Dmytro Khmelenko
 * 
 */
public class RouteStep {

	private final Location iStartLocation;
	private final Location iEndLocation;

	private final int iDistance;
	private final int iDuration;

	private final String iTravelMode;
	private final String iInstructions;

	private final String iPoints;

	/**
	 * Constructor
	 * 
	 * @param aStart
	 * @param aEnd
	 * @param aDistance
	 * @param aDuration
	 * @param aTravelMode
	 * @param aInstruction
	 * @param aPoints
	 */
	private RouteStep(Location aStart, Location aEnd, int aDistance,
			int aDuration, String aTravelMode, String aInstruction,
			String aPoints) {
		iDistance = aDistance;
		iDuration = aDuration;

		iStartLocation = new Location(aStart);
		iEndLocation = new Location(aEnd);

		iTravelMode = aTravelMode;
		iInstructions = aInstruction;

		iPoints = aPoints;
	}

	public Location getStartLocation() {
		return iStartLocation;
	}

	public Location getEndLocation() {
		return iEndLocation;
	}

	public int getDistance() {
		return iDistance;
	}

	public int getDuration() {
		return iDuration;
	}

	public String getTravelMode() {
		return iTravelMode;
	}

	public String getInstructions() {
		return iInstructions;
	}

	public String getPoints() {
		return iPoints;
	}

	/**
	 * Helper builder class
	 * 
	 * @author Dmytro Khmelenko
	 * 
	 */
	public static class Builder {
		private Location iStartLocation;
		private Location iEndLocation;

		private int iDistance;
		private int iDuration;

		private String iTravelMode;
		private String iInstructions;

		private String iPoints;

		public Builder(Location aStart, Location aEnd) {
			iStartLocation = aStart;
			iEndLocation = aEnd;
		}

		public Builder distance(int aDistance) {
			iDistance = aDistance;
			return this;
		}

		public Builder duration(int aDuration) {
			iDuration = aDuration;
			return this;
		}

		public Builder travelMode(String aTravelMode) {
			iTravelMode = aTravelMode;
			return this;
		}

		public Builder instructions(String aInstructions) {
			iInstructions = aInstructions;
			return this;
		}

		public Builder points(String aPoints) {
			iPoints = aPoints;
			return this;
		}

		/**
		 * Builds the {@link RouteStep} instance
		 * 
		 * @return {@link RouteStep} instance
		 */
		public RouteStep build() {
			return new RouteStep(iStartLocation, iEndLocation, iDistance,
					iDuration, iTravelMode, iInstructions, iPoints);
		}
	}
}
