package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import utils.ReadLocations;
import utils.SpreadDate;
import utils.Utils;

public class DiscreteTreeToKML {

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// Earths radius in km
	private static final double EarthRadius = 6371;

	private ReadLocations data;
	private RootedTree tree;
	private String mrsdString;
	private int numberOfIntervals;
	private double timescaler;
	private String stateAttName;
	private String userAttribute;
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

	private enum branchesMappingEnum {
		TIME, DISTANCE, DEFAULT, USER
	}

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private timescalerEnum timescalerSwitcher;
	private branchesMappingEnum branchesColorMapping;
	private branchesMappingEnum branchesOpacityMapping;
	private branchesMappingEnum altitudeMapping;

	public DiscreteTreeToKML() {

		// parse combobox choices here
		timescalerSwitcher = timescalerEnum.YEARS;
		branchesColorMapping = branchesMappingEnum.TIME;
		branchesOpacityMapping = branchesMappingEnum.TIME;
		altitudeMapping = branchesMappingEnum.DISTANCE;

	}// END: DiscreteTreeToKML()

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

	public void setLocationFilePath(String path) throws ParseException {
		data = new ReadLocations(path);
	}

	public void setUserAttribute(String attribute) {
		userAttribute = attribute;
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

		// This is a general time span for the tree
		SpreadDate mrsd = new SpreadDate(mrsdString);
		TimeLine timeLine = new TimeLine(mrsd.getTime()
				- (rootHeight * DayInMillis * timescaler), mrsd.getTime(),
				numberOfIntervals);

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// Execute threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		executor.submit(new SanityCheck());
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

			for (int i = 0; i < data.nrow; i++) {
				placesLayer.addItem(new Place(data.locations[i], null,
						new Coordinates(data.coordinates[i][1],
								data.coordinates[i][0]), 0, 0));
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

						String state = Utils.getStringNodeAttribute(node,
								stateAttName);

						Node parentNode = tree.getParent(node);
						String parentState = (String) parentNode
								.getAttribute(stateAttName);

						if (!state.toLowerCase().equals(
								parentState.toLowerCase())) {

							float longitude = Utils.MatchStateCoordinate(data,
									state, 0);
							float latitude = Utils.MatchStateCoordinate(data,
									state, 1);

							float parentLongitude = Utils.MatchStateCoordinate(
									data, parentState, 0);
							float parentLatitude = Utils.MatchStateCoordinate(
									data, parentState, 1);

							double nodeHeight = tree.getHeight(node);

							/**
							 * altitude mapping
							 * */
							double maxAltitude = Double.NaN;
							switch (altitudeMapping) {
							case TIME:
								maxAltitude = (int) Utils.map(nodeHeight, 0,
										treeHeightMax, 0, maxAltMapping);
								break;

							case USER:
								maxAltitude = Utils.map(Utils
										.getDoubleNodeAttribute(node,
												userAttribute), 0,
										treeHeightMax, 0, maxAltMapping);
								break;

							case DISTANCE:
								maxAltitude = Utils.map(Utils.RhumbDistance(
										parentLongitude, parentLatitude,
										longitude, latitude), 0, EarthRadius,
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

								red = (int) Utils.map(nodeHeight, 0,
										treeHeightMax, minBranchRedMapping,
										maxBranchRedMapping);

								green = (int) Utils.map(nodeHeight, 0,
										treeHeightMax, minBranchGreenMapping,
										maxBranchGreenMapping);

								blue = (int) Utils.map(nodeHeight, 0,
										treeHeightMax, minBranchBlueMapping,
										maxBranchBlueMapping);
								break;

							case USER:
								red = (int) Utils.map(Utils
										.getDoubleNodeAttribute(node,
												userAttribute), 0,
										treeHeightMax, minBranchRedMapping,
										maxBranchRedMapping);

								green = (int) Utils.map(Utils
										.getDoubleNodeAttribute(node,
												userAttribute), 0,
										treeHeightMax, minBranchGreenMapping,
										maxBranchGreenMapping);

								blue = (int) Utils.map(Utils
										.getDoubleNodeAttribute(node,
												userAttribute), 0,
										treeHeightMax, minBranchBlueMapping,
										maxBranchBlueMapping);
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
										treeHeightMax, maxBranchOpacityMapping,
										minBranchOpacityMapping);
								break;
							case USER:
								alpha = (int) Utils.map(Utils
										.getDoubleNodeAttribute(node,
												userAttribute), 0,
										treeHeightMax, maxBranchOpacityMapping,
										minBranchOpacityMapping);
								break;
							case DEFAULT:
								alpha = 255;
								break;
							}

							Color col = new Color(red, green, blue, alpha);

							Style linesStyle = new Style(col, branchWidth);
							linesStyle.setId("branch_style" + branchStyleId);

							SpreadDate mrsd = new SpreadDate(mrsdString);
							int days = (int) (nodeHeight * timescaler);
							double startTime = mrsd.minus(days);

							branchesLayer.addItem(new Line(
									(parentState + ":" + state), // string name
									new Coordinates(parentLatitude,
											parentLongitude), startTime, // startime
									linesStyle, // style startstyle
									new Coordinates(latitude, longitude), // endcoords
									0.0, // double endtime
									linesStyle, // style endstyle
									maxAltitude, // double maxAltitude
									0.0) // double duration
									);

							branchStyleId++;
						}
					}
				}// END: nodes loop

				layers.add(branchesLayer);

			} catch (ParseException e) {
				e.printStackTrace();

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

				double[][] numberOfLineages = CountLineagesHoldingState(
						numberOfIntervals, rootHeight);
				double lineagesCountMax = Utils.get2DArrayMax(numberOfLineages);

				int circleStyleId = 1;
				for (int i = 0; i < (numberOfIntervals - 1); i++) {
					for (int j = 0; j < (data.locations.length); j++) {

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
							 * 
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

							int days = (int) (numberOfLineages[i][0] * timescaler);
							SpreadDate mrsd = new SpreadDate(mrsdString);

							double startTime = mrsd.minus(days);
							// this is to get duration in milliseconds:
							double duration = ((rootHeight - numberOfLineages[i][0]) / (i + 1))
									* DayInMillis;

							circlesLayer.addItem(new Polygon(data.locations[j]
									+ "_" + radius + "_" + "km", // String name
									Utils.GenerateCircle( // List<Coordinates>
											data.coordinates[j][1], // centerLong
											data.coordinates[j][0], // centerLat
											radius, // radius
											36), // numPoints
									circlesStyle, // Style style
									startTime, // double startime
									duration * timescaler // double duration
							));

						}
					}// END: col loop
				}// END: row loop

				layers.add(circlesLayer);

			} catch (ParseException e) {
				e.printStackTrace();

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Circles class

	private class SanityCheck implements Runnable {

		public void run() {

			Set<String> uniqueTreeStates = new HashSet<String>();
			for (Node node : tree.getNodes()) {
				if (!tree.isRoot(node)) {

					uniqueTreeStates.add(Utils.getStringNodeAttribute(node,
							stateAttName));

				}
			}// END: node loop

			Object[] uniqueTreeStatesArray = uniqueTreeStates.toArray();

			for (int i = 0; i < data.locations.length; i++) {

				String state = null;

				for (int j = 0; j < uniqueTreeStatesArray.length; j++) {

					if (data.locations[i].toLowerCase().equals(
							((String) uniqueTreeStatesArray[j]).toLowerCase())) {

						state = data.locations[i];

					}// END: if location and discrete states match

				}// END: unique discrete states loop

				if (state == null) { // if none matches
					System.out.println("Location " + data.locations[i]
							+ " does not fit any of the discrete states");
				}
			}// END: locations loop

		}// END: run
	}// END: SanityCheck class

	private double[][] CountLineagesHoldingState(int numberOfIntervals,
			double rootHeight) {

		double delta = rootHeight / numberOfIntervals;
		double[][] numberOfLineages = new double[(numberOfIntervals - 1)][data.locations.length + 1];

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			numberOfLineages[i][0] = rootHeight - ((i + 1) * delta);
		}

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			for (int j = 0; j < (data.locations.length); j++) {

				int numberOfLineagesOfState = 0;

				for (Node node : tree.getNodes()) {

					if (!tree.isRoot(node)) {

						Node parentNode = tree.getParent(node);
						String state = (String) node.getAttribute(stateAttName);

						String parentState = (String) parentNode
								.getAttribute(stateAttName);

						if ((tree.getHeight(node) <= numberOfLineages[i][0])
								&& (tree.getHeight(parentNode) > numberOfLineages[i][0])) {

							if ((state.toLowerCase().equals(parentState
									.toLowerCase()))
									&& (parentState.toLowerCase()
											.equals(data.locations[j]
													.toLowerCase()))) {

								numberOfLineagesOfState++;

							}
						}
					}
				}// END: node loop

				numberOfLineages[i][j + 1] = numberOfLineagesOfState;

			}// END: col loop
		}// END: row loop

		return numberOfLineages;
	}// END: CountLineagesHoldingState

}// END: DiscreteTreeToKML class