package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import math.MultivariateNormalDistribution;
import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

public class TimeSlicerToKML {

	public long time;

	private final int DayInMillis = 86400000;

	private TreeImporter treeImporter;
	private RootedTree tree;
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
	private TreeImporter treesImporter;
	private RootedTree currentTree;
	private String mrsdString;
	private double timescaler;
	private int numberOfIntervals;
	private int burnIn;
	private boolean impute;
	private boolean useTrueNoise;
	private String locationString;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private String precisionString;
	private List<Layer> layers;
	private int polygonsStyleId;
	private SimpleDateFormat formatter;
	private PrintWriter writer;
	private Double sliceTime;
	private TimeLine timeLine;
	private double startTime;
	private double endTime;

	private ConcurrentMap<Double, List<Coordinates>> slicesMap;

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private timescalerEnum timescalerSwitcher;

	public TimeSlicerToKML() {

		// parse combobox choices here
		timescalerSwitcher = timescalerEnum.YEARS;

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
	}

	public void setTreePath(String path) throws FileNotFoundException {
		treeImporter = new NexusImporter(new FileReader(path));
	}

	public void setTreesPath(String path) throws FileNotFoundException {
		treesImporter = new NexusImporter(new FileReader(path));
	}

	public void setMrsdString(String mrsd) {
		mrsdString = mrsd;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setBurnIn(int burnInDouble) {
		burnIn = burnInDouble;
	}

	public void setImpute(boolean imputeBoolean) {
		impute = imputeBoolean;
	}

	public void setTrueNoise(boolean trueNoiseBoolean) {
		useTrueNoise = trueNoiseBoolean;
	}

	public void setLocationAttName(String name) {
		locationString = name;
		longitudeName = (locationString + 2);
		latitudeName = (locationString + 1);
	}

	public void setRateAttName(String name) {
		rateString = name;
	}

	public void setPrecisionAttName(String name) {
		precisionString = name;
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
	}

	public void setMaxAltitudeMapping(double max) {
		maxAltMapping = max;
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
			ParseException, RuntimeException, OutOfMemoryError {

		// start timing
		time = -System.currentTimeMillis();

		System.out.println("Importing trees...");
		tree = (RootedTree) treeImporter.importNextTree();

		System.out.println("Analyzing trees...");

		// This is for collecting coordinates into polygons
		slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

		// Executor for threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		int readTrees = 0;
		while (treesImporter.hasTree()) {

			currentTree = (RootedTree) treesImporter.importNextTree();
			synchronized (slicesMap) {
				if (readTrees >= burnIn) {

					// executor.submit(new AnalyzeTree());
					AnalyzeTree analyzeTree = new AnalyzeTree();
					analyzeTree.run();
				}

				readTrees++;
			}
		}

		if ((readTrees - burnIn) <= 0.0) {
			throw new RuntimeException("Burnt too many trees!");
		} else {
			System.out.println("Analyzed " + (int) (readTrees - burnIn)
					+ " trees");
		}

		// Wait until all threads are finished
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		// this is to generate kml output
		layers = new ArrayList<Layer>();
		Set<Double> hostKeys = slicesMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();
		executor = Executors.newFixedThreadPool(NTHREDS);
		formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
		timeLine = GenerateTimeLine(tree);
		startTime = timeLine.getStartTime();
		endTime = timeLine.getEndTime();

		System.out.println("Generating Polygons...");
		System.out.println("Iterating through Map...");

		polygonsStyleId = 1;
		synchronized (iterator) {
			while (iterator.hasNext()) {

				System.out.println("Key " + polygonsStyleId + "...");

				sliceTime = (Double) iterator.next();

				// executor.submit(new Polygons());
				Polygons polygons = new Polygons();
				polygons.run();

			}
		}

		executor.submit(new Branches());

		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		System.out.println("Writing to kml...");

		KMLGenerator kmloutput = new KMLGenerator();
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();
		System.out.println("Finished in: " + time + " msec \n");

	}// END: GenerateKML

	private class AnalyzeTree implements Runnable {

		public void run() {

			try {

				double treeRootHeight = tree.getHeight(tree.getRootNode());

				for (Node node : currentTree.getNodes()) {

					if (!currentTree.isRoot(node)) {

						for (int i = 0; i <= numberOfIntervals; i++) {

							Node parentNode = currentTree.getParent(node);

							double nodeHeight = currentTree.getHeight(node);
							double parentHeight = currentTree
									.getHeight(parentNode);

							Object[] location = (Object[]) Utils
									.getArrayNodeAttribute(node, locationString);
							double latitude = (Double) location[0];
							double longitude = (Double) location[1];

							Object[] parentLocation = (Object[]) Utils
									.getArrayNodeAttribute(parentNode,
											locationString);
							double parentLatitude = (Double) parentLocation[0];
							double parentLongitude = (Double) parentLocation[1];

							double sliceHeight = treeRootHeight
									- (treeRootHeight / numberOfIntervals)
									* ((double) i);

							if (nodeHeight < sliceHeight
									&& sliceHeight <= parentHeight) {

								SpreadDate mrsd = new SpreadDate(mrsdString);
								double sliceTime = mrsd
										.minus((int) (sliceHeight * timescaler));

								if (slicesMap.containsKey(sliceTime)) {

									slicesMap.get(sliceTime).add(
											new Coordinates(parentLongitude,
													parentLatitude, 0.0));

									if (impute) {

										double rate = Utils
												.getDoubleNodeAttribute(node,
														rateString);

										Object[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceHeight, nodeHeight,
												parentHeight, currentTree,
												rate, useTrueNoise);

										slicesMap
												.get(sliceTime)
												.add(
														new Coordinates(
																Double
																		.valueOf(imputedLocation[1]
																				.toString()),
																Double
																		.valueOf(imputedLocation[0]
																				.toString()),
																0.0));
									}

									slicesMap.get(sliceTime).add(
											new Coordinates(longitude,
													latitude, 0.0));

								} else {

									List<Coordinates> coords = new ArrayList<Coordinates>();

									coords.add(new Coordinates(parentLongitude,
											parentLatitude, 0.0));

									if (impute) {

										double rate = Utils
												.getDoubleNodeAttribute(node,
														rateString);

										Object[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceHeight, nodeHeight,
												parentHeight, currentTree,
												rate, useTrueNoise);

										coords.add(new Coordinates(Double
												.valueOf(imputedLocation[1]
														.toString()), Double
												.valueOf(imputedLocation[0]
														.toString()), 0.0));
									}

									coords.add(new Coordinates(longitude,
											latitude, 0.0));

									slicesMap.putIfAbsent(sliceTime, coords);

								}// END: key check
							}// END: sliceTime check
						}// END: numberOfIntervals loop
					}
				}// END: node loop

			} catch (Exception e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: AnalyzeTree

	// ///////////////////////////
	// ---CONCURRENT POLYGONS---//
	// ///////////////////////////
	private class Polygons implements Runnable {

		public void run() throws OutOfMemoryError {

			Layer polygonsLayer = new Layer("Time_Slice_"
					+ formatter.format(sliceTime), null);

			/**
			 * Color and Opacity mapping
			 * */
			int red = (int) Utils.map(sliceTime, startTime, endTime,
					minPolygonRedMapping, maxPolygonRedMapping);

			int green = (int) Utils.map(sliceTime, startTime, endTime,
					minPolygonGreenMapping, maxPolygonGreenMapping);

			int blue = (int) Utils.map(sliceTime, startTime, endTime,
					minPolygonBlueMapping, maxPolygonBlueMapping);

			int alpha = (int) Utils.map(sliceTime, startTime, endTime,
					maxPolygonOpacityMapping, minPolygonOpacityMapping);

			Color col = new Color(red, green, blue, alpha);
			Style polygonsStyle = new Style(col, 0);
			polygonsStyle.setId("polygon_style" + polygonsStyleId);

			List<Coordinates> list = slicesMap.get(sliceTime);

			double[] x = new double[list.size()];
			double[] y = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {

				x[i] = list.get(i).getLatitude();
				y[i] = list.get(i).getLongitude();

			}

			ContourMaker contourMaker = new ContourWithSynder(x, y, 200);
			ContourPath[] paths = contourMaker.getContourPaths(0.8);

			int pathCounter = 1;
			for (ContourPath path : paths) {

				double[] latitude = path.getAllX();
				double[] longitude = path.getAllY();
				List<Coordinates> coords = new ArrayList<Coordinates>();

				for (int i = 0; i < latitude.length; i++) {

					coords.add(new Coordinates(longitude[i], latitude[i], 0.0));
				}

				polygonsLayer.addItem(new Polygon("HPDRegion_" + pathCounter, // name
						coords, // List<Coordinates>
						polygonsStyle, // Style style
						sliceTime, // double startime
						0.0 // double duration
						));

				pathCounter++;

			}// END: paths loop

			layers.add(polygonsLayer);
			polygonsStyleId++;

		}// END: run
	}// END: Polygons

	// ///////////////////////////
	// ---CONCURRENT BRANCHES---//
	// ///////////////////////////
	private class Branches implements Runnable {

		public void run() {

			try {

				double treeHeightMax = Utils.getTreeHeightMax(tree);

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

						/**
						 * Mapping
						 * */
						double nodeHeight = tree.getHeight(node);

						double maxAltitude = Utils.map(nodeHeight, 0,
								treeHeightMax, 0, maxAltMapping);

						int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
								minBranchRedMapping, maxBranchRedMapping);

						int green = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minBranchGreenMapping,
								maxBranchGreenMapping);

						int blue = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minBranchBlueMapping,
								maxBranchBlueMapping);

						int alpha = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, maxBranchOpacityMapping,
								minBranchOpacityMapping);

						Color col = new Color(red, green, blue, alpha);

						Style linesStyle = new Style(col, branchWidth);
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

			} catch (ParseException e) {
				e.printStackTrace();

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Branches class

	private Object[] imputeValue(Object[] location, Object[] parentLocation,
			double sliceTime, double nodeTime, double parentTime,
			RootedTree tree, double rate, boolean trueNoise) {

		Object o = tree.getAttribute(precisionString);

		if (o == null) {
			throw new RuntimeException("Attribute " + precisionString
					+ " missing from the tree. \n");
		}

		double treeNormalization = tree.getHeight(tree.getRootNode());

		Object[] array = (Object[]) o;

		int dim = (int) Math.sqrt(1 + 8 * array.length) / 2;
		double[][] precision = new double[dim][dim];
		int c = 0;
		for (int i = 0; i < dim; i++) {
			for (int j = i; j < dim; j++) {
				precision[j][i] = precision[i][j] = ((Double) array[c++])
						* treeNormalization;
			}
		}

		dim = location.length;
		double[] nodeValue = new double[2];
		double[] parentValue = new double[2];

		for (int i = 0; i < dim; i++) {

			nodeValue[i] = Double.parseDouble(location[i].toString());
			parentValue[i] = Double.parseDouble(parentLocation[i].toString());

		}

		final double scaledTimeChild = (sliceTime - nodeTime) * rate;
		final double scaledTimeParent = (parentTime - sliceTime) * rate;
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

		Object[] result = new Object[dim];
		for (int i = 0; i < dim; i++)
			result[i] = mean[i];

		return result;
	}// END: ImputeValue

	private TimeLine GenerateTimeLine(RootedTree tree) throws ParseException {

		// This is a general time span for all of the trees
		double treeRootHeight = tree.getHeight(tree.getRootNode());
		SpreadDate mrsd = new SpreadDate(mrsdString);
		double startTime = mrsd.getTime()
				- (treeRootHeight * DayInMillis * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfIntervals);

		return timeLine;

	}// END: GenerateTimeLine

}// END: class