package utils;

import java.awt.Color;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;

import org.boehn.kmlframework.kml.Point;

import structure.Coordinates;

public class Utils {

	// Earths radius in km
	static final double EarthRadius = 6371.0;

	public static String getKMLDate(double fractionalDate) {

		int year = (int) fractionalDate;
		String yearString;

		if (year < 10) {
			yearString = "000" + year;
		} else if (year < 100) {
			yearString = "00" + year;
		} else if (year < 1000) {
			yearString = "0" + year;
		} else {
			yearString = "" + year;
		}

		double fractionalMonth = fractionalDate - year;

		int month = (int) (12.0 * fractionalMonth);
		String monthString;

		if (month < 10) {
			monthString = "0" + month;
		} else {
			monthString = "" + month;
		}

		int day = (int) Math.round(30 * (12 * fractionalMonth - month));
		String dayString;

		if (day < 10) {
			dayString = "0" + day;
		} else {
			dayString = "" + day;
		}

		return yearString + "-" + monthString + "-" + dayString;
	}

	public static int getIntegerNodeAttribute(Node node, String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		return (Integer) node.getAttribute(attributeName);
	}

	public static int getIntegerNodeAttribute(Node node, String attributeName,
			int defaultValue) {
		if (node.getAttribute(attributeName) == null) {
			return defaultValue;
		}
		return (Integer) node.getAttribute(attributeName);
	}

	public static double getDoubleNodeAttribute(Node node, String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		return (Double) node.getAttribute(attributeName);
	}

	public static double getDoubleNodeAttribute(Node node,
			String attributeName, double defaultValue) {
		if (node.getAttribute(attributeName) == null) {
			return defaultValue;
		}
		return (Double) node.getAttribute(attributeName);
	}

	public static Object[] getArrayNodeAttribute(Node node, String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		return (Object[]) node.getAttribute(attributeName);
	}

	public static int getExternalNodeCount(RootedTree tree) {

		int externalNodeCount = 0;
		for (Node node : tree.getNodes()) {
			if (tree.isExternal(node)) {

				externalNodeCount++;
			}
		}

		return externalNodeCount;
	}

	public static double getTreeHeightMin(RootedTree tree) {
		/**
		 * Finds the min height for given tree.
		 * 
		 * @param tree
		 * @return min height
		 */
		double m = Double.MAX_VALUE;
		for (Node node : tree.getNodes()) {
			if (tree.getHeight(node) < m) {
				m = tree.getHeight(node);
			}
		}
		return m;
	}// END: getTreeHeightMin

	public static double getTreeHeightMax(RootedTree tree) {
		/**
		 * Finds the max height for given tree.
		 * 
		 * @param tree
		 * @return max height
		 */
		double m = -Double.MAX_VALUE;
		for (Node node : tree.getNodes()) {
			if (tree.getHeight(node) > m) {
				m = tree.getHeight(node);
			}
		}
		return m;
	}// END: getTreeHeightMax

	public static double getListMin(List<Double> list) {
		double m = Double.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) < m) {
				m = list.get(i);
			}
		}
		return m;
	}// END: getDoubleListMax

	public static double getListMax(List<Double> list) {
		double m = -Double.MAX_VALUE;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > m) {
				m = list.get(i);
			}
		}
		return m;
	}// END: getDoubleListMax

	public static double get2DArrayMax(double[][] array) {
		double m = -Double.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (array[i][j] > m) {
					m = array[i][j];
				}
			}
		}
		return m;
	}// END: get2DArrayMax

	public static String getKMLColor(Color color) {
		/**
		 * converts a Java color into a 4 channel hex color string.
		 * 
		 * @param color
		 * @return the color string
		 */
		String a = Integer.toHexString(color.getAlpha());
		String b = Integer.toHexString(color.getBlue());
		String g = Integer.toHexString(color.getGreen());
		String r = Integer.toHexString(color.getRed());
		return (a.length() < 2 ? "0" : "") + a + (b.length() < 2 ? "0" : "")
				+ b + (g.length() < 2 ? "0" : "") + g
				+ (r.length() < 2 ? "0" : "") + r;
	}

	public static String getKMLColor(Color color, double opacity) {
		/**
		 * converts a Java color into a 4 channel hex color string.
		 * 
		 * @param color
		 * @return the color string
		 */
		int alpha = (int) (256 * (1.0 - opacity));
		String a = Integer.toHexString(alpha);
		String b = Integer.toHexString(color.getBlue());
		String g = Integer.toHexString(color.getGreen());
		String r = Integer.toHexString(color.getRed());
		return (a.length() < 2 ? "0" : "") + a + (b.length() < 2 ? "0" : "")
				+ b + (g.length() < 2 ? "0" : "") + g
				+ (r.length() < 2 ? "0" : "") + r;
	}

	public static Color getBlendedColor(float proportion, Color startColor,
			Color endColor) {
		proportion = Math.max(proportion, 0.0F);
		proportion = Math.min(proportion, 1.0F);
		float[] start = startColor.getRGBColorComponents(null);
		float[] end = endColor.getRGBColorComponents(null);

		float[] color = new float[start.length];
		for (int i = 0; i < start.length; i++) {
			color[i] = start[i] + ((end[i] - start[i]) * proportion);
		}

		return new Color(color[0], color[1], color[2]);
	}

	public static Color getRandomColor() {
		/**
		 * random color selection
		 * 
		 * @return the Color
		 */
		int red = 127 + (int) (Math.random() * 127);
		int green = 127 + (int) (Math.random() * 127);
		int blue = 127 + (int) (Math.random() * 127);
		int alpha = 127 + (int) (Math.random() * 127);
		Color col = new Color(red, green, blue, alpha);

		return col;
	}// END: getRandomColor

	public static double map(double x, double x1, double x2, double y1,
			double y2) {
		/**
		 * maps a single value from its range into another interval
		 * 
		 * @param x1
		 *            , x2 - range of x; y1, y2 - interval
		 * 
		 * @return the mapped value
		 */
		double y = ((y1 - y2) / (x1 - x2)) * x
				- ((x2 * y1 - x1 * y2) / (x1 - x2));

		return y;
	}// END: map

	public static List<Point> convertToPoint(List<Coordinates> coords) {

		List<Point> points = new ArrayList<Point>();

		Iterator<Coordinates> iterator = coords.iterator();

		while (iterator.hasNext()) {

			Point point = new Point();
			Coordinates coord = iterator.next();
			point.setLongitude(coord.getLongitude());
			point.setLatitude(coord.getLatitude());
			point.setAltitude(0.0);

			points.add(point);
		}

		return points;
	}// END: convertToPoint

	public static List<Coordinates> GenerateCircle(double centerLong,
			double centerLat, double radius, int numPoints) {

		List<Coordinates> coords = new ArrayList<Coordinates>();

		double Clat = Math.toDegrees((radius / EarthRadius));
		double Clong = Clat / Math.cos(Math.toRadians(centerLat));

		for (int i = 0; i < numPoints; i++) {

			double theta = 2.0 * Math.PI * (i / (double) numPoints);
			double Cx = centerLong + (Clong * Math.cos(theta));
			double Cy = centerLat + (Clat * Math.sin(theta));

			coords.add(new Coordinates(Cx, Cy, 0.0));

		}// END: numPoints loop

		return coords;
	}// END: GenerateCircle

	public static double[] parseDouble(String[] lines) {

		int nrow = lines.length;
		double[] a = new double[nrow];
		for (int i = 0; i < nrow; i++) {
			a[i] = Double.parseDouble(lines[i]);
		}
		return a;
	}

	public static List<Coordinates> ParsePolygons(Object[] longitudeHPD,
			Object[] latitudeHPD) {

		List<Coordinates> coords = new ArrayList<Coordinates>();

		for (int i = 0; i < longitudeHPD.length; i++) {
			coords.add(new Coordinates(Double.valueOf(longitudeHPD[i]
					.toString()), Double.valueOf(latitudeHPD[i].toString()),
					0.0));
		}

		return coords;
	}// END: GenerateCircle

	public static void printHashMap(ConcurrentMap<Double,List<Coordinates>> slicesMap,
			boolean printValues) {

		Set<Double> hostKeys = slicesMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();

		int valCount = 1;
		while (iterator.hasNext()) {

			Double mapKey = (Double) iterator.next();

			List<Coordinates> list = slicesMap.get(mapKey);

			if (printValues) {

				System.out.println("===================================");
				System.out.println(mapKey);
				System.out.println("===================================");

				for (int i = 0; i < list.size(); i++) {

					Coordinates coord = list.get(i);
					
					System.out.println(coord.getLongitude() + " "
							+ coord.getLatitude());
					
					if (coord == null) {
						System.out.println("null!");
					}
				}

			} else {

				System.out.println(mapKey);
				System.out.println("is null = " + (list == null));
				System.out.println("===================================");
				valCount++;
			}
		}
	}// END: PrintHashMap

	public static void PrintArray(double[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: PrintArray

	public static void PrintArray(String[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: PrintArray

	public static void PrintArray(Object[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: PrintArray

	public static void HeadArray(Object[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: HeadArray

	public static void HeadArray(double[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: HeadArray

	public static void HeadArray(String[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: Head2DArray

	public static void Print2DArray(Object[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: Print2DArray

	public static void Print2DArray(double[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: Print2DArray

	public static void Print2DArray(float[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: Print2DArray

	public static void Head2DArray(float[][] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: Head2DArray

	public static void PrintTwoArrays(Object[] left, Object[] right) {
		/**
		 * Prints elements of two arrays until smaller one runs out of any
		 * elements
		 * */
		int n = left.length < right.length ? left.length : right.length;

		for (int row = 0; row < n; row++) {

			System.out.println(left[row] + " " + right[row]);

		}
	}

	public static void PrintReadLocations(ReadLocations locations) {
		for (int row = 0; row < locations.nrow; row++) {
			System.out.println(locations.locations[row] + " "
					+ locations.coordinates[row][0] + " "
					+ locations.coordinates[row][1]);
		}
	}

	public static void Save2DArray(String filename, double[][] array) {

		try {

			PrintWriter pri = new PrintWriter(filename);
			for (int row = 0; row < array.length; row++) {
				for (int col = 0; col < array[row].length; col++) {
					pri.print(array[row][col] + " ");
				}
				pri.print("\n");
			}

			pri.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}// END: save2DArray

	public static double GreatCircDistSpherLawCos(double startLon,
			double startLat, double endLon, double endLat) {
		/**
		 * Calculates the geodesic distance between two points specified by
		 * latitude/longitude using the Spherical Law of Cosines (slc)
		 * 
		 * @param start
		 *            point Lon, Lat; end point Lon, Lat;
		 * 
		 * @return distance in km
		 * */
		double rlon1 = Math.toRadians(startLon);
		double rlat1 = Math.toRadians(startLat);
		double rlon2 = Math.toRadians(endLon);
		double rlat2 = Math.toRadians(endLat);

		double distance = Math.acos(Math.sin(rlat1) * Math.sin(rlat2)
				+ Math.cos(rlat1) * Math.cos(rlat2) * Math.cos(rlon2 - rlon1))
				* EarthRadius;

		return distance;
	}// END: GreatCircDistSpherLawCos

	public static double GreatCircDistHavForm(double startLon, double startLat,
			double endLon, double endLat) {
		/**
		 * Calculates the geodesic distance between two points specified by
		 * latitude/longitude using the Haversine formula
		 * 
		 * @param start
		 *            point Lon, Lat; end point Lon, Lat;
		 * 
		 * @return distance in km
		 * */
		double dLat = Math.toRadians(endLat - startLat);
		double dLon = Math.toRadians(endLon - startLon);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(startLat))
				* Math.cos(Math.toRadians(endLat)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = EarthRadius * c;
		return distance;
	}// END: GreatCircDistHavForm

	public static double GreatCircDistVincInvForm(double startLon,
			double startLat, double endLon, double endLat) {
		/**
		 * Calculates the geodesic distance between two points specified by
		 * latitude/longitude using the Vincenty inverse formula for ellipsoids
		 * 
		 * @param start
		 *            point Lon, Lat; end point Lon, Lat;
		 * 
		 * @return distance in km
		 * */
		double rlon1 = Math.toRadians(startLon);
		double rlat1 = Math.toRadians(startLat);
		double rlon2 = Math.toRadians(endLon);
		double rlat2 = Math.toRadians(endLat);

		double a = 6378137.0; // length of major axis of the ellipsoid (radius
		// at equator)
		double b = 6356752.314245; // length of minor axis of the ellipsoid
		// (radius at the poles)
		double f = 1 / 298.257223563; // flattening of the ellipsoid

		double L = (rlon2 - rlon1); // difference in longitude
		double U1 = Math.atan((1 - f) * Math.tan(rlat1)); // reduced latitude
		double U2 = Math.atan((1 - f) * Math.tan(rlat2)); // reduced latitude
		double sinU1 = Math.sin(U1);
		double cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2);
		double cosU2 = Math.cos(U2);

		double cosSqAlpha = Double.NaN;
		double sinSigma = Double.NaN;
		double cosSigma = Double.NaN;
		double cos2SigmaM = Double.NaN;
		double sigma = Double.NaN;

		double lambda = L;
		double lambdaP = 0.0;

		int iterLimit = 100;

		while (Math.abs(lambda - lambdaP) > 1e-12 & iterLimit > 0) {

			double sinLambda = Math.sin(lambda);
			double cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
					* (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

			if (sinSigma == 0) {
				return 0.0; // Co-incident points
			}

			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

			if (Double.isNaN(cos2SigmaM)) {
				cos2SigmaM = 0.0; // Equatorial line: cosSqAlpha=0
			}

			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;

			lambda = L
					+ (1 - C)
					* f
					* sinAlpha
					* (sigma + C
							* sinSigma
							* (cos2SigmaM + C * cosSigma
									* (-1 + 2 * cos2SigmaM * cos2SigmaM)));

			iterLimit--;

		}// END: convergence loop

		if (iterLimit == 0) {
			return Double.NaN; // formula failed to converge
		}

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384.0
				* (4096.0 + uSq * (-768.0 + uSq * (320.0 - 175.0 * uSq)));
		double B = uSq / 1024.0
				* (256.0 + uSq * (-128.0 + uSq * (74.0 - 47.0 * uSq)));

		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * Math.pow(cos2SigmaM, 2)) - B
								/ 6 * cos2SigmaM
								* (-3 + 4 * Math.pow(sinSigma, 2))
								* (-3 + 4 * Math.pow(cos2SigmaM, 2))));

		double distance = b * A * (sigma - deltaSigma) / 1000.0;

		return distance; // Distance in km

	}// END: GreatCircDistVincInvForm

	public static double RhumbDistance(double startLon, double startLat,
			double endLon, double endLat) {
		/**
		 * Returns the distance from start point to the end point in km,
		 * travelling along a rhumb line
		 * 
		 * @param start
		 *            point Lon, Lat; end point Lon, Lat;
		 * 
		 * @return distance in km
		 */
		double rlon1 = Math.toRadians(startLon);
		double rlat1 = Math.toRadians(startLat);
		double rlon2 = Math.toRadians(endLon);
		double rlat2 = Math.toRadians(endLat);

		double dLat = (rlat2 - rlat1);
		double dLon = Math.abs(rlon2 - rlon1);

		double dPhi = Math.log(Math.tan(rlat2 / 2 + Math.PI / 4)
				/ Math.tan(rlat1 / 2 + Math.PI / 4));
		double q = (!Double.isNaN(dLat / dPhi)) ? dLat / dPhi : Math.cos(rlat1); // E-W
		// line
		// gives
		// dPhi=0
		// if dLon over 180° take shorter rhumb across 180° meridian:
		if (dLon > Math.PI)
			dLon = 2 * Math.PI - dLon;
		double distance = Math.sqrt(dLat * dLat + q * q * dLon * dLon)
				* EarthRadius;

		return distance;
	}

	public static double Bearing(double rlon1, double rlat1, double rlon2,
			double rlat2) {
		/**
		 * Returns the (initial) bearing from start point to the destination
		 * point, in degrees
		 * 
		 * @param rlat1
		 *            , rlon1 : longitude/latitude in radians of start point
		 *            rlon2, rlat2 : longitude/latitude of end point
		 * 
		 * @returns Initial bearing in degrees from North
		 */

		double brng = Double.NaN;

		if ((Math.cos(rlat1) < 1 / Double.MAX_VALUE)) {

			if (rlat2 > 0) { // if starting from North Pole

				brng = Math.PI;

			} else { // if starting from South Pole

				brng = 2 * Math.PI;

			}

		} else { // if starting from points other than Poles

			double dLon = (rlon2 - rlon1);

			double y = Math.sin(dLon) * Math.cos(rlat2);
			double x = Math.cos(rlat1) * Math.sin(rlat2) - Math.sin(rlat1)
					* Math.cos(rlat2) * Math.cos(dLon);

			// double brng = Math.abs(Math.atan2(y, x));
			brng = Math.atan2(y, x);
		}
		return Math.toRadians((Math.toDegrees(brng) + 360) % 360);
	}

	public static double rhumbBearing(double rlon1, double rlat1, double rlon2,
			double rlat2) {
		/**
		 * Returns the bearing from start point to the supplied point along a
		 * rhumb line
		 * 
		 * @param rlat1
		 *            , rlon1 : longitude/latitude in radians of start point
		 *            rlon2, rlat2 : longitude/latitude of end point
		 * 
		 * @returns Initial bearing in degrees from North
		 */
		double dLon = (rlon2 - rlon1);

		double dPhi = Math.log(Math.tan(rlat2 / 2 + Math.PI / 4)
				/ Math.tan(rlat1 / 2 + Math.PI / 4));
		if (Math.abs(dLon) > Math.PI)
			dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);

		double brng = Math.atan2(dLon, dPhi);

		return Math.toRadians((Math.toDegrees(brng) + 360) % 360);
	}

	public static double LongNormalise(double lon) {
		// normalise to -180...+180
		return (lon + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
	}

	public static float getMercatorLatitude(double lat) {

		double R_MAJOR = 6378137.0;
		double R_MINOR = 6356752.3142;

		if (lat > 89.5) {
			lat = 89.5;
		}
		if (lat < -89.5) {
			lat = -89.5;
		}
		double temp = R_MINOR / R_MAJOR;
		double es = 1.0 - (temp * temp);
		double eccent = Math.sqrt(es);
		double phi = Math.toRadians(lat);
		double sinphi = Math.sin(phi);
		double con = eccent * sinphi;
		double com = 0.5 * eccent;
		con = Math.pow(((1.0 - con) / (1.0 + con)), com);
		double ts = Math.tan(0.5 * ((Math.PI * 0.5) - phi)) / con;
		double y = 0 - R_MAJOR * Math.log(ts);

		return (float) y;
	}

	public static float MatchStateCoordinate(ReadLocations data, String state,
			int latlon) {
		/**
		 * Match state name with its coordinates
		 * 
		 * 0 for lon, 1 for lat
		 */
		float coordinate = Float.NaN;

		for (int i = 0; i < data.locations.length; i++) {
			if (data.locations[i].toLowerCase().equals(state.toLowerCase())) {
				coordinate = data.coordinates[i][latlon];
			}
		}

		return coordinate;
	}// END: MatchStateCoordinate

	public static String[] Subset(String line[], int start, int length) {
		String output[] = new String[length];
		System.arraycopy(line, start, output, 0, length);
		return output;
	}

	public static int Newton(int n, int k) {
		BigInteger newton = BigInteger.valueOf(1);
		String newtonString = null;
		for (int i = 1; i <= k; i++) {
			newton = newton.multiply(BigInteger.valueOf(n - i + 1)).divide(
					BigInteger.valueOf(i));
			newtonString = newton.toString();
		}
		return Integer.parseInt(newtonString);
	}

	public static double ColMean(double a[][], int col) {
		double sum = 0;
		int nrows = a.length;
		for (int row = 0; row < nrows; row++) {
			sum += a[row][col];
		}
		return sum / nrows;
	}

	public static double[] ColMeans(double a[][]) {
		int ncol = a[0].length;
		double[] b = new double[ncol];
		for (int c = 0; c < ncol; c++) {
			b[c] = ColMean(a, c);
		}
		return b;
	}

}// END: Utils class
