package utils;

public class RhumbIntermediate {

	// Earths radius in km
	static final double EarthRadius = 6371.0;
	private double coords[][];

	public RhumbIntermediate(double startLon, double startLat, double endLon,
			double endLat, int sliceCount) {

		// Calculate full distance
		double distance = Utils.RhumbDistance(startLon, startLat, endLon,
				endLat);

		double distanceSlice = distance / (double) sliceCount;

		// Convert to radians
		double rlon1 = Utils.LongNormalise(Math.toRadians(startLon));
		double rlat1 = Math.toRadians(startLat);
		double rlon2 = Utils.LongNormalise(Math.toRadians(endLon));
		double rlat2 = Math.toRadians(endLat);

		coords = new double[sliceCount + 1][2];
		coords[0][0] = startLon;
		coords[0][1] = startLat;
		coords[sliceCount][0] = endLon;
		coords[sliceCount][1] = endLat;

		for (int i = 1; i < sliceCount; i++) {

			distance = distanceSlice;
			double rDist = distance / EarthRadius;

			// Calculate the bearing
			double bearing = Utils.rhumbBearing(rlon1, rlat1, rlon2, rlat2);

			// use the bearing and the start point to find the
			// destination
			double newLonRad = Utils.LongNormalise(rlon1
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

			distance = Utils.RhumbDistance(newLon, newLat, endLon, endLat);
			distanceSlice = distance / (sliceCount - i);

		}// END: sliceCount loop

	}// END: RhumbIntermediate()

	public double[][] getCoords() {

		return coords;
	}

}// END: RhumbIntermediate class
