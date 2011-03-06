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
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;

public class TimeSlicerToKML {

	public long time;
	public String message;

	private static final int DayInMillis = 86400000;

	private TreeImporter treesImporter;
	private TreeImporter treeImporter;
	private String precisionString;
	private String locationString;
	private String rateString;
	private int numberOfIntervals;
	private boolean trueNoise;
	private String mrsdString;
	private double timescaler;
	private TimeLine timeLine;
	private HashMap<Double, List<Coordinates>> sliceMap;
	private double startTime;
	private double endTime;
	private List<Layer> layers;
	private PrintWriter writer;
	private double burnIn;
	private RootedTree currentTree;
	private SimpleDateFormat formatter;
	private Double sliceTime;
	private int polygonsStyleId;

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

	public void setMccTreePath(String path) throws FileNotFoundException {
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

	public void setBurnIn(double burnInDouble) {
		burnIn = burnInDouble;
	}

	public void setTrueNoise(boolean trueNoiseBoolean) {
		trueNoise = trueNoiseBoolean;
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
	}

	public void GenerateKML() throws IOException, ImportException,
			ParseException {

		// start timing
		time = -System.currentTimeMillis();

		message = "Importing trees...";
		System.out.println(message);

		// This is a general time span for all of the trees
		RootedTree tree = (RootedTree) treeImporter.importNextTree();
		timeLine = GenerateTimeLine(tree);
		startTime = timeLine.getStartTime();
		endTime = timeLine.getEndTime();

		// This is for slice times
		sliceMap = new HashMap<Double, List<Coordinates>>();

		message = "Analyzing trees...";
		System.out.println(message);

		// Executor for threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		int readTrees = 0;
		while (treesImporter.hasTree()) {

			currentTree = (RootedTree) treesImporter.importNextTree();

			if (readTrees >= burnIn) {
//				AnalyzeTree(currentTree);
				executor.submit(new AnalyzeTree());
			}

			readTrees++;
		}

		// Wait until all threads are finished
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		message = "Analyzed " + (int) (readTrees - burnIn) + " trees";
		System.out.println(message);

		// this is to generate kml output
		layers = new ArrayList<Layer>();

		message = "Generating Polygons...";
		System.out.println(message);

		// Utils.printHashMap(sliceMap, true);

		message = "Iterating through Map...";
		System.out.println(message);
		formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
		Set<Double> hostKeys = sliceMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();
		executor = Executors.newFixedThreadPool(NTHREDS);

		polygonsStyleId = 1;
		synchronized (iterator) {
			while (iterator.hasNext()) {
				sliceTime = (Double) iterator.next();
				// executor.submit(new Polygons());
				Polygons(sliceTime);
			}
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		message = "Writing to kml...";
		System.out.println(message);

		KMLGenerator kmloutput = new KMLGenerator();
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();
		System.out.println("Finished in: " + time + " msec");

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
	}

	// ////////////////////
	// ---ANALYZE TREE---//
	// ////////////////////
	private void AnalyzeTree(RootedTree currentTree) throws ParseException {

		double timeSpan = startTime - endTime;

		for (Node node : currentTree.getNodes()) {

			if (!currentTree.isRoot(node)) {

				for (int i = numberOfIntervals; i > 0; i--) {

					Node parentNode = currentTree.getParent(node);

					double nodeHeight = currentTree.getHeight(node);
					double parentHeight = currentTree.getHeight(parentNode);

					Object[] location = (Object[]) Utils.getArrayNodeAttribute(
							node, locationString);
					double latitude = (Double) location[0];
					double longitude = (Double) location[1];

					Object[] parentLocation = (Object[]) Utils
							.getArrayNodeAttribute(parentNode, locationString);
					double parentLatitude = (Double) parentLocation[0];
					double parentLongitude = (Double) parentLocation[1];

					double rate = Utils
							.getDoubleNodeAttribute(node, rateString);

					double sliceTime = startTime
							- (timeSpan / numberOfIntervals) * ((double) i);

					SpreadDate mrsd0 = new SpreadDate(mrsdString);
					double parentTime = mrsd0
							.minus((int) (parentHeight * timescaler));

					SpreadDate mrsd1 = new SpreadDate(mrsdString);
					double nodeTime = mrsd1
							.minus((int) (nodeHeight * timescaler));

					Object[] imputedLocation = imputeValue(location,
							parentLocation, sliceTime, nodeTime, parentTime,
							currentTree, rate, trueNoise);

					if (parentTime < sliceTime && sliceTime <= nodeTime) {

						if (sliceMap.containsKey(sliceTime)) {

							sliceMap.get(sliceTime).add(
									new Coordinates(parentLongitude,
											parentLatitude, 0.0));

							sliceMap.get(sliceTime).add(new Coordinates(

							Double.valueOf(imputedLocation[1].toString()),

							Double.valueOf(imputedLocation[0].toString()),

							0.0));

							sliceMap.get(sliceTime).add(
									new Coordinates(longitude, latitude, 0.0));

						} else {

							List<Coordinates> coords = new ArrayList<Coordinates>();

							coords.add(new Coordinates(parentLongitude,
									parentLatitude, 0.0));

							coords.add(new Coordinates(Double
									.valueOf(imputedLocation[1].toString()),
									Double.valueOf(imputedLocation[0]
											.toString()), 0.0));

							coords
									.add(new Coordinates(longitude, latitude,
											0.0));

							sliceMap.put(sliceTime, coords);

						}// END: key check
					}
				}// END: numberOfIntervals loop
			}
		}// END: node loop
	}

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

							Object[] imputedLocation = imputeValue(location,
									parentLocation, sliceTime, nodeTime,
									parentTime, currentTree, rate, trueNoise);

							if (parentTime < sliceTime && sliceTime <= nodeTime) {

								if (sliceMap.containsKey(sliceTime)) {

									sliceMap.get(sliceTime).add(
											new Coordinates(parentLongitude,
													parentLatitude, 0.0));

									sliceMap.get(sliceTime).add(
											new Coordinates(

											Double.valueOf(imputedLocation[1]
													.toString()),

											Double.valueOf(imputedLocation[0]
													.toString()),

											0.0));

									sliceMap.get(sliceTime).add(
											new Coordinates(longitude,
													latitude, 0.0));

								} else {

									List<Coordinates> coords = new ArrayList<Coordinates>();

									coords.add(new Coordinates(parentLongitude,
											parentLatitude, 0.0));

									coords.add(new Coordinates(Double
											.valueOf(imputedLocation[1]
													.toString()), Double
											.valueOf(imputedLocation[0]
													.toString()), 0.0));

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
			}

		}// END: run
	}// END: analyzeTree

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

				if (list.get(i) != null) {// TODO NullPointerException

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

	// ////////////////
	// ---POLYGONS---//
	// ////////////////
	private void Polygons(Double sliceTime) {

		Layer polygonsLayer = new Layer("Time_Slice_"
				+ formatter.format(sliceTime), null);

		/**
		 * Color and Opacity mapping
		 * */
		int red = 55;
		int green = (int) Utils.map(sliceTime, startTime, endTime, 255, 0);
		int blue = 0;
		int alpha = (int) Utils.map(sliceTime, startTime, endTime, 100, 255);

		Color col = new Color(red, green, blue, alpha);
		Style polygonsStyle = new Style(col, 0);
		polygonsStyle.setId("polygon_style" + polygonsStyleId);

		List<Coordinates> list = sliceMap.get(sliceTime);

		double[] x = new double[list.size()];
		double[] y = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i) != null) {// TODO NullPointerException

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
	}// END: Polygons

}// END: TimeSlicer class
