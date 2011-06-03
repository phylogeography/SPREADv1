package templates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import processing.core.PApplet;
import structure.Coordinates;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

@SuppressWarnings("serial")
public class TimeSlicerToProcessing extends PApplet {

	private final int DayInMillis = 86400000;

	// Concurrency stuff
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private Double sliceTime;
	private RootedTree currentTree;

	private TreeImporter treesImporter;
	private TreeImporter treeImporter;
	private String precisionString;
	private String coordinatesName;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private int numberOfIntervals;
	private boolean useTrueNoise;
	private boolean impute;
	private String mrsdString;
	private double timescaler;
	private TimeLine timeLine;
	private double startTime;
	private double endTime;
	private double burnIn;
	private RootedTree tree;
	private double HPD;

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

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private timescalerEnum timescalerSwitcher;

	private MapBackground mapBackground;
	private float minX, maxX;
	private float minY, maxY;

	public TimeSlicerToProcessing() {

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

	}// END: TimeSlicerToProcessing()

	public void setHPD(double percent) {
		HPD = percent;
	}

	public void setMccTreePath(String path) throws FileNotFoundException {
		treeImporter = new NexusImporter(new FileReader(path));
	}

	public void setTreesPath(String path) throws FileNotFoundException {
		treesImporter = new NexusImporter(new FileReader(path));
	}

	public void setMrsdString(String mrsd) {
		mrsdString = mrsd;
	}

	public void setCoordinatesName(String name) {
		coordinatesName = name;
		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);
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
		useTrueNoise = trueNoiseBoolean;
	}

	public void setImpute(boolean imputeBoolean) {
		impute = imputeBoolean;
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

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -90;
		maxY = 90;

		mapBackground = new MapBackground(this);

	}// END:setup

	public void draw() {

		noLoop();
		smooth();
		mapBackground.drawMapBackground();
		drawPolygons();
		drawBranches();

	}// END:draw

	private void drawPolygons() throws OutOfMemoryError {

		System.out.println("Generating Polygons...");
		System.out.println("Iterating through Map...");

		Set<Double> hostKeys = slicesMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();

		while (iterator.hasNext()) {
			sliceTime = (Double) iterator.next();
			drawPolygon(sliceTime);
		}
	}// END: drawPolygons

	private void drawPolygon(Double sliceTime) throws OutOfMemoryError {

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

		stroke(red, green, blue, alpha);
		fill(red, green, blue, alpha);

		List<Coordinates> list = slicesMap.get(sliceTime);

		double[] x = new double[list.size()];
		double[] y = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {

			x[i] = list.get(i).getLatitude();
			y[i] = list.get(i).getLongitude();

		}

		ContourMaker contourMaker = new ContourWithSynder(x, y, 100);
		ContourPath[] paths = contourMaker.getContourPaths(HPD);

		for (ContourPath path : paths) {

			double[] latitude = path.getAllX();
			double[] longitude = path.getAllY();

			List<Coordinates> coordinates = new ArrayList<Coordinates>();

			for (int i = 0; i < latitude.length; i++) {

				coordinates
						.add(new Coordinates(longitude[i], latitude[i], 0.0));
			}

			beginShape();

			for (int row = 0; row < coordinates.size() - 1; row++) {

				float X = map((float) coordinates.get(row).getLongitude(),
						minX, maxX, 0, width);
				float Y = map((float) coordinates.get(row).getLatitude(), maxY,
						minY, 0, height);

				float XEND = map((float) coordinates.get(row + 1)
						.getLongitude(), minX, maxX, 0, width);
				float YEND = map((float) (coordinates.get(row + 1)
						.getLatitude()), maxY, minY, 0, height);

				vertex(X, Y);
				vertex(XEND, YEND);

			}// END: coordinates loop
			endShape(CLOSE);

		}// END: paths loop

	}// END: drawPolygon()

	private void drawBranches() {

		double treeHeightMax = Utils.getTreeHeightMax(tree);
		strokeWeight((float) branchWidth);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				float longitude = (float) Utils.getDoubleNodeAttribute(node,
						longitudeName);

				float latitude = (float) Utils.getDoubleNodeAttribute(node,
						latitudeName);

				Node parentNode = tree.getParent(node);
				float parentLongitude = (float) Utils.getDoubleNodeAttribute(
						parentNode, longitudeName);
				float parentLatitude = (float) Utils.getDoubleNodeAttribute(
						parentNode, latitudeName);

				// Equirectangular projection:
				float x0 = map(parentLongitude, minX, maxX, 0, width);
				float y0 = map(parentLatitude, maxY, minY, 0, height);

				float x1 = map(longitude, minX, maxX, 0, width);
				float y1 = map(latitude, maxY, minY, 0, height);

				/**
				 * Color mapping
				 * */
				double nodeHeight = tree.getHeight(node);

				int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchRedMapping, maxBranchRedMapping);

				int green = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchBlueMapping, maxBranchBlueMapping);

				int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);
				line(x0, y0, x1, y1);
			}
		}// END: nodes loop
	}// END: DrawBranches

	public void AnalyzeTrees() throws IOException, ImportException,
			ParseException {

		// This is a general time span for all of the trees
		tree = (RootedTree) treeImporter.importNextTree();
		timeLine = GenerateTimeLine(tree);
		startTime = timeLine.getStartTime();
		endTime = timeLine.getEndTime();

		// This is for slice times
		slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

		System.out.println("Analyzing trees...");

		// Executor for threads
		int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		int readTrees = 0;
		while (treesImporter.hasTree()) {

			currentTree = (RootedTree) treesImporter.importNextTree();

			if (readTrees >= burnIn) {

				// executor.submit(new AnalyzeTree());
				new AnalyzeTree().run();

				if (readTrees % burnIn == 0) {
					System.out.print(readTrees + " trees... ");
				}
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

	}// END: AnalyzeTrees

	// ///////////////////////////////
	// ---CONCURRENT ANALYZE TREE---//
	// ///////////////////////////////
	private class AnalyzeTree implements Runnable {

		public void run() {

			try {

				// attributes parsed once per tree
				double treeRootHeight = tree.getHeight(tree.getRootNode());
				double treeNormalization = 0;
				double[] precisionArray = null;
				if (impute) {
					treeNormalization = currentTree.getHeight(currentTree
							.getRootNode());
					precisionArray = Utils.getTreeDoubleArrayAttribute(
							currentTree, precisionString);
				}

				for (Node node : currentTree.getNodes()) {
					if (!currentTree.isRoot(node)) {

						// attributes parsed once per node
						Node parentNode = currentTree.getParent(node);

						double nodeHeight = currentTree.getHeight(node);
						double parentHeight = currentTree.getHeight(parentNode);

						double[] location = Utils.getDoubleArrayNodeAttribute(
								node, coordinatesName);

						double[] parentLocation = Utils
								.getDoubleArrayNodeAttribute(parentNode,
										coordinatesName);

						double rate = 0;
						if (impute) {
							rate = Utils.getDoubleNodeAttribute(node,
									rateString);
						}

						for (int i = 0; i <= numberOfIntervals; i++) {

							double sliceHeight = treeRootHeight
									- (treeRootHeight / numberOfIntervals)
									* ((double) i);

							if (nodeHeight < sliceHeight
									&& sliceHeight <= parentHeight) {

								double sliceTime = new SpreadDate(mrsdString)
										.minus((int) (sliceHeight * timescaler));

								// grow map entry if key exists
								if (slicesMap.containsKey(sliceTime)) {

									if (impute) {

										double[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceHeight, nodeHeight,
												parentHeight, rate,
												useTrueNoise,
												treeNormalization,
												precisionArray);

										slicesMap
												.get(sliceTime)
												.add(
														new Coordinates(
																imputedLocation[1],
																imputedLocation[0],
																0.0));
									}

									// start new entry if no such key in the map
								} else {

									List<Coordinates> coords = new ArrayList<Coordinates>();

									if (impute) {

										double[] imputedLocation = imputeValue(
												location, parentLocation,
												sliceHeight, nodeHeight,
												parentHeight, rate,
												useTrueNoise,
												treeNormalization,
												precisionArray);

										coords.add(new Coordinates(
												imputedLocation[1],
												imputedLocation[0], 0.0));
									}

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

	private double[] imputeValue(double[] location, double[] parentLocation,
			double sliceTime, double nodeTime, double parentTime, double rate,
			boolean trueNoise, double treeNormalization, double[] precisionArray) {

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

		double[] result = new double[dim];
		for (int i = 0; i < dim; i++)
			result[i] = mean[i];

		return result;
	}// END: ImputeValue

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

}// END: TimeScalerToProcessing class
