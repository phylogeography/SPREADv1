package templates;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import math.MultivariateNormalDistribution;
import processing.core.PApplet;
import processing.core.PImage;
import structure.Coordinates;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

@SuppressWarnings("serial")
public class TimeSlicerToProcessing extends PApplet {

	private final int imageWidth = 2048;
	private final int imageHeight = 1025;
	private final int DayInMillis = 86400000;
	private final int NTHREDS;

	private TreeImporter treesImporter;
	private TreeImporter treeImporter;
	private String precisionString;
	private String locationString;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private int numberOfIntervals;
	private boolean trueNoise;
	private String mrsdString;
	private double timescaler;
	private TimeLine timeLine;
	private HashMap<Double, List<Coordinates>> sliceMap;
	private double startTime;
	private double endTime;
	private double burnIn;
	private RootedTree currentTree;
	private Double sliceTime;
	RootedTree tree;
	
	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private timescalerEnum timescalerSwitcher;

	private PImage mapImage;
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

		NTHREDS = Runtime.getRuntime().availableProcessors();

	}// END: TimeSlicerToProcessing()

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

	public void setBurnIn(double burnInDouble) {
		burnIn = burnInDouble;
	}

	public void setTrueNoise(boolean trueNoiseBoolean) {
		trueNoise = trueNoiseBoolean;
	}

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -80;
		maxY = 90;

		width = imageWidth;
		height = imageHeight;

		size(width, height);
		try {
			AnalyzeTrees();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// END:setup

	public void draw() {

		smooth();
		drawMapBackground();
		drawPolygons();
		drawBranches();
		
	}// END:draw

	private void drawMapBackground() {

		mapImage = loadImage(LoadMapBackgroundInEclipse());
		// mapImage = loadImage(LoadMapBackgroundFromJar());
		image(mapImage, 0, 0, width, height);

	}// END: drawMapBackground

	private void drawPolygons() {

		Set<Double> hostKeys = sliceMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();

		while (iterator.hasNext()) {
			sliceTime = (Double) iterator.next();
			drawPolygon(sliceTime);
		}
	}

	private void drawPolygon(Double sliceTime) {

		/**
		 * Color and Opacity mapping
		 * */
		int red = 55;
		int green = (int) Utils.map(sliceTime, startTime, endTime, 255, 0);
		int blue = 0;
		int alpha = (int) Utils.map(sliceTime, startTime, endTime, 100, 255);

		stroke(red, green, blue, alpha);
		fill(red, green, blue, alpha);

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

			pathCounter++;

		}// END: paths loop

	}// END: drawPolygons()
	
	private void drawBranches() {

		double treeHeightMax = Utils.getTreeHeightMax(tree);
		strokeWeight(2);

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

				int red = 255;
				int green = 0;
				int blue = (int) Utils
						.map(nodeHeight, 0, treeHeightMax, 255, 0);
				int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax, 100,
						255);

				stroke(red, green, blue, alpha);
				line(x0, y0, x1, y1);
			}
		}// END: nodes loop
	}// END: DrawBranches

	private void AnalyzeTrees() throws IOException, ImportException,
			ParseException {

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

		System.out.println("Analyzed " + (int) (readTrees - burnIn) + " trees.");

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

	@SuppressWarnings("all")
	private String LoadMapBackgroundFromJar() {
		String imgPathFromJar = "jar:"
				+ this.getClass().getResource("world_map.png").getPath();
		return imgPathFromJar;
	}

	@SuppressWarnings("all")
	private String LoadMapBackgroundInEclipse() {
		String imgPathFromJar = this.getClass().getResource("world_map.png")
				.getPath();
		return imgPathFromJar;
	}

}// END: TimeScalerToProcessing class
