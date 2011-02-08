/**
 * RacRABV 
 * */
//   "/home/filip/Dropbox/Java-ML/JavaProjects/Spread/data/RacRABV/RacRABV_cont_MCC.tre"

/**
 * WNX
 * */
//   "/home/filip/Dropbox/Java-ML/JavaProjects/Spread/data/WNX/WNV_relaxed_geo_gamma_MCC.tre"

package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;

public class ContinuousTreeToKML {

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// Earths radius in km
	private static final double EarthRadius = 6371;

	private static RootedTree tree;
	private static String coordinatesName;
	private static String HPD;
	private static String mrsdString;
	private static int numberOfIntervals;
	private static double timescaler;
	private static double rootHeight;
	private static List<Layer> layers;
	private static double maxAltMapping;
	private static String longitudeName;
	private static String latitudeName;
	private static double treeHeightMax;

	private enum branchesMappingEnum {
		TIME, RATE, DISTANCE, DEFAULT
	}

	private enum polygonsMappingEnum {
		TIME, RATE, DEFAULT
	}

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private static timescalerEnum timescalerSwitcher;
	private static branchesMappingEnum branchesColorMapping;
	private static branchesMappingEnum branchesOpacityMapping;
	private static branchesMappingEnum branchesWidthMapping;
	private static branchesMappingEnum branchesAltitudeMapping;
	private static polygonsMappingEnum polygonsColorMapping;
	private static polygonsMappingEnum polygonsOpacityMapping;

	public static void main(String args[]) throws Exception {

		// start timing
		long time = -System.currentTimeMillis();

		// this will be parsed from gui:
		TreeImporter importer = new NexusImporter(
				new FileReader(
						"/home/filip/Dropbox/Java-ML/JavaProjects/Spread/data/RacRABV/RacRABV_cont_MCC.tre"

				));

		coordinatesName = "location";
		HPD = "95%";
		mrsdString = "2002-11-01 AD";
		numberOfIntervals = 100;
		maxAltMapping = 5000000;
		PrintWriter writer = new PrintWriter("/home/filip/Pulpit/output.kml");

		// parse combobox choices here
		timescalerSwitcher = timescalerEnum.YEARS;
		branchesColorMapping = branchesMappingEnum.TIME;
		branchesOpacityMapping = branchesMappingEnum.RATE;
		branchesWidthMapping = branchesMappingEnum.RATE;
		branchesAltitudeMapping = branchesMappingEnum.DISTANCE;
		polygonsColorMapping = polygonsMappingEnum.RATE;
		polygonsOpacityMapping = polygonsMappingEnum.RATE;

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// this is to choose the proper time scale (if the tree nodes are not
		// scaled in years)
		timescaler = Double.NaN;
		switch (timescalerSwitcher) {
		case DAYS:
			timescaler = 1;
			break;
		case MONTHS:
			timescaler = 30;
		case YEARS:
			timescaler = 365;
			break;
		}

		tree = (RootedTree) importer.importNextTree();
		// this is for time calculations
		rootHeight = tree.getHeight(tree.getRootNode());

		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);

		// this is for mappings
		treeHeightMax = Utils.getTreeHeightMax(tree);

		// This is a general time span for the tree
		SpreadDate mrsd = new SpreadDate(mrsdString);
		TimeLine timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * timescaler), mrsd.getTime(),
				numberOfIntervals);

		// Execute threads
		final int NTHREDS = 10;
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		executor.submit(new Branches());
		executor.submit(new Polygons());
		executor.shutdown();
		// Wait until all threads are finished
		while (!executor.isTerminated()) {
		}

		// generate kml
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();
		System.out.println("finished in: " + time + " msec");

	}// END: main

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private static class Branches implements Runnable {

		public void run() {

			try {
				// this is for Branches folder:
				String branchesDescription = null;
				Layer branchesLayer = new Layer("Branches", branchesDescription);

				int branchStyleId = 1;
				for (Node node : tree.getNodes()) {

					if (!tree.isRoot(node)) {

						double longitude = (Double) node
								.getAttribute(longitudeName);
						double latitude = (Double) node
								.getAttribute(latitudeName);

						Node parentNode = tree.getParent(node);
						double parentLongitude = (Double) parentNode
								.getAttribute(longitudeName);
						double parentLatitude = (Double) parentNode
								.getAttribute(latitudeName);

						double nodeHeight = tree.getHeight(node);

						/**
						 * altitude mapping
						 * */
						double maxAltitude = Double.NaN;
						switch (branchesAltitudeMapping) {
						case TIME:
							maxAltitude = Utils.map(nodeHeight, 0,
									treeHeightMax, 0, maxAltMapping);
							break;
						case RATE:
							maxAltitude = Utils.map(Utils
									.getDoubleNodeAttribute(node, "rate"), 0,
									treeHeightMax, 0, maxAltMapping);
							break;
						case DISTANCE:
							maxAltitude = Utils
									.map(Utils
											.RhumbDistance(parentLongitude,
													parentLatitude, longitude,
													latitude), 0, EarthRadius,
											0, maxAltMapping);
							break;
						case DEFAULT:
							maxAltitude = 0;
							break;
						}

						/**
						 * Color mapping
						 * */
						int red = (int) Double.NaN;
						int green = (int) Double.NaN;
						int blue = (int) Double.NaN;
						switch (branchesColorMapping) {
						case TIME:
							red = 255;
							green = 0;
							blue = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, 255, 0);
							break;
						case RATE:
							red = 255;
							green = 0;
							blue = (int) Utils.map(Utils
									.getDoubleNodeAttribute(node, "rate"), 0,
									treeHeightMax, 255, 0);
							break;
						case DEFAULT:
							red = 255;
							green = 0;
							blue = 0;
							break;
						}

						/**
						 * opacity mapping
						 * */
						int alpha = (int) Double.NaN;
						switch (branchesOpacityMapping) {
						case TIME:
							alpha = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, 255, 100);
							break;
						case RATE:
							alpha = (int) Utils.map(Utils
									.getDoubleNodeAttribute(node, "rate"), 0,
									treeHeightMax, 255, 100);
							break;
						case DEFAULT:
							alpha = 255;
							break;
						}

						Color col = new Color(red, green, blue, alpha);

						/**
						 * width mapping
						 * */
						double width = Double.NaN;
						switch (branchesWidthMapping) {
						case TIME:
							width = Utils.map(nodeHeight, 0, treeHeightMax,
									3.5, 10.0);
							break;
						case RATE:
							width = Utils.map(Utils.getDoubleNodeAttribute(
									node, "rate"), 0, treeHeightMax, 4.0, 5.0);
							break;
						case DEFAULT:
							width = 4.5;
							break;
						}

						Style linesStyle = new Style(col, width);
						linesStyle.setId("branch_style" + branchStyleId);
						branchStyleId++;

						SpreadDate mrsd = new SpreadDate(mrsdString);
						int days = (int) (nodeHeight * timescaler);
						double startTime = mrsd.minus(days);

						branchesLayer
								.addItem(new Line((parentLongitude + ","
										+ parentLatitude + ":" + longitude
										+ "," + latitude), // name
										new Coordinates(parentLongitude,
												parentLatitude), // startCoords
										startTime, // double startime
										linesStyle, // style startstyle
										new Coordinates(longitude, latitude), // endCoords
										0.0, // double endtime
										linesStyle, // style endstyle
										maxAltitude, // double maxAltitude
										0.0) // double duration
								);

					}
				}// END: node loop

				layers.add(branchesLayer);

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}// END: Branches class

	private static class Polygons implements Runnable {
		public void run() {
			// ////////////////
			// ---POLYGONS---//
			// ////////////////
			try {
				// this is for Polygons folder:
				String polygonsDescription = null;
				Layer polygonsLayer = new Layer("Polygons", polygonsDescription);

				int polygonsStyleId = 1;
				for (Node node : tree.getNodes()) {
					if (!tree.isRoot(node)) {
						if (!tree.isExternal(node)) {

							double nodeHeight = tree.getHeight(node);
							int modality = Utils.getIntegerNodeAttribute(node,
									coordinatesName + "_" + HPD
											+ "HPD_modality");

							for (int i = 1; i <= modality; i++) {

								Object[] longitudeHPD = Utils
										.getArrayNodeAttribute(node,
												longitudeName + "_" + HPD
														+ "HPD_" + i);
								Object[] latitudeHPD = Utils
										.getArrayNodeAttribute(node,
												latitudeName + "_" + HPD
														+ "HPD_" + i);

								/**
								 * Color mapping
								 * */
								int red = (int) Double.NaN;
								int green = (int) Double.NaN;
								int blue = (int) Double.NaN;
								switch (polygonsColorMapping) {
								case TIME:
									red = 55;
									green = (int) Utils.map(nodeHeight, 0,
											treeHeightMax, 255, 0);
									blue = 0;
									break;
								case RATE:
									red = 55;
									green = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													"rate"), 0, treeHeightMax,
											255, 0);
									blue = 0;
									break;
								case DEFAULT:
									red = 0;
									green = 255;
									blue = 0;
									break;
								}

								/**
								 * opacity mapping
								 * */
								int alpha = (int) Double.NaN;
								switch (polygonsOpacityMapping) {
								case TIME:
									alpha = (int) Utils.map(nodeHeight, 0,
											treeHeightMax, 100, 255);
									break;
								case RATE:
									alpha = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													"rate"), 0, treeHeightMax,
											100, 255);
									break;
								case DEFAULT:
									alpha = 255;
									break;
								}

								Color col = new Color(red, green, blue, alpha);
								Style polygonsStyle = new Style(col, 0);
								polygonsStyle.setId("polygon_style"
										+ polygonsStyleId);

								SpreadDate mrsd = new SpreadDate(mrsdString);
								int days = (int) (nodeHeight * timescaler);
								double startTime = mrsd.minus(days);

								polygonsLayer.addItem(new Polygon("node"
										+ polygonsStyleId + "_" + HPD + "HPD"
										+ "_" + i, // String name
										Utils.ParsePolygons(longitudeHPD,
												latitudeHPD),// List<Coordinates>
										polygonsStyle, // Style style
										startTime, // double startime
										0.0 // double duration
										));

								polygonsStyleId++;

							}// END: modality loop

						}
					}
				}// END: nodes loop

				layers.add(polygonsLayer);

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}// END: polygons class

}// END: ContinuousTreeToKML class
