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

import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import structure.Coordinates;
import structure.Layer;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.ReadTimeSlices;
import utils.ThreadLocalSpreadDate;
import utils.Utils;
import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

public class TimeSlicerToKMLCustomTimeSlices {

	public long time;

	// how many millisecond one day holds
	private static final int DayInMillis = 86400000;
	// how many days one year holds
	private static final int DaysInYear = 365;

	// Concurrency stuff
	private ConcurrentMap<Double, List<Coordinates>> slicesMap;
	private RootedTree currentTree;

	private double[] timeSlices;

	private double minPolygonRedMapping;
	private double minPolygonGreenMapping;
	private double minPolygonBlueMapping;
	private double minPolygonOpacityMapping;

	private double maxPolygonRedMapping;
	private double maxPolygonGreenMapping;
	private double maxPolygonBlueMapping;
	private double maxPolygonOpacityMapping;

	private ThreadLocalSpreadDate mrsd;
	private double timescaler;
	private TreeImporter treesImporter;
	private String mrsdString;
	private int burnIn;
	private boolean impute;
	private boolean useTrueNoise;
	private String coordinatesName;
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

	public TimeSlicerToKMLCustomTimeSlices() {
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

	public void setTimeSlices(String path) {
		timeSlices = new ReadTimeSlices(path).getTimeSlices();
	}

	public void setTreesPath(String path) throws FileNotFoundException {
		treesImporter = new NexusImporter(new FileReader(path));
	}

	public void setMrsdString(String mrsd) {
		mrsdString = mrsd;
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

	public void GenerateKML() throws IOException, ImportException,
			ParseException, RuntimeException, OutOfMemoryError {

		// start timing
		time = -System.currentTimeMillis();

		mrsd = new ThreadLocalSpreadDate(mrsdString);
		timeLine = generateCustomSliceTimesTimeLine(timeSlices);

		// this is to generate kml output
		layers = new ArrayList<Layer>();

		// Executor for threads
		int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS * 10);

		if (impute) {

			System.out.println("Analyzing trees...");

			// This is for collecting coordinates into polygons
			slicesMap = new ConcurrentHashMap<Double, List<Coordinates>>();

			int readTrees = 1;
			while (treesImporter.hasTree()) {

				currentTree = (RootedTree) treesImporter.importNextTree();

				if (readTrees >= burnIn) {

					executor.submit(new AnalyzeTreeCustomSliceTimes(
							currentTree, precisionString, coordinatesName,
							rateString, timeSlices, timescaler, mrsd,
							slicesMap, useTrueNoise));

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
				slicesMap.remove(sliceTime);
			}// END: while has next

		}// END: if impute

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
	private class Polygons implements Runnable {

		private double sliceTime;
		private int polygonsStyleId;

		private Polygons(Double sliceTime, int polygonsStyleId) {
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

		}// END: run
	}// END: Polygons

	private TimeLine generateCustomSliceTimesTimeLine(double[] timeSlices) {

		// This is a general time span for all of the trees
		int numberOfSlices = timeSlices.length;
		double startTime = mrsd.getTime()
				- (timeSlices[numberOfSlices - 1] * DayInMillis * DaysInYear * timescaler);
		double endTime = mrsd.getTime();
		TimeLine timeLine = new TimeLine(startTime, endTime, numberOfSlices);

		return timeLine;
	}// END: generateMCCTreeTimeLine

}// END: class