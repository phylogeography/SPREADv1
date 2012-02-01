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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
import utils.ReadSliceHeights;
import utils.ThreadLocalSpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

public class TimeSlicerToKML {

	private int analysisType;
	public final static int FIRST_ANALYSIS = 1;
	public final static int SECOND_ANALYSIS = 2;

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// how many days one year holds
	private static final int DaysInYear = 365;

	// Concurrency stuff
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private RootedTree currentTree;

	private TreeImporter treeImporter;

	private RootedTree tree;
	private int numberOfIntervals;
	private double[] sliceHeights;

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

	private ThreadLocalSpreadDate mrsd;
	private double timescaler;
	private double treeRootHeight;
	private double branchWidth;
	private TreeImporter treesImporter;
	private String mrsdString;
	private int burnIn;
	private boolean impute;
	private boolean useTrueNoise;
	private String coordinatesName;
	private String longitudeName;
	private String latitudeName;
	private String rateString;
	private String precisionString;
	private List<Layer> layers;
	private SimpleDateFormat formatter;
	private String kmlPath;
	private TimeLine timeLine;
	private double startTime;
	private double endTime;
	private double HPD;
	private int gridSize;

	public TimeSlicerToKML() {
	}

	public void setAnalysisType(int analysisType) {
		this.analysisType = analysisType;
	}

	public void setCustomSliceHeights(String path) {
		sliceHeights = new ReadSliceHeights(path).getSliceHeights();
	}

	public void setTimescaler(double timescaler) {
		this.timescaler = timescaler;
	}

	public void setHPD(double percent) {
		HPD = percent;
	}

	public void setGridSize(int size) {
		gridSize = size;
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
		coordinatesName = name;
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);
	}

	public void setRateAttName(String name) {
		rateString = name;
	}

	public void setPrecisionAttName(String name) {
		precisionString = name;
	}

	public void setKmlWriterPath(String path) throws FileNotFoundException {
		kmlPath = path;
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
		
		// this is to generate kml output
		layers = new ArrayList<Layer>();

		// Executor for threads
		int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS * 2);

		if (impute) {

			System.out.println("Analyzing trees...");

			// This is for collecting coordinates into polygons
			slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

			int readTrees = 1;
			while (treesImporter.hasTree()) {

				currentTree = (RootedTree) treesImporter.importNextTree();

				if (readTrees >= burnIn) {

					executor.submit(new AnalyzeTree(currentTree,
							precisionString, coordinatesName, rateString,
							sliceHeights, timescaler, mrsd, slicesMap,
							useTrueNoise));

					// new AnalyzeTree(currentTree,
					// precisionString, coordinatesName, rateString,
					// sliceHeights, timescaler, mrsd, slicesMap,
					// useTrueNoise).run();

					if (readTrees % 500 == 0) {
						System.out.print(readTrees + " trees... ");
					}
				}// END: if burn-in

				readTrees++;

			}// END: while has trees

			if ((readTrees - burnIn) <= 0.0) {
				throw new RuntimeException("Burnt too many trees!");
			} else {
				System.out.println("Analyzed " + (int) (readTrees - burnIn - 1)
						+ " trees with burn-in of " + burnIn);
			}

			// Wait until all threads are finished
			executor.shutdown();
			while (!executor.isTerminated()) {
			}

			System.out.println("Generating polygons...");

			Iterator<Double> iterator = slicesMap.keySet().iterator();
			executor = Executors.newFixedThreadPool(NTHREDS);
			formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
			startTime = timeLine.getStartTime();
			endTime = timeLine.getEndTime();

			System.out.println("Iterating through Map...");

			int polygonsStyleId = 1;
			while (iterator.hasNext()) {

				System.out.println("Key " + polygonsStyleId + "...");

				Double sliceTime = iterator.next();

				// executor.submit(new Polygons(sliceTime, polygonsStyleId));
				new Polygons(sliceTime, polygonsStyleId).run();

				polygonsStyleId++;
			}// END: while has next

		}// END: if impute

		switch (analysisType) {
		case 1:
			System.out.println("Generating branches...");
			executor.submit(new Branches());
			break;
		case 2:
			break;
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		System.out.println("Writing to kml...");

		PrintWriter writer = new PrintWriter(kmlPath);
		KMLGenerator kmloutput = new KMLGenerator();
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();

	}// END: GenerateKML

	// ///////////////////////////
	// ---CONCURRENT POLYGONS---//
	// ///////////////////////////
	
	public class Polygons implements Runnable {

		private double sliceTime;
		private int polygonsStyleId;

		public Polygons(Double sliceTime, int polygonsStyleId) {
			this.sliceTime = sliceTime;
			this.polygonsStyleId = polygonsStyleId;
		}

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

			Color color = new Color(red, green, blue, alpha);
			Style polygonsStyle = new Style(color, 0);
			polygonsStyle.setId("polygon_style" + polygonsStyleId);

			List<Coordinates> list = slicesMap.get(sliceTime);

			double[] x = new double[list.size()];
			double[] y = new double[list.size()];

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i) == null) {
					System.out.println("null found");
				}

				x[i] = list.get(i).getLatitude();
				y[i] = list.get(i).getLongitude();

			}

			ContourMaker contourMaker = new ContourWithSynder(x, y, gridSize);
			ContourPath[] paths = contourMaker.getContourPaths(HPD);

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
			slicesMap.remove(sliceTime);

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
							 * Mapping
							 * */
							double maxAltitude = Utils.map(nodeHeight, 0,
									treeHeightMax, 0, maxAltMapping);

							int red = (int) Utils.map(nodeHeight, 0,
									treeHeightMax, minBranchRedMapping,
									maxBranchRedMapping);

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
		double startTime = mrsd.getTime()
				- (timeSlices[numberOfSlices - 1] * DayInMillis * DaysInYear * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfSlices);

		return timeLine;
	}// END: generateCustomTimeLine

	public int getAnalysisType() {
		return analysisType;
	}

}// END: class