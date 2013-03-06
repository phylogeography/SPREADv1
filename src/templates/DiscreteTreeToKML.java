package templates;

import generator.KMLGenerator;
import gui.InteractiveTableModel;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import structure.Place;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.ThreadLocalSpreadDate;
import utils.Utils;

public class DiscreteTreeToKML {

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// how many days one year holds
	private static final int DaysInYear = 365;
	// Earths radius in km
	private static final double EarthRadius = 6371;

	private RootedTree tree;
	private String stateAttName;
	private InteractiveTableModel table;
	private String mrsdString;
	private ThreadLocalSpreadDate mrsd;
	private int numberOfIntervals;
	private int timescaler;
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

	private double polygonsRadiusMultiplier;
	private double branchWidth;
	private PrintWriter writer;
	private TreeImporter importer;

	private Random generator;

	public DiscreteTreeToKML() {

		generator = new Random();

	}// END: DiscreteTreeToKML()

	public void setTimescaler(int timescaler) {
		this.timescaler = timescaler;
	}

	public void setStateAttName(String name) {
		stateAttName = name;
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

	public void setTable(InteractiveTableModel tableModel) {
		table = tableModel;
	}

	public void setPolygonsRadiusMultiplier(double multiplier) {
		polygonsRadiusMultiplier = multiplier;
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
			ParseException, RuntimeException {

		// start timing
		time = -System.currentTimeMillis();

		tree = (RootedTree) importer.importNextTree();

		// this is for time calculations
		rootHeight = tree.getHeight(tree.getRootNode());

		// This is a general time span for the tree
		mrsd = new ThreadLocalSpreadDate(mrsdString);
		TimeLine timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * DaysInYear * timescaler), mrsd
				.getTime(), numberOfIntervals);

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// Execute threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		executor.submit(new Places());
		executor.submit(new Branches());
		executor.submit(new Circles());
		executor.shutdown();

		// Wait until all threads are finished
		while (!executor.isTerminated()) {
		}

		// generate kml
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();

	}// END: GenerateKML

	// //////////////
	// ---PLACES---//
	// //////////////

	private class Places implements Runnable {

		public void run() {

			// this is for Places folder:
			String placesDescription = null;
			Layer placesLayer = new Layer("Places", placesDescription);

			for (int i = 0; i < table.getRowCount(); i++) {

				String name = String.valueOf(table.getValueAt(i, 0));

				Double longitude = Double.valueOf(String.valueOf(table
						.getValueAt(i, 2)));

				Double latitude = Double.valueOf(String.valueOf(table
						.getValueAt(i, 1)));

				placesLayer.addItem(new Place(name, null, new Coordinates(
						longitude, latitude), 0, 0));
			}

			layers.add(placesLayer);
		}
	}// END: Places class

	// ////////////////
	// ---BRANCHES---//
	// ////////////////

	private class Branches implements Runnable {

		public void run() {

			try {

				// this is for Branches folder:
				String branchesDescription = null;
				Layer branchesLayer = new Layer("Branches", branchesDescription);

				double treeHeightMax = Utils.getTreeHeightMax(tree);

				int branchStyleId = 1;
				for (Node node : tree.getNodes()) {
					if (!tree.isRoot(node)) {

						String state = getRandomState((String) node
								.getAttribute(stateAttName), true);

						Node parentNode = tree.getParent(node);

						String parentState = getRandomState((String) parentNode
								.getAttribute(stateAttName), false);

						if (state != null && parentState != null) {

							if (!state.toLowerCase().equals(
									parentState.toLowerCase())) {

								float longitude = Utils.matchStateCoordinate(
										table, state, 2);
								float latitude = Utils.matchStateCoordinate(
										table, state, 1);

								float parentLongitude = Utils
										.matchStateCoordinate(table,
												parentState, 2);
								float parentLatitude = Utils
										.matchStateCoordinate(table,
												parentState, 1);

								double nodeHeight = Utils.getNodeHeight(tree,
										node);

								double parentHeight = Utils.getNodeHeight(tree,
										parentNode);

								/**
								 * altitude mapping
								 * */
								
								double maxAltitude = Utils.map(Utils
										.rhumbDistance(parentLongitude,
												parentLatitude, longitude,
												latitude), 0, EarthRadius, 0,
										maxAltMapping);

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
								linesStyle
										.setId("branch_style" + branchStyleId);

								double startTime = mrsd.minus((int) (nodeHeight
										* DaysInYear * timescaler));
								double endTime = mrsd.minus((int) (parentHeight
										* DaysInYear * timescaler));

								// TODO
//								System.out.println("*******************************");
//								System.out.println(parentState + ":" + state);
//								System.out.println("*******************************");
								
								branchesLayer.addItem(new Line((parentState
										+ ":" + state), // string name
										new Coordinates(parentLongitude,
												parentLatitude), // startcoords
										startTime, // startime
										linesStyle, // style startstyle
										new Coordinates(longitude, latitude), // endcoords
										endTime, // double endtime
										linesStyle, // style endstyle
										maxAltitude, // double maxAltitude
										0.0) // double duration
										);

								branchStyleId++;

							}// END: state and parent state equality check
						}// END: null check
					}// END: root check
				}// END: nodes loop

				layers.add(branchesLayer);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END Branches class

	// ///////////////
	// ---CIRCLES---//
	// ///////////////

	private class Circles implements Runnable {

		public void run() {

			try {

				// this is for Circles folder:
				String circlesDescription = null;
				Layer circlesLayer = new Layer("Circles", circlesDescription);

				double[][] numberOfLineages = countLineagesHoldingState(
						numberOfIntervals, rootHeight);
				double lineagesCountMax = Utils.get2DArrayMax(numberOfLineages);

				int circleStyleId = 1;
				for (int i = 0; i < (numberOfIntervals - 1); i++) {
					for (int j = 0; j < (table.getRowCount()); j++) {

						if (numberOfLineages[i][j + 1] > 0) {

							/**
							 * Color mapping
							 * */
							int red = (int) Utils.map(
									numberOfLineages[i][j + 1], 0,
									lineagesCountMax, minPolygonRedMapping,
									maxPolygonRedMapping);

							int green = (int) Utils.map(
									numberOfLineages[i][j + 1], 0,
									lineagesCountMax, minPolygonGreenMapping,
									maxPolygonGreenMapping);

							int blue = (int) Utils.map(
									numberOfLineages[i][j + 1], 0,
									lineagesCountMax, minPolygonBlueMapping,
									maxPolygonBlueMapping);

							/**
							 * Opacity mapping
							 * Larger the values more opaque the colors
							 * */
							int alpha = (int) Utils.map(
									numberOfLineages[i][j + 1], 0,
									lineagesCountMax, maxPolygonOpacityMapping,
									minPolygonOpacityMapping);

							Color col = new Color(red, green, blue, alpha);

							Style circlesStyle = new Style(col, 0);
							circlesStyle.setId("circle_style" + circleStyleId);
							circleStyleId++;

							double radius = Math.round(100 * Math
									.sqrt(numberOfLineages[i][j + 1]))
									* polygonsRadiusMultiplier;

							int days = (int) (numberOfLineages[i][0]
									* DaysInYear * timescaler);
							double startTime = mrsd.minus(days);

							// this is to get duration in milliseconds:
							double duration = ((rootHeight - numberOfLineages[i][0]) / (i + 1))
									* DayInMillis;

							String name = String
									.valueOf(table.getValueAt(j, 0));

							Double longitude = Double.valueOf(String
									.valueOf(table.getValueAt(j, 1)));

							Double latitude = Double.valueOf(String
									.valueOf(table.getValueAt(j, 2)));

							circlesLayer.addItem(new Polygon(name + "_"
									+ radius + "_" + "km", // String name
									Utils.generateCircle( // List<Coordinates>
											latitude, // centerLat
											longitude, // centerLong
											radius, // radius
											36), // numPoints
									circlesStyle, // Style style
									startTime, // double startime
									duration * DaysInYear * timescaler // duration
							));

						}
					}// END: row loop
				}// END: col loop

				layers.add(circlesLayer);

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Circles class

	private double[][] countLineagesHoldingState(int numberOfIntervals,
			double rootHeight) {

		double delta = rootHeight / numberOfIntervals;
		double[][] numberOfLineages = new double[(numberOfIntervals - 1)][table
				.getRowCount() + 1];

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			numberOfLineages[i][0] = rootHeight - ((i + 1) * delta);
		}

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			for (int j = 0; j < table.getRowCount(); j++) {

				int numberOfLineagesOfState = 0;

				for (Node node : tree.getNodes()) {
					if (!tree.isRoot(node)) {

						String state = getRandomState((String) node
								.getAttribute(stateAttName), false);

						Node parentNode = tree.getParent(node);

						String parentState = getRandomState((String) parentNode
								.getAttribute(stateAttName), false);

						if (state != null && parentState != null) {

							if ((tree.getHeight(node) <= numberOfLineages[i][0])
									&& (tree.getHeight(parentNode) > numberOfLineages[i][0])) {

								String name = String.valueOf(table.getValueAt(
										j, 0));

								if ((state.toLowerCase().equals(parentState
										.toLowerCase()))
										&& (parentState.toLowerCase()
												.equals(name.toLowerCase()))) {

									numberOfLineagesOfState++;

								}// END: state
							}// END: height check
						}// END: null check
					}// END: root check
				}// END: node loop

				numberOfLineages[i][j + 1] = numberOfLineagesOfState;

			}// END: col loop
		}// END: row loop

		return numberOfLineages;
	}// END: countLineagesHoldingState

	private String getRandomState(String state, boolean verbose) {

		// pick always the same states in this run
		generator.setSeed(time);

		if (!state.contains("+")) {// single state so return state
			return state;

		} else {// this breaks ties

			if (verbose)
				System.out.println("Found combined " + stateAttName
						+ " attribute: " + state);

			state = Utils.pickRand(state.split("\\+"), generator);

			if (verbose)
				System.out.println("Randomly picking: " + state);

			return state;
		}

	}// END: getRandomState

}// END: DiscreteTreeToKML class