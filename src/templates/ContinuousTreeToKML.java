package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
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
import utils.ThreadLocalSpreadDate;
import utils.Utils;

public class ContinuousTreeToKML {

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// how many days one year holds
	private static final int DaysInYear = 365;
	// Earths radius in km
	private static final double EarthRadius = 6371;

	private RootedTree tree;
	private String coordinatesName;
	private String HPDString;
	private String mrsdString;
	private ThreadLocalSpreadDate mrsd;
	private int numberOfIntervals;
	private double timescaler;
	private double rootHeight;
	private ArrayList<Layer> layers;
	private TimeLine timeLine;
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
	private double treeHeightMax;
	private TreeImporter importer;
	private PrintWriter writer;

	public ContinuousTreeToKML() {
	}// END: ContinuousTreeToKML()

	public void setTimescaler(double timescaler) {
		this.timescaler = timescaler;
	}

	public void setHPDString(String HPDString) {
		this.HPDString = HPDString;
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

		// import tree
		tree = (RootedTree) importer.importNextTree();

		// this is for time calculations
		rootHeight = tree.getHeight(tree.getRootNode());

		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);

		// this is for mappings
		treeHeightMax = Utils.getTreeHeightMax(tree);

		// This is a general time span for the tree
		mrsd = new ThreadLocalSpreadDate(mrsdString);
		timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * DaysInYear * timescaler), mrsd
				.getTime(), numberOfIntervals);

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

	}// END: GenerateKML

	public ArrayList<Layer> getLayers() throws IOException, ImportException,
			ParseException {

		// start timing
		time = -System.currentTimeMillis();

		// import tree
		tree = (RootedTree) importer.importNextTree();

		// this is for time calculations
		rootHeight = tree.getHeight(tree.getRootNode());

		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);

		// this is for mappings
		treeHeightMax = Utils.getTreeHeightMax(tree);

		// This is a general time span for the tree
		mrsd = new ThreadLocalSpreadDate(mrsdString);
		timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * DaysInYear * timescaler), mrsd
				.getTime(), numberOfIntervals);

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

		// stop timing
		time += System.currentTimeMillis();

		return layers;
	}// END: getLayers

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

						Double longitude = (Double) node
								.getAttribute(longitudeName);
						Double latitude = (Double) node
								.getAttribute(latitudeName);

						Double nodeHeight = Utils.getNodeHeight(tree, node);

						Node parentNode = tree.getParent(node);

						Double parentLongitude = (Double) parentNode
								.getAttribute(longitudeName);
						Double parentLatitude = (Double) parentNode
								.getAttribute(latitudeName);

						Double parentHeight = Utils.getNodeHeight(tree,
								parentNode);

						if (longitude != null && latitude != null
								&& parentLongitude != null
								&& parentLatitude != null) {

							/**
							 * altitude mapping
							 * */

							double maxAltitude = Utils
									.map(Utils
											.rhumbDistance(parentLongitude,
													parentLatitude, longitude,
													latitude), 0, EarthRadius,
											0, maxAltMapping);

							/**
							 * Color mapping
							 * */

							int red = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchRedMapping,
									maxBranchRedMapping);

							int green = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchGreenMapping,
									maxBranchGreenMapping);

							int blue = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchBlueMapping,
									maxBranchBlueMapping);

							/**
							 * opacity mapping
							 * */
							int alpha = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, maxBranchOpacityMapping,
									minBranchOpacityMapping);

							Color col = new Color(red, green, blue, alpha);

							Style linesStyle = new Style(col, branchWidth);
							linesStyle.setId("branch_style" + branchStyleId);
							branchStyleId++;

							double startTime = mrsd.minus((int) (nodeHeight
									* DaysInYear * timescaler));
							double endTime = mrsd.minus((int) (parentHeight
									* DaysInYear * timescaler));

							branchesLayer.addItem(new Line((parentLongitude
									+ "," + parentLatitude + ":" + longitude
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

						}// END: null checks
					}// END: root check
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

							Integer modality = (Integer) node
									.getAttribute(coordinatesName + "_"
											+ HPDString + "_modality");

							if (modality != null) {

								for (int i = 1; i <= modality; i++) {

									Object[] longitudeHPD = Utils
											.getObjectArrayNodeAttribute(node,
													longitudeName + "_"
															+ HPDString + "_"
															+ i);

									Object[] latitudeHPD = Utils
											.getObjectArrayNodeAttribute(node,
													latitudeName + "_"
															+ HPDString + "_"
															+ i);

									/**
									 * Color mapping
									 * */
									double nodeHeight = tree.getHeight(node);

									int red = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonRedMapping,
											maxPolygonRedMapping);

									int green = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonGreenMapping,
											maxPolygonGreenMapping);

									int blue = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											minPolygonBlueMapping,
											maxPolygonBlueMapping);

									/**
									 * opacity mapping
									 * */
									int alpha = (int) Utils.map(nodeHeight, 0,
											treeHeightMax,
											maxPolygonOpacityMapping,
											minPolygonOpacityMapping);

									Color col = new Color(red, green, blue,
											alpha);
									Style polygonsStyle = new Style(col, 0);
									polygonsStyle.setId("polygon_style"
											+ polygonsStyleId);

									int days = (int) (nodeHeight * DaysInYear * timescaler);
									double startTime = mrsd.minus(days);

									polygonsLayer.addItem(new Polygon("node"
											+ polygonsStyleId + "_" + HPDString
											+ "_" + i, // String name
											Utils.parsePolygons(longitudeHPD,
													latitudeHPD),// List<Coordinates>
											polygonsStyle, // Style style
											startTime, // double startime
											0.0 // double duration
											));

									polygonsStyleId++;

								}// END: modality loop
							}// END: null check
						}// END: external node check
					}// END: root check
				}// END: nodes loop

				layers.add(polygonsLayer);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: polygons class

}// END: ContinuousTreeToKML class
