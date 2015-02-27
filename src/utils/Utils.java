package utils;

import gui.InteractiveTableModel;

import java.awt.Color;
import java.awt.Frame;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;

import math.MultivariateNormalDistribution;

import kmlframework.kml.Point;

import app.SpreadApp;

import readers.LocationsReader;
import structure.Coordinates;
import structure.Line;
import structure.TimeLine;

public class Utils {

	// Earths radius in km
	public static final double EARTH_RADIUS = 6371.0;
	// how many millisecond one day holds
	public static final int DAY_IN_MILLIS = 86400000;
	// how many days one year holds
	public static final int DAYS_IN_YEAR = 365;
	
	// ///////////////////
	// ---ENUM FIELDS---//
	// ///////////////////

	public enum PoissonPriorEnum {
		DEFAULT, USER
	}

	public static void errorMessageBox(final String msg) {
		JOptionPane.showMessageDialog(Utils.getActiveFrame(), //
			msg, //
			"Error", //
			JOptionPane.ERROR_MESSAGE, //
			SpreadApp.errorIcon
		);
	}

	// ////////////////////////////////
	// ---EXCEPTION HANDLING UTILS---//
	// ////////////////////////////////

	public static void handleException(final Throwable e, final String msg) {

		final Thread t = Thread.currentThread();
		
		if (SwingUtilities.isEventDispatchThread()) {
			showExceptionDialog(t, e, msg);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					showExceptionDialog(t, e, msg);
				}
			});
		}// END: EDT check
	}// END: uncaughtException

	private static void showExceptionDialog(Thread t, Throwable e, String msg) {
		
		String message = String.format("Unexpected problem on thread %s: %s" + "\n" + msg,
				t.getName(), e.getMessage());

		logException(t, e);

		JOptionPane.showMessageDialog(Utils.getActiveFrame(), //
				message, //
				"Error", //
				JOptionPane.ERROR_MESSAGE, //
				SpreadApp.errorIcon);
	}// END: showExceptionDialog

	private static void logException(Thread t, Throwable e) {
		// TODO: start a thread that logs it, also spying on the user and planting evidence
		// CIA style MOFO!!!
		e.printStackTrace();
	}// END: logException
	
	// ///////////////////////////
	// ---DISCRETE TREE UTILS---//
	// ///////////////////////////

	public static String pickRand(String[] array, Random generator) {

		int rnd = generator.nextInt(array.length);
		return array[rnd];

	}

	public static List<Coordinates> generateCircle(double centerY,
			double centerX, double radius, int numPoints) {

		List<Coordinates> coords = new ArrayList<Coordinates>();

		double Clat = Math.toDegrees((radius / EARTH_RADIUS));
		double Clong = Clat / Math.cos(Math.toRadians(centerX));

		for (int i = 0; i < numPoints; i++) {

			double theta = 2.0 * Math.PI * (i / (double) numPoints);
			double Cx = centerY + (Clong * Math.cos(theta));
			double Cy = centerX + (Clat * Math.sin(theta));

			coords.add(new Coordinates(Cx, Cy, 0.0));

		}// END: numPoints loop

		return coords;
	}// END: GenerateCircle

	public static float matchStateCoordinate(InteractiveTableModel table,
			String state, int latlon) {
		/**
		 * Match state name with its coordinates
		 * 
		 * 1 for lon, 2 for lat
		 */
		float coordinate = Float.NaN;

		for (int i = 0; i < table.getRowCount(); i++) {

			String name = String.valueOf(table.getValueAt(i, 0));

			if (name.toLowerCase().equals(state.toLowerCase())) {
				coordinate = Float.valueOf(String.valueOf(table.getValueAt(i,
						latlon)));
			}
		}

		return coordinate;
	}// END: MatchStateCoordinate

	public static float matchStateCoordinate(LocationsReader data, String state,
			int latlon) {
		/**
		 * Match state name with its coordinates
		 * 
		 * 1 for lon, 0 for lat
		 */
		float coordinate = Float.NaN;

		for (int i = 0; i < data.locations.length; i++) {

			if (data.locations[i].toLowerCase().equals(state.toLowerCase())) {
				coordinate = data.coordinates[i][latlon];

			}
		}

		return coordinate;
	}// END: MatchStateCoordinate

	// ///////////////////////////
	// ---BAYES FACTORS UTILS---//
	// ///////////////////////////

	public static double[] parseDouble(String[] lines) {

		int nrow = lines.length;
		double[] a = new double[nrow];
		for (int i = 0; i < nrow; i++) {
			a[i] = Double.parseDouble(lines[i]);
		}
		return a;
	}

	public static double colMean(double a[][], int col) {
		double sum = 0;
		int nrows = a.length;
		for (int row = 0; row < nrows; row++) {
			sum += a[row][col];
		}
		return sum / nrows;
	}

	public static double[] colMeans(double a[][]) {
		int ncol = a[0].length;
		double[] b = new double[ncol];
		for (int c = 0; c < ncol; c++) {
			b[c] = colMean(a, c);
		}
		return b;
	}

	public static String[] subset(String line[], int start, int length) {
		String output[] = new String[length];
		System.arraycopy(line, start, output, 0, length);
		return output;
	}

	// /////////////////////////////
	// ---CONTINUOUS TREE UTILS---//
	// /////////////////////////////

	public static String getModalityAttributeName(String longitudeName,
			String HPDString) {
		
		String coordinatesName = longitudeName.replaceAll("[0-9.]", "");
		String modalityAttributeName = coordinatesName + "_" + HPDString
				+ "_modality";

		return modalityAttributeName;
	}// END: getModalityAttributeName
	
	public static List<Coordinates> parsePolygons(Object[] longitudeHPD,
			Object[] latitudeHPD) {

		List<Coordinates> coords = new ArrayList<Coordinates>();

		for (int i = 0; i < longitudeHPD.length; i++) {
			coords.add(new Coordinates(Double.valueOf(longitudeHPD[i]
					.toString()), Double.valueOf(latitudeHPD[i].toString()),
					0.0));
		}

		return coords;
	}// END: parsePolygons

	// /////////////////////////
	// ---TIME SLICER UTILS---//
	// /////////////////////////

	public static TimeLine generateTreeTimeLine(RootedTree tree, double timescaler, int numberOfIntervals, ThreadLocalSpreadDate mrsd) {

		// This is a general time span for all of the trees
		double treeRootHeight = Utils.getNodeHeight(tree, tree.getRootNode());
		double startTime = mrsd.getTime()
				- (treeRootHeight * DAY_IN_MILLIS * DAYS_IN_YEAR * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfIntervals);

		return timeLine;
	}// END: generateTreeTimeLine

	public static double[] generateTreeSliceHeights(double treeRootHeight,
			int numberOfIntervals) {

		double[] timeSlices = new double[numberOfIntervals];

		for (int i = 0; i < numberOfIntervals; i++) {

			timeSlices[i] = treeRootHeight
					- (treeRootHeight / (double) numberOfIntervals)
					* ((double) i);
		}

		return timeSlices;
	}// END: generateTimeSlices

	public static TimeLine generateCustomTimeLine(double[] timeSlices, double timescaler, ThreadLocalSpreadDate mrsd) {

		// This is a general time span for all of the trees
		int numberOfSlices = timeSlices.length;
		double firstSlice = timeSlices[0];

		double startTime = mrsd.getTime()
				- (firstSlice * DAY_IN_MILLIS * DAYS_IN_YEAR * timescaler);
		double endTime = mrsd.getTime();

		return new TimeLine(startTime, endTime, numberOfSlices);
	}// END: generateCustomTimeLine
	
	public static double getTreeLength(RootedTree tree, Node node) {

		int childCount = tree.getChildren(node).size();
		if (childCount == 0)
			return tree.getLength(node);

		double length = 0;
		for (int i = 0; i < childCount; i++) {
			length += getTreeLength(tree, tree.getChildren(node).get(i));
		}
		if (node != tree.getRootNode())
			length += tree.getLength(node);
		return length;

	}

	public static double[] imputeValue(double[] location, double[] parentLocation,
			double sliceHeight, double nodeHeight, double parentHeight,
			double rate, boolean trueNoise, double treeNormalization,
			double[] precisionArray) {

		int dim = (int) Math.sqrt(1 + 8 * precisionArray.length) / 2;
		double[][] precision = new double[dim][dim];
		int c = 0;
		for (int i = 0; i < dim; i++) {
			for (int j = i; j < dim; j++) {
				precision[j][i] = precision[i][j] = precisionArray[c++]
						* treeNormalization;
			}
		}

		dim = location.length;
		double[] nodeValue = new double[2];
		double[] parentValue = new double[2];

		for (int i = 0; i < dim; i++) {

			nodeValue[i] = location[i];
			parentValue[i] = parentLocation[i];

		}

		final double scaledTimeChild = (sliceHeight - nodeHeight) * rate;
		final double scaledTimeParent = (parentHeight - sliceHeight) * rate;
		final double scaledWeightTotal = (1.0 / scaledTimeChild)
				+ (1.0 / scaledTimeParent);

		if (scaledTimeChild == 0)
			return location;

		if (scaledTimeParent == 0)
			return parentLocation;

		// Find mean value, weighted average
		double[] mean = new double[dim];
		double[][] scaledPrecision = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			mean[i] = (nodeValue[i] / scaledTimeChild + parentValue[i]
					/ scaledTimeParent)
					/ scaledWeightTotal;

			if (trueNoise) {
				for (int j = i; j < dim; j++)
					scaledPrecision[j][i] = scaledPrecision[i][j] = precision[i][j]
							* scaledWeightTotal;
			}
		}

		if (trueNoise) {
			mean = MultivariateNormalDistribution
					.nextMultivariateNormalPrecision(mean, scaledPrecision);
		}

		double[] result = new double[dim];
		for (int i = 0; i < dim; i++) {
			result[i] = mean[i];
		}

		return result;
	}// END: ImputeValue
	
	// ///////////////////////////
	// ---KML GENERATOR UTILS---//
	// ///////////////////////////

	public static double longNormalise(double lon) {
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

	public static double greatCircDistSpherLawCos(double startLon,
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
				* EARTH_RADIUS;

		return distance;
	}// END: GreatCircDistSpherLawCos

	public static double greatCircDistHavForm(double startLon, double startLat,
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
		double distance = EARTH_RADIUS * c;
		return distance;
	}// END: GreatCircDistHavForm

	public static double greatCircDistVincInvForm(double startLon,
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

	public static double rhumbDistance(double startLon, double startLat,
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
				* EARTH_RADIUS;

		return distance;
	}

	public static double bearing(double rlon1, double rlat1, double rlon2,
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

	// /////////////////
	// ---GUI UTILS---//
	// /////////////////

	public static Frame getActiveFrame() {
		Frame result = null;
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			if (frame.isVisible()) {
				result = frame;
				break;
			}
		}
		return result;
	}

	public static void printProgress(int percent) {

		StringBuilder bar = new StringBuilder("[");

		for (int i = 0; i < 50; i++) {
			if (i < (percent / 2)) {
				bar.append("=");
			} else if (i == (percent / 2)) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}

		bar.append("]   " + percent + "%      ");
		System.out.print("\r" + bar.toString());
	}

	public static void updateProgress(double progressPercentage) {

		final int width = 50; // progress bar width in chars

		System.out.print("\r[");
		int i = 0;
		for (; i <= (int) (progressPercentage * width); i++) {
			System.out.print(".");
		}
		for (; i < width; i++) {
			System.out.print(" ");
		}
		System.out.print("]");
	}

	// ////////////////////
	// ---COMMON UTILS---//
	// ////////////////////

	public double[][] matrixMultiply(double[][] a, double[][] b) {
		int nrowA = a.length; 
		int ncolA = a[0].length; 
		int nrowB = b.length; 
		int ncolB = b[0].length; 

		double c[][] = null;

		if (ncolA == nrowB) {
			
			c = new double[nrowA][ncolB];
			for (int i = 0; i < nrowA; i++) {
				for (int j = 0; j < ncolB; j++) {
					c[i][j] = 0;
					for (int k = 0; k < ncolA; k++) {
						c[i][j] = c[i][j] + a[i][k] * b[k][j];
					}
				}
			}
			
		} else {
			throw new RuntimeException("non-conformable arguments");
		}

		return c;
	}
	
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

	public static String getStringNodeAttribute(Node node, String attributeName) {
		Object attr = node.getAttribute(attributeName);
		if (attr == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		if(!attr.getClass().equals(String.class)) {
			throw new RuntimeException(
				"Attribute, " + attributeName 
				+ ", is not a text attribute for nodes."
			);
		}
		return attr.toString();
	}

	public static String getStringNodeAttribute(Node node,
			String attributeName, String defaultValue) {
		if (node.getAttribute(attributeName) == null) {
			return defaultValue;
		}
		return (String) node.getAttribute(attributeName);
	}

	public static Object getObjectNodeAttribute(Node node, String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		return node.getAttribute(attributeName);
	}

	public static Object[] getObjectArrayNodeAttribute(Node node,
			String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}
		return (Object[]) node.getAttribute(attributeName);
	}

	public static double[] getDoubleArrayNodeAttribute(Node node,
			String attributeName) {
		if (node.getAttribute(attributeName) == null) {
			throw new RuntimeException("Attribute, " + attributeName
					+ ", missing from node");
		}

		Object[] o = (Object[]) node.getAttribute(attributeName);

		double[] array = new double[o.length];
		for (int i = 0; i < o.length; i++) {
			array[i] = Double.valueOf(o[i].toString());
		}
		return array;
	}

	public static Double getNodeHeight(RootedTree tree, Node node) {
		Double nodeHeight = tree.getHeight(node);
		if (nodeHeight == null) {
			throw new RuntimeException(
					"Height attribute missing from the node. \n");
		}

		return nodeHeight;
	}

	public static Object[] getTreeArrayAttribute(RootedTree tree,
			String attribute) {
		Object o = tree.getAttribute(attribute);
		if (o == null) {
			throw new RuntimeException("Attribute " + attribute
					+ " missing from the tree. \n");
		}

		return (Object[]) o;
	}

	public static double[] getTreeDoubleArrayAttribute(RootedTree tree,
			String attribute) {

		Object[] o = (Object[]) tree.getAttribute(attribute);
		if (o == null) {
			throw new RuntimeException("Attribute " + attribute
					+ " missing from the tree. \n");
		}

		double[] array = new double[o.length];
		for (int i = 0; i < o.length; i++) {
			array[i] = Double.valueOf(o[i].toString());
		}

		return array;
	}

	public static int getNodeCount(RootedTree tree) {

		int NodeCount = 0;
		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {
				NodeCount++;
			}
		}

		return NodeCount;
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

	public static double map(double value, double low1, double high1,
			double low2, double high2) {
		/**
		 * maps a single value from its range into another interval
		 * 
		 * @param low1, high1 - range of value; low2, high2 - interval
		 * @return the mapped value
		 */
		// return ((low2 - high2) / (low1 - high1)) * value - ((high1 * low2 - low1 * high2) / (low1 - high1));
		return (value - low1) / (high1 - low1) * (high2 - low2) + low2;
	}// END: map

	public static int newton(int n, int k) {
		BigInteger newton = BigInteger.valueOf(1);
		String newtonString = null;
		for (int i = 1; i <= k; i++) {
			newton = newton.multiply(BigInteger.valueOf(n - i + 1)).divide(
					BigInteger.valueOf(i));
			newtonString = newton.toString();
		}
		return Integer.parseInt(newtonString);
	}

	// /////////////////
	// ---DEBUGGING---//
	// /////////////////

	public static void printCoordinate(Coordinates coordinate) {
		System.out.println("Longitude: " + coordinate.getLongitude());
		System.out.println("Latitude: " + coordinate.getLatitude());
	}
	
	public static void printCoordinatesList(List<Coordinates> list) {
		for (Coordinates coordinate : list) {
			printCoordinate(coordinate);
		}
	}// END: printCoordinatesList
	
	public static void printLine(Line line) {
		
		System.out.println("Start coords:");
		System.out.println("\t Longitude: " + line.getStartLocation().getLongitude());
		System.out.println("\t Latitude: " + line.getStartLocation().getLatitude());
		System.out.println("Start time: " + line.getStartTime());
		System.out.println("End coords:");
		System.out.println("\t Longitude: " + line.getEndLocation().getLongitude());
		System.out.println("\t Latitude: " + line.getEndLocation().getLatitude());
		System.out.println("End time: " + line.getEndTime());
		System.out.println("Max altitude: "+line.getMaxAltitude());
		
	}//END: printLine
	
	public static String getSpreadFormattedTime(double time) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd G",
				Locale.US);

		return formatter.format(time);
	}

	public static void printArray(double[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: printArray

	public static void printArray(String[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: printArray

	public static void printArray(Object[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
	}// END: printArray

	public static void headArray(Object[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: printArray

	public static void print2DArray(Object[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: print2DArray

	public static void print2DArray(double[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: print2DArray

	public static void print2DArray(float[][] array) {
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: print2DArray

	public static void headArray(double[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: headArray

	public static void headArray(String[] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			System.out.println(array[row]);
		}
	}// END: headArray

	public static void head2DArray(float[][] array, int nrow) {
		for (int row = 0; row < nrow; row++) {
			for (int col = 0; col < array[row].length; col++) {
				System.out.print(array[row][col] + " ");
			}
			System.out.print("\n");
		}
	}// END: head2DArray

	public static void save2DArray(String filename, int[][] array) {

		try {

			PrintWriter pri = new PrintWriter(filename);
			for (int row = 0; row < array.length; row++) {
				for (int col = 0; col < array[row].length; col++) {
					pri.print(array[row][col] + "\t");
				}
				pri.print("\n");
			}
			pri.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// END: save2DArray

	public static void save2DArray(String filename, double[][] array) {

		try {

			PrintWriter pri = new PrintWriter(filename);
			for (int row = 0; row < array.length; row++) {
				for (int col = 0; col < array[row].length; col++) {
					pri.print(array[row][col] + "\t");
				}
				pri.print("\n");
			}

			pri.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}// END: save2DArray

	public static void printHashMap(
			ConcurrentMap<Double, List<Coordinates>> slicesMap) {

		Iterator<Double> iterator = slicesMap.keySet().iterator();
		while (iterator.hasNext()) {

			Double sliceTime = (Double) iterator.next();
			List<Coordinates> list = slicesMap.get(sliceTime);

			double[][] array = new double[list.size()][2];// 3
			for (int i = 0; i < list.size(); i++) {

				array[i][0] = list.get(i).getLatitude();// 1
				array[i][1] = list.get(i).getLongitude();// 2

			}

			System.out.println(sliceTime);
			System.out.println(array.length);
//            print2DArray(array);
			
		}// END while has next
	}// END: saveHashMap
	
	public static void saveHashMap(
			ConcurrentMap<Double, List<Coordinates>> slicesMap) {

		Iterator<Double> iterator = slicesMap.keySet().iterator();
		int j = 0;
		while (iterator.hasNext()) {

			Double sliceTime = (Double) iterator.next();

			List<Coordinates> list = slicesMap.get(sliceTime);

			double[][] array = new double[list.size()][2];// 3

			for (int i = 0; i < list.size(); i++) {

				// array[i][0] = sliceTime;
				array[i][0] = list.get(i).getLatitude();// 1
				array[i][1] = list.get(i).getLongitude();// 2

			}
			Utils.save2DArray(
					"/home/filip/Dropbox/SPREAD/out1/true_noise_array_" + j,
					array);
			j++;

		}// END while has next
	}// END: saveHashMap

}// END: class
