package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;

public class ContinuousTreeToKML {

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// Earths radius in km
	private static final double EarthRadius = 6371;

	private RootedTree tree;
	private String coordinatesName;
	private String HPD;
	private String mrsdString;
	private SpreadDate mrsd;
	private int numberOfIntervals;
	private double timescaler;
	private double rootHeight;
	private List<Layer> layers;
	private double maxAltMapping;

	private double minPolygonRedMapping;
	private double minPolygonGreenMapping;
	private double minPolygonBlueMapping;
	private double minPolygonOpacityMapping;

	private double maxPolygonRedMapping;
	private double maxPolygonGreenMapping;
	private double maxPolygonBlueMapping;
	private double maxPolygonOpacityMapping;

	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double branchWidth;
	private String longitudeName;
	private String latitudeName;
	private String userAttribute;
	private double treeHeightMax;
	private TreeImporter importer;
	private PrintWriter writer;

	private enum branchesMappingEnum {
		TIME, DISTANCE, DEFAULT, USER
	}

	private enum polygonsMappingEnum {
		TIME, RATE, USER
	}

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private timescalerEnum timescalerSwitcher;
	private branchesMappingEnum branchesColorMapping;
	private branchesMappingEnum branchesOpacityMapping;
	private branchesMappingEnum branchesAltitudeMapping;
	private polygonsMappingEnum polygonsColorMapping;
	private polygonsMappingEnum polygonsOpacityMapping;

	public ContinuousTreeToKML() {

		// parse combobox choices here
		timescalerSwitcher = timescalerEnum.YEARS;
		branchesColorMapping = branchesMappingEnum.TIME;
		branchesOpacityMapping = branchesMappingEnum.TIME;
		branchesAltitudeMapping = branchesMappingEnum.DISTANCE;
		polygonsColorMapping = polygonsMappingEnum.TIME;
		polygonsOpacityMapping = polygonsMappingEnum.TIME;

	}// END: ContinuousTreeToKML()

	public void setHPD(String percent) {
		HPD = percent;
	}

	public void setCoordinatesName(String name) {
		coordinatesName = name;
	}

	public void setMrsdString(String mrsd) {
		mrsdString = mrsd;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setMaxAltitudeMapping(double max) {
		maxAltMapping = max;
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
	}

	public void setTreePath(String path) throws FileNotFoundException {
		importer = new NexusImporter(new FileReader(path));
	}

	public void setUserAttribute(String attribute) {
		userAttribute = attribute;
	}

	public void setMinPolygonRedMapping(double min) {
		minPolygonRedMapping = min;
	}

	public void setMinPolygonGreenMapping(double min) {
		minPolygonGreenMapping = min;
	}

	public void setMinPolygonBlueMapping(double min) {
		minPolygonBlueMapping = min;
	}

	public void setMinPolygonOpacityMapping(double min) {
		minPolygonOpacityMapping = min;
	}

	public void setMaxPolygonRedMapping(double max) {
		maxPolygonRedMapping = max;
	}

	public void setMaxPolygonGreenMapping(double max) {
		maxPolygonGreenMapping = max;
	}

	public void setMaxPolygonBlueMapping(double max) {
		maxPolygonBlueMapping = max;
	}

	public void setMaxPolygonOpacityMapping(double max) {
		maxPolygonOpacityMapping = max;
	}

	public void setMinBranchRedMapping(double min) {
		minBranchRedMapping = min;
	}

	public void setMinBranchGreenMapping(double min) {
		minBranchGreenMapping = min;
	}

	public void setMinBranchBlueMapping(double min) {
		minBranchBlueMapping = min;
	}

	public void setMinBranchOpacityMapping(double min) {
		minBranchOpacityMapping = min;
	}

	public void setMaxBranchRedMapping(double max) {
		maxBranchRedMapping = max;
	}

	public void setMaxBranchGreenMapping(double max) {
		maxBranchGreenMapping = max;
	}

	public void setMaxBranchBlueMapping(double max) {
		maxBranchBlueMapping = max;
	}

	public void setMaxBranchOpacityMapping(double max) {
		maxBranchOpacityMapping = max;
	}

	public void setBranchWidth(double width) {
		branchWidth = width;
	}

	public void GenerateKML() throws IOException, ImportException,
			ParseException {

		// start timing
		time = -System.currentTimeMillis();

		// this is to choose the proper time scale
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
		mrsd = new SpreadDate(mrsdString);
		TimeLine timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * timescaler), mrsd.getTime(),
				numberOfIntervals);

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// Execute threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		executor.submit(new Branches());
		executor.submit(new Polygons());
		executor.shutdown();
		// Wait until all threads are finished
		while (!executor.isTerminated()) {
		}

		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();

	}// END: GenerateKML() method

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private class Branches implements Runnable {

		public void run() {

			try {

				// this is for Branches folder:
				String branchesDescription = null;
				Layer branchesLayer = new Layer("Branches", branchesDescription);

				int branchStyleId = 1;
				for (Node node : tree.getNodes()) {

					if (!tree.isRoot(node)) {

						double longitude = Utils.getDoubleNodeAttribute(node,
								longitudeName);
						double latitude = Utils.getDoubleNodeAttribute(node,
								latitudeName);

						Node parentNode = tree.getParent(node);
						double parentLongitude = (Double) parentNode
								.getAttribute(longitudeName);
						double parentLatitude = (Double) parentNode
								.getAttribute(latitudeName);

						double nodeHeight = tree.getHeight(node);
						double parentHeight = tree.getHeight(parentNode);

						/**
						 * altitude mapping
						 * */
						double maxAltitude = Double.NaN;
						switch (branchesAltitudeMapping) {
						case TIME:
							maxAltitude = Utils.map(nodeHeight, 0,
									treeHeightMax, 0, maxAltMapping);
							break;
						case USER:
							maxAltitude = Utils.map(
									Utils.getDoubleNodeAttribute(node,
											userAttribute), 0, treeHeightMax,
									0, maxAltMapping);
							break;
						case DISTANCE:
							maxAltitude = Utils
									.map(Utils
											.rhumbDistance(parentLongitude,
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
							red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
									minBranchRedMapping, maxBranchRedMapping);

							green = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchGreenMapping,
									maxBranchGreenMapping);

							blue = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchBlueMapping,
									maxBranchBlueMapping);

							break;
						case USER:
							red = (int) Utils.map(Utils.getDoubleNodeAttribute(
									node, userAttribute), 0, treeHeightMax,
									minBranchRedMapping, maxBranchRedMapping);

							green = (int) Utils.map(
									Utils.getDoubleNodeAttribute(node,
											userAttribute), 0, treeHeightMax,
									minBranchGreenMapping,
									maxBranchGreenMapping);

							blue = (int) Utils.map(
									Utils.getDoubleNodeAttribute(node,
											userAttribute), 0, treeHeightMax,
									minBranchBlueMapping, maxBranchBlueMapping);

							break;
						}

						/**
						 * opacity mapping
						 * */
						int alpha = (int) Double.NaN;
						switch (branchesOpacityMapping) {
						case TIME:
							alpha = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, maxBranchOpacityMapping,
									minBranchOpacityMapping);
							break;
						case USER:
							alpha = (int) Utils.map(
									Utils.getDoubleNodeAttribute(node,
											userAttribute), 0, treeHeightMax,
									maxBranchOpacityMapping,
									minBranchOpacityMapping);
							break;
						case DEFAULT:
							alpha = 255;
							break;
						}

						Color col = new Color(red, green, blue, alpha);

						Style linesStyle = new Style(col, branchWidth);
						linesStyle.setId("branch_style" + branchStyleId);
						branchStyleId++;

						double startTime = mrsd
								.minus((int) (nodeHeight * timescaler));
						double endTime = mrsd
								.minus((int) (parentHeight * timescaler));

						branchesLayer
								.addItem(new Line((parentLongitude + ","
										+ parentLatitude + ":" + longitude
										+ "," + latitude), // name
										new Coordinates(parentLongitude,
												parentLatitude), // startCoords
										startTime, // double startime
										linesStyle, // style startstyle
										new Coordinates(longitude, latitude), // endCoords
										endTime, // double endtime
										linesStyle, // style endstyle
										maxAltitude, // double maxAltitude
										0.0) // double duration
								);

					}
				}// END: node loop

				layers.add(branchesLayer);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Branches class

	// ////////////////
	// ---POLYGONS---//
	// ////////////////
	private class Polygons implements Runnable {

		public void run() {

			try {

				// this is for Polygons folder:
				String polygonsDescription = null;
				Layer polygonsLayer = new Layer("Polygons", polygonsDescription);

				int polygonsStyleId = 1;
				for (Node node : tree.getNodes()) {

					if (!tree.isRoot(node)) {

						if (!tree.isExternal(node)) {

							int modality = Utils.getIntegerNodeAttribute(node,
									coordinatesName + "_" + HPD
											+ "HPD_modality");

							for (int i = 1; i <= modality; i++) {

								Object[] longitudeHPD = Utils
										.getObjectArrayNodeAttribute(node,
												longitudeName + "_" + HPD
														+ "HPD_" + i);
								Object[] latitudeHPD = Utils
										.getObjectArrayNodeAttribute(node,
												latitudeName + "_" + HPD
														+ "HPD_" + i);

								/**
								 * Color mapping
								 * */
								double nodeHeight = tree.getHeight(node);

								int red = (int) Double.NaN;
								int green = (int) Double.NaN;
								int blue = (int) Double.NaN;
								switch (polygonsColorMapping) {
								case TIME:
									red = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonRedMapping,
											maxPolygonRedMapping);

									green = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonGreenMapping,
											maxPolygonGreenMapping);

									blue = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonBlueMapping,
											maxPolygonBlueMapping);

									break;
								case USER:
									red = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													userAttribute), 0,
											treeHeightMax,
											minPolygonRedMapping,
											maxPolygonRedMapping);

									green = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													userAttribute), 0,
											treeHeightMax,
											minPolygonGreenMapping,
											maxPolygonGreenMapping);

									blue = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													userAttribute), 0,
											treeHeightMax,
											minPolygonBlueMapping,
											maxPolygonBlueMapping);

									break;
								}

								/**
								 * opacity mapping
								 * */
								int alpha = (int) Double.NaN;
								switch (polygonsOpacityMapping) {
								case TIME:
									alpha = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											maxPolygonOpacityMapping,
											minPolygonOpacityMapping);
									break;
								case USER:
									alpha = (int) Utils.map(Utils
											.getDoubleNodeAttribute(node,
													userAttribute), 0,
											treeHeightMax,
											maxPolygonOpacityMapping,
											minPolygonOpacityMapping);
									break;
								}

								Color col = new Color(red, green, blue, alpha);
								Style polygonsStyle = new Style(col, 0);
								polygonsStyle.setId("polygon_style"
										+ polygonsStyleId);

								int days = (int) (nodeHeight * timescaler);
								double startTime = mrsd.minus(days);

								polygonsLayer.addItem(new Polygon("node"
										+ polygonsStyleId + "_" + HPD + "HPD"
										+ "_" + i, // String name
										Utils.parsePolygons(longitudeHPD,
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

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: polygons class

}// END: ContinuousTreeToKML class
