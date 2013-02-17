package kmlframework.utils;

public class Ellipsoid {
	
	private static double a = 6378137; // default to wgs 84
	private static double f = 298.257223563; // default to wgs 84
	private static double fInverted = 1 / f;
	private static double eps2 = fInverted * (2d - fInverted);
	private static double k1 = Math.toRadians(a * (1d - eps2));
	private static double k2 = Math.toRadians(a);
	
	/**
	 * Convert meter to longitude at ref latitude
	 */
	public static final double meterToLongitude(double latitude) {
		return 1.0 / longitudeToMeter(latitude);
	}
	
	/**
	 * Convert meter to latitude at ref latitude
	 */
	public static final double meterToLatitude(double latitude) {
		return 1.0 / latitudeToMeter(latitude);
	}
	
	/**
	 * Convert longitude to meter at ref latitude
	 */
	public static final double longitudeToMeter(double latitude) {
		return (Math.cos(Math.toRadians(latitude)) * k2) / Math.sqrt(getDiv0(latitude));
	}
	
	/**
	 * Convert latitude to meter at ref latitude
	 */
	public static final double latitudeToMeter(double latitude) {
		return (k1 / Math.sqrt(Math.pow(getDiv0(latitude), 3)));
	}
	
	private static final double getDiv0(double latitude) {
		return 1.0 - eps2 * Math.pow(Math.sin(Math.toRadians(latitude)), 2);
	}
}