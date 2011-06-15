package utils;

public class GeoIntermediate {

	// Earths radius in km
	static final double EarthRadius = 6371.0;
	private double coords[][];

	private enum navigationEnum {
		RHUMB, ARC
	}

	private navigationEnum navigationSwitcher;

	public GeoIntermediate(double startLon, double startLat, double endLon,
			double endLat, int sliceCount) {

		navigationSwitcher = navigationEnum.RHUMB;

		// Calculate full distance
		double distance = 0;
		switch (navigationSwitcher) {
		case RHUMB:
			distance = Utils.rhumbDistance(startLon, startLat, endLon, endLat);
			break;

		case ARC:
			distance = Utils.greatCircDistSpherLawCos(startLon, startLat,
					endLon, endLat);
			break;
		}

		double distanceSlice = distance / (double) sliceCount;

		// Convert to radians
		double rlon1 = Utils.longNormalise(Math.toRadians(startLon));
		double rlat1 = Math.toRadians(startLat);
		double rlon2 = Utils.longNormalise(Math.toRadians(endLon));
		double rlat2 = Math.toRadians(endLat);

		coords = new double[sliceCount + 1][2];
		coords[0][0] = startLon;
		coords[0][1] = startLat;
		coords[sliceCount][0] = endLon;
		coords[sliceCount][1] = endLat;

		for (int i = 1; i < sliceCount; i++) {

			distance = distanceSlice;
			double rDist = distance / EarthRadius;

			double bearing = 0;
			// Calculate the bearing
			switch (navigationSwitcher) {
			case RHUMB:
				bearing = Utils.rhumbBearing(rlon1, rlat1, rlon2, rlat2);
				break;

			case ARC:
				bearing = Utils.bearing(rlon1, rlat1, rlon2, rlat2);
				break;
			}

			// use the bearing and the start point to find the
			// destination
			double newLonRad = Utils.longNormalise(rlon1
					+ Math.atan2(Math.sin(bearing) * Math.sin(rDist)
							* Math.cos(rlat1), Math.cos(rDist)
							- Math.sin(rlat1) * Math.sin(rlat2)));

			double newLatRad = Math.asin(Math.sin(rlat1) * Math.cos(rDist)
					+ Math.cos(rlat1) * Math.sin(rDist) * Math.cos(bearing));

			// Convert from radians to degrees
			double newLat = Math.toDegrees(newLatRad);
			double newLon = Math.toDegrees(newLonRad);

			coords[i][0] = newLon;
			coords[i][1] = newLat;

			// This updates the input to calculate new bearing
			rlon1 = newLonRad;
			rlat1 = newLatRad;

			switch (navigationSwitcher) {
			case RHUMB:
				distance = Utils.rhumbDistance(newLon, newLat, endLon, endLat);
				break;

			case ARC:
				distance = Utils.greatCircDistSpherLawCos(newLon, newLat,
						endLon, endLat);
				break;
			}

			distanceSlice = distance / (sliceCount - i);

		}// END: sliceCount loop

	}// END: RhumbIntermediate()

	public double[][] getCoords() {

		return coords;
	}

	public void setRhumbNavigation() {
		navigationSwitcher = navigationEnum.RHUMB;
	}

	public void setArcNavigation() {
		navigationSwitcher = navigationEnum.ARC;
	}

}// END: RhumbIntermediate class
