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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

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

public class TimeSlicerToKML {

	public long time;

	private static final int DayInMillis = 86400000;

	private TreeImporter treesImporter;
	private TreeImporter treeImporter;
	private String precisionString;
	private String locationString;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private int numberOfIntervals;
	private boolean trueNoise;
	private boolean impute;
	private String mrsdString;
	private double timescaler;
	private TimeLine timeLine;
	private HashMap<Double, List<Coordinates>> sliceMap;
	private double startTime;
	private double endTime;
	private List<Layer> layers;
	private PrintWriter writer;
	private int burnIn;
	private RootedTree currentTree;
	private SimpleDateFormat formatter;
	private Double sliceTime;
	private int polygonsStyleId;
	private RootedTree tree;
	private double maxAltMapping;

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

	}// END: TimeSlicerToKML()

	public void setTreePath(String path) throws FileNotFoundException {
		treeImporter = new NexusImporter(new FileReader(path));
	}

	public void setTreesPath(String path) throws FileNotFoundException {
		treesImporter = new NexusImporter(new FileReader(path));
	}

	public void setMrsdString(String mrsd) {
		mrsdString = mrsd;
	}

	public void setLocationAttName(String name) {
		locationString = name;
		// this is for coordinate attribute names
		longitudeName = (locationString + 2);
		latitudeName = (locationString + 1);
	}

	public void setRateAttName(String name) {
		rateString = name;
	}

	public void setPrecisionAttName(String name) {
		precisionString = name;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setMaxAltitudeMapping(double max) {
		maxAltMapping = max;
	}

	public void setBurnIn(int burnInDouble) {
		burnIn = burnInDouble;
	}

	public void setTrueNoise(boolean trueNoiseBoolean) {
		trueNoise = trueNoiseBoolean;
	}

	public void setImpute(boolean imputeBoolean) {
		impute = imputeBoolean;
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
	}

	public void GenerateKML() throws IOException, ImportException,
			ParseException, RuntimeException {

		// start timing
		time = -System.currentTimeMillis();

		System.out.println("Importing trees...");

		// This is a general time span for all of the trees
		tree = (RootedTree) treeImporter.importNextTree();
		timeLine = GenerateTimeLine(tree);
		startTime = timeLine.getStartTime();
		endTime = timeLine.getEndTime();

		// This is for slice times
		sliceMap = new HashMap<Double, List<Coordinates>>();

		System.out.println("Analyzing trees...");

		// Executor for threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		int readTrees = 0;
		while (treesImporter.hasTree()) {

			currentTree = (RootedTree) treesImporter.importNextTree();

			if (readTrees >= burnIn) {
				executor.submit(new AnalyzeTree());
			}

			readTrees++;
		}

		// Wait until all threads are finished
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		if ((readTrees - burnIn) <= 0.0) {
			throw new RuntimeException("Burnt too many trees!");
		} else {
			System.out.println("Analyzed " + (int) (readTrees - burnIn)
					+ " trees");
		}

		// this is to generate kml output
		layers = new ArrayList<Layer>();

		System.out.println("Generating Polygons...");

		System.out.println("Iterating through Map...");

		formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
		Set<Double> hostKeys = sliceMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();
		executor = Executors.newFixedThreadPool(NTHREDS);

		polygonsStyleId = 1;
		synchronized (iterator) {
			while (iterator.hasNext()) {
				sliceTime = (Double) iterator.next();
				// TODO sync it with sliceTime to use concurency
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

	private TimeLine GenerateTimeLine(RootedTree mccTree) throws ParseException {

		// This is a general time span for all of the trees
		double treeRootHeight = mccTree.getHeight(mccTree.getRootNode());
		SpreadDate mrsd = new SpreadDate(mrsdString);
		double startTime = mrsd.getTime()
				- (treeRootHeight * DayInMillis * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfIntervals);

		return timeLine;

	}// END: GenerateTimeLine

	// ///////////////////////////////
	// ---CONCURRENT ANALYZE TREE---//
	// ///////////////////////////////
	private class AnalyzeTree implements Runnable {

		public void run() {

			try {

				double timeSpan = startTime - endTime;

				for (Node node : currentTree.getNodes()) {

					if (!currentTree.isRoot(node)) {

						for (int i = numberOfIntervals; i > 0; i--) {

							Node parentNode = currentTree.getParent(node);

							double nodeHeight = currentTree.getHeight(node);
							// TODO: throws NullPointerException
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

							double rate = Utils.getDoubleNodeAttribute(node,
									rateString);

							double sliceTime = startTime
									- (timeSpan / numberOfIntervals)
									* ((double) i);

							SpreadDate mrsd0 = new SpreadDate(mrsdString);
							double parentTime = mrsd0
									.minus((int) (parentHeight * timescaler));

							SpreadDate mrsd1 = new SpreadDate(mrsdString);
							double nodeTime = mrsd1
									.minus((int) (nodeHeight * timescaler));

							if (parentTime < sliceTime && sliceTime <= nodeTime) {

								// if there is an entry grow it:
								if (sliceMap.containsKey(sliceTime)) {

									sliceMap.get(sliceTime).add(
											new Coordinates(parentLongitude,
													parentLatitude, 0.0));

									if (impute) {

										Object[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceTime, nodeTime,
												parentTime, currentTree, rate,
												trueNoise);

										sliceMap
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

									sliceMap.get(sliceTime).add(
											new Coordinates(longitude,
													latitude, 0.0));

								} else { // if no entry to grow add it:

									List<Coordinates> coords = new ArrayList<Coordinates>();

									coords.add(new Coordinates(parentLongitude,
											parentLatitude, 0.0));

									if (impute) {

										Object[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceTime, nodeTime,
												parentTime, currentTree, rate,
												trueNoise);

										coords.add(new Coordinates(Double
												.valueOf(imputedLocation[1]
														.toString()), Double
												.valueOf(imputedLocation[0]
														.toString()), 0.0));

									}

									coords.add(new Coordinates(longitude,
											latitude, 0.0));

									sliceMap.put(sliceTime, coords);

								}// END: key check
							}
						}// END: numberOfIntervals loop
					}
				}// END: node loop

			} catch (ParseException e) {
				e.printStackTrace();

			} catch (RuntimeException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: AnalyzeTree

	private Object[] imputeValue(Object[] location, Object[] parentLocation,
			double sliceTime, double nodeTime, double parentTime,
			RootedTree tree, double rate, boolean trueNoise) {

		Object o = tree.getAttribute(precisionString);
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
		final double scaledWeightTotal = 1.0 / scaledTimeChild + 1.0
				/ scaledTimeParent;

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

	// ///////////////////////////
	// ---CONCURRENT POLYGONS---//
	// ///////////////////////////
	private class Polygons implements Runnable {

		public void run() {

			Layer polygonsLayer = new Layer("Time_Slice_"
					+ formatter.format(sliceTime), null);

			/**
			 * Color and Opacity mapping
			 * */
			int red = 55;
			int green = (int) Utils.map(sliceTime, startTime, endTime, 255, 0);
			int blue = 0;
			int alpha = (int) Utils
					.map(sliceTime, startTime, endTime, 100, 255);

			Color col = new Color(red, green, blue, alpha);
			Style polygonsStyle = new Style(col, 0);
			polygonsStyle.setId("polygon_style" + polygonsStyleId);

			List<Coordinates> list = sliceMap.get(sliceTime);

			double[] x = new double[list.size()];
			double[] y = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i) != null) {// TODO NullPointerException thrown

					x[i] = list.get(i).getLatitude();
					y[i] = list.get(i).getLongitude();

				}
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

				// This is for mappings
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

						int red = 255;
						int green = 0;
						int blue = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, 255, 0);
						int alpha = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, 100, 255);

						Color col = new Color(red, green, blue, alpha);

						double width = Utils.map(nodeHeight, 0, treeHeightMax,
								3.5, 10.0);

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

			} catch (ParseException e) {
				e.printStackTrace();

			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Branches class

}// END: TimeSlicer class
