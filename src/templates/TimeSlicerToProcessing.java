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
import processing.core.PApplet;
import structure.Coordinates;
import structure.TimeLine;
import utils.ReadSliceHeights;
import utils.ThreadLocalSpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

@SuppressWarnings("serial")
public class TimeSlicerToProcessing extends PApplet {

	private int analysisType;
	public final static int FIRST_ANALYSIS = 1;
	public final static int SECOND_ANALYSIS = 2;

	// how many millisecond one day holds
	private final int DayInMillis = 86400000;
	// how many days one year holds
	private static final int DaysInYear = 365;

	// Concurrency stuff
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private RootedTree currentTree;

	private RootedTree tree;
	private int numberOfIntervals;
	private double[] sliceHeights;

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
	private TreeImporter treeImporter;
	private double treeRootHeight;
	private String precisionString;
	private String coordinatesName;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private boolean useTrueNoise;
	private String mrsdString;
	private ThreadLocalSpreadDate mrsd;
	private double timescaler;
	private TimeLine timeLine;
	private double startTime;
	private double endTime;
	private double burnIn;
	private double hpd;
	private int gridSize;

	private MapBackground mapBackground;
	private float minX, maxX;
	private float minY, maxY;

	public TimeSlicerToProcessing() {
	}// END: constructor

	public void setAnalysisType(int analysisType) {
		this.analysisType = analysisType;
	}

	public void setCustomSliceHeightsPath(String path) {
		sliceHeights = new ReadSliceHeights(path).getSliceHeights();
	}

	public void setTimescaler(double timescaler) {
		this.timescaler = timescaler;
	}

	public void setHPD(double hpd) {
		this.hpd = hpd;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
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

	public void setCoordinatesName(String name) {
		coordinatesName = name;
		// this is for coordinate attribute names
		latitudeName = (coordinatesName + 1);
		longitudeName = (coordinatesName + 2);
	}

	public void setRateAttributeName(String name) {
		rateString = name;
	}

	public void setPrecisionAttributeName(String name) {
		precisionString = name;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setBurnIn(double burnIn) {
		this.burnIn = burnIn;
	}

	public void setUseTrueNoise(boolean useTrueNoise) {
		this.useTrueNoise = useTrueNoise;
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
			System.out.println("Drawing polygons...");
			drawPolygons();

		switch (analysisType) {
		case 1:
			System.out.println("Drawing branches...");
			drawBranches();
			break;
		case 2:
			break;
		}

	}// END:draw

	private void drawPolygons() throws OutOfMemoryError {

		System.out.println("Iterating through Map...");

		Set<Double> hostKeys = slicesMap.keySet();
		Iterator<Double> iterator = hostKeys.iterator();

		int polygonsStyleId = 1;
		while (iterator.hasNext()) {

			System.out.println("Key " + polygonsStyleId + "...");

			Double sliceTime = iterator.next();
			drawPolygon(sliceTime);

			polygonsStyleId++;
			// slicesMap.remove(sliceTime);
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

		ContourMaker contourMaker = new ContourWithSynder(x, y, gridSize);
		ContourPath[] paths = contourMaker.getContourPaths(hpd);

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

				double X = Utils.map(coordinates.get(row).getLongitude(), minX,
						maxX, 0, width);
				double Y = Utils.map(coordinates.get(row).getLatitude(), maxY,
						minY, 0, height);

				double XEND = Utils.map(
						coordinates.get(row + 1).getLongitude(), minX, maxX, 0,
						width);
				double YEND = Utils.map(
						(coordinates.get(row + 1).getLatitude()), maxY, minY,
						0, height);

				vertex((float) X, (float) Y);
				vertex((float) XEND, (float) YEND);

			}// END: coordinates loop
			endShape(CLOSE);

		}// END: paths loop

		slicesMap.remove(sliceTime);

	}// END: drawPolygon()

	private void drawBranches() {

		double treeHeightMax = Utils.getTreeHeightMax(tree);
		strokeWeight((float) branchWidth);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				Double longitude = (Double) node.getAttribute(longitudeName);
				Double latitude = (Double) node.getAttribute(latitudeName);

				Node parentNode = tree.getParent(node);

				Double parentLongitude = (Double) parentNode
						.getAttribute(longitudeName);
				Double parentLatitude = (Double) parentNode
						.getAttribute(latitudeName);

				// Equirectangular projection:
				double x0 = Utils.map(parentLongitude, minX, maxX, 0, width);
				double y0 = Utils.map(parentLatitude, maxY, minY, 0, height);

				double x1 = Utils.map(longitude, minX, maxX, 0, width);
				double y1 = Utils.map(latitude, maxY, minY, 0, height);

				/**
				 * Color mapping
				 * */
				double nodeHeight = Utils.getNodeHeight(tree, node);

				int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchRedMapping, maxBranchRedMapping);

				int green = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						minBranchBlueMapping, maxBranchBlueMapping);

				int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);
				line((float) x0, (float) y0, (float) x1, (float) y1);
			}
		}// END: nodes loop
	}// END: DrawBranches

	public void analyzeTrees() throws IOException, ImportException,
			ParseException {

		mrsd = new ThreadLocalSpreadDate(mrsdString);

		switch (analysisType) {
		case 1:
			tree = (RootedTree) treeImporter.importNextTree();
			treeRootHeight = Utils.getNodeHeight(tree, tree.getRootNode());
			sliceHeights = generateTreeSliceHeights(treeRootHeight,
					numberOfIntervals);
			timeLine = generateTreeTimeLine(tree);
			break;
		case 2:
			timeLine = generateCustomTimeLine(sliceHeights);
			break;
		}

		System.out.println("Using as slice times: ");
		Utils.printArray(sliceHeights);
		System.out.println();
		
			startTime = timeLine.getStartTime();
			endTime = timeLine.getEndTime();

			// This is for slice times
			slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

			// Executor for threads
			int NTHREDS = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(NTHREDS * 2);

			int treesAssumed = 10000;
			int treesRead = 0;
			
			System.out.println("Analyzing trees (bar assumes 10,000 trees)");
			System.out.println("0                   25                  50                  75                 100");
			System.out.println("|---------------------|---------------------|---------------------|---------------------|");
//			System.out.println("0              25             50             75            100");
//			System.out.println("|--------------|--------------|--------------|--------------|");
			
			int stepSize = treesAssumed / 60;
			if (stepSize < 1) {
				stepSize = 1;
			}

			// This is for collecting coordinates into polygons
			slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

			int totalTrees = 0;
			while (treesImporter.hasTree()) {

				currentTree = (RootedTree) treesImporter.importNextTree();

				if (totalTrees >= burnIn) {

					executor.submit(new AnalyzeTree(currentTree, //
							precisionString,//
							coordinatesName, //
							rateString, //
							sliceHeights, //
							timescaler,//
							mrsd, //
							slicesMap, //
							useTrueNoise//
							));

					// new AnalyzeTree(currentTree,
					// precisionString, coordinatesName, rateString,
					// sliceHeights, timescaler, mrsd, slicesMap,
					// useTrueNoise).run();

					treesRead += 1;

				}// END: if burn-in

				if (totalTrees > 0 && totalTrees % stepSize == 0) {
					System.out.print("*");
					System.out.flush();
				}

				totalTrees++;

			}// END: while has trees

			if ((totalTrees - burnIn) <= 0.0) {
				throw new RuntimeException("Burnt too many trees!");
			} else {
				System.out.println("\nAnalyzed " + treesRead
						+ " trees with burn-in of " + burnIn + " for the total of "
						+ totalTrees + " trees");
			}

			// Wait until all threads are finished
			executor.shutdown();
			while (!executor.isTerminated()) {
			}

	}// END: AnalyzeTrees

	private TimeLine generateTreeTimeLine(RootedTree tree) {

		// This is a general time span for all of the trees
		double treeRootHeight = Utils.getNodeHeight(tree, tree.getRootNode());
		double startTime = mrsd.getTime()
				- (treeRootHeight * DayInMillis * DaysInYear * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfIntervals);

		return timeLine;
	}// END: generateTreeTimeLine

	private double[] generateTreeSliceHeights(double treeRootHeight,
			int numberOfIntervals) {

		double[] timeSlices = new double[numberOfIntervals];

		for (int i = 0; i < numberOfIntervals; i++) {

			timeSlices[i] = treeRootHeight
					- (treeRootHeight / (double) numberOfIntervals)
					* ((double) i);
		}

		return timeSlices;
	}// END: generateTimeSlices

	private TimeLine generateCustomTimeLine(double[] timeSlices) {

		// This is a general time span for all of the trees
		int numberOfSlices = timeSlices.length;
		double firstSlice = timeSlices[0];
		
		double startTime = mrsd.getTime() - (firstSlice * DayInMillis * DaysInYear * timescaler);
		double endTime = mrsd.getTime();

		return new TimeLine(startTime, endTime, numberOfSlices);
	}// END: generateCustomTimeLine

}// END: TimeScalerToProcessing class
