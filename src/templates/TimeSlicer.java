// "/home/filip/Dropbox/Phyleography/data/WNX/WNV_relaxed_geo_gamma.trees"

package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import contouring.ContourMaker;
import contouring.ContourPath;
import contouring.ContourWithSynder;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import jebl.evolution.trees.Tree;
import math.MultivariateNormalDistribution;
import structure.Coordinates;
import structure.Layer;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.SpreadDate;
import utils.Utils;

public class TimeSlicer {

	public static long time;

	private static final int DayInMillis = 86400000;

	private static RootedTree MccTree;
	private static TreeImporter treesImporter;
	private static TreeImporter treeImporter;
	private static double treeRootHeight;
	private static String precisionString;
	private static String locationString;
	private static String rateString;
	private static int numberOfIntervals;
	private static boolean trueNoise;
	private static String mrsdString;
	private static double timescaler;
	private static TimeLine timeLine;
	private static SimpleDateFormat formatter;
	private static HashMap<Double, List<Coordinates>> sliceMap;
	private static double sliceTimeMax;
	private static double sliceTimeMin;
	private static List<Layer> layers;
	private static PrintWriter writer;

	private enum timescalerEnum {
		DAYS, MONTHS, YEARS
	}

	private static timescalerEnum timescalerSwitcher;

	public static void main(String args[]) throws Exception {

		// start timing
		time = -System.currentTimeMillis();

		// this will be parsed from gui
		treesImporter = new NexusImporter(
				new FileReader(
						"/home/filip/Dropbox/Phyleography/data/WNX/WNV_relaxed_geo_gamma.trees"));

		treeImporter = new NexusImporter(
				new FileReader(
						"/home/filip/Dropbox/Phyleography/data/WNX/WNV_relaxed_geo_gamma_MCC.tre"));

		precisionString = "precision";
		locationString = "location";
		rateString = "rate";
		numberOfIntervals = 10;
		trueNoise = false;
		mrsdString = "2006-12-31";

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

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		MccTree = (RootedTree) treeImporter.importNextTree();

		// this is for time calculations
		treeRootHeight = MccTree.getHeight(MccTree.getRootNode());

		// this is for mappings
		sliceTimeMin = Double.MAX_VALUE;
		sliceTimeMax = -Double.MAX_VALUE;

		// This is a general time span for all of the trees
		SpreadDate mrsd = new SpreadDate(mrsdString);
		timeLine = new TimeLine(mrsd.getTime()
				- (treeRootHeight * DayInMillis * timescaler), mrsd.getTime(),
				numberOfIntervals);

		sliceMap = new HashMap<Double, List<Coordinates>>();

		List<Tree> forest = treesImporter.importTrees();

		double burnIn = 0.1;

		for (int i = (int) (forest.size() * burnIn); i < forest.size(); i++) {

			RootedTree currentTree = (RootedTree) forest.get(i);

			// TODO: start separate thread for each tree
			analyzeTree(currentTree);
		}

		Polygons();

		writer = new PrintWriter("/home/filip/Pulpit/output.kml");
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();
		System.out.println("Finished in: " + time + " msec");
	}// END: main

	private static void analyzeTree(RootedTree tree) throws ParseException {

		double startTime = timeLine.getStartTime();
		double endTime = timeLine.getEndTime();
		double timeSpan = startTime - endTime;

		for (Node node : tree.getNodes()) {

			if (!tree.isRoot(node)) {

				for (int i = numberOfIntervals; i > 0; i--) {

					Node parentNode = tree.getParent(node);

					double nodeHeight = tree.getHeight(node);
					double parentHeight = tree.getHeight(parentNode);

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

					if (sliceTime < sliceTimeMin) {
						sliceTimeMin = sliceTime;
					}

					if (sliceTime > sliceTimeMax) {
						sliceTimeMax = sliceTime;
					}

					SpreadDate mrsd0 = new SpreadDate(mrsdString);
					double parentTime = mrsd0
							.minus((int) (parentHeight * timescaler));

					SpreadDate mrsd1 = new SpreadDate(mrsdString);
					double nodeTime = mrsd1
							.minus((int) (nodeHeight * timescaler));

					Object[] imputedLocation = imputeValue(location,
							parentLocation, sliceTime, nodeTime, parentTime,
							tree, rate, trueNoise);

					// TODO: improve that
					if (parentTime < sliceTime && sliceTime <= nodeTime) {

						if (sliceMap.containsKey(sliceTime)) {

							sliceMap.get(sliceTime).add(
									new Coordinates(parentLongitude,
											parentLatitude, 0.0));
							sliceMap.get(sliceTime).add(
									new Coordinates(Double
											.valueOf(imputedLocation[1]
													.toString()), Double
											.valueOf(imputedLocation[0]
													.toString()), 0.0));
							sliceMap.get(sliceTime).add(
									new Coordinates(longitude, latitude, 0.0));

						} else {

							// double[][] coordinates = new double[2][];

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

	}// END: analyzeTree

	private static Object[] imputeValue(Object[] location,
			Object[] parentLocation, double sliceTime, double nodeTime,
			double parentTime, RootedTree tree, double rate, boolean trueNoise) {

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

	// ////////////////
	// ---POLYGONS---//
	// ////////////////
	private static void Polygons() {

		try {

			formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);

			Set<Double> HostKeys = sliceMap.keySet();
			Iterator<Double> iterator = HostKeys.iterator();

			int polygonsStyleId = 1;

			while (iterator.hasNext()) {

				Double sliceTime = (Double) iterator.next();
				String polygonsDescription = null;
				Layer polygonsLayer = new Layer("Time Slices"
						+ formatter.format(sliceTime), polygonsDescription);
				/**
				 * Color and Opacity mapping
				 * */
				int red = 55;
				int green = (int) Utils.map(sliceTime, sliceTimeMin,
						sliceTimeMax, 255, 0);
				int blue = 0;
				int alpha = (int) Utils.map(sliceTime, sliceTimeMin,
						sliceTimeMax, 100, 255);

				Color col = new Color(red, green, blue, alpha);
				Style polygonsStyle = new Style(col, 0);
				polygonsStyle.setId("polygon_style" + polygonsStyleId);

				// TODO: clean this!
				List<Coordinates> list = sliceMap.get(sliceTime);

				double[] x = new double[list.size()];
				double[] y = new double[list.size()];

				for (int i = 0; i < list.size(); i++) {

					x[i] = list.get(i).getLatitude();
					y[i] = list.get(i).getLongitude();

				}

				List<Coordinates> coords = new ArrayList<Coordinates>();
				ContourMaker contourMaker = new ContourWithSynder(x, y, 200);
				ContourPath[] paths = contourMaker.getContourPaths(0.8);

				int pathCounter = 1;
				for (ContourPath path : paths) {

					double[] latitude = path.getAllX();
					double[] longitude = path.getAllY();

					for (int i = 0; i < latitude.length; i++) {

						coords.add(new Coordinates(longitude[i], latitude[i],
								0.0));
					}

					polygonsLayer.addItem(new Polygon("HPDRegion_"
							+ pathCounter, // name
							coords, // List<Coordinates>
							polygonsStyle, // Style style
							sliceTime, // double startime
							0.0 // double duration
							));

					pathCounter++;

				}// END: paths loop

				polygonsStyleId++;
				layers.add(polygonsLayer);
			}// END: Map keys loop

		} catch (Exception e) {

			e.printStackTrace();

		}
	}// END: Polygons

}// END: TimeSlicer class
