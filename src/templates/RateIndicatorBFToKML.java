package templates;

import generator.KMLGenerator;
import gui.InteractiveTableModel;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jebl.evolution.io.ImportException;
import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Place;
import structure.Style;
import structure.TimeLine;
import utils.ReadLog;
import utils.Utils;

public class RateIndicatorBFToKML {

	public static long time;

	private static InteractiveTableModel table;
	private ReadLog indicators;
	private List<Layer> layers;
	private PrintWriter writer;
	private int numberOfIntervals;
	private double maxAltMapping;
	private double bfCutoff;
	private List<Double> bayesFactors;
	private List<String> combin;

	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double branchWidth;
	private double meanPoissonPrior;
	private double poissonPriorOffset;

	private enum PoissonPriorEnum {
		DEFAULT, USER
	}

	private PoissonPriorEnum poissonPriorOffsetSwitcher;
	private PoissonPriorEnum meanPoissonPriorSwitcher;

	public RateIndicatorBFToKML() {
	}// END: RateIndicatorBF()

	public void setDefaultPoissonPriorOffset() {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserPoissonPriorOffset(double offset) {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.USER;
		poissonPriorOffset = offset;
	}

	public void setDefaultMeanPoissonPrior() {
		meanPoissonPriorSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserMeanPoissonPrior(double mean) {
		meanPoissonPriorSwitcher = PoissonPriorEnum.USER;
		meanPoissonPrior = mean;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setMaxAltitudeMapping(double max) {
		maxAltMapping = max;
	}

	public void setBfCutoff(double cutoff) {
		bfCutoff = cutoff;
	}

	public static void setTable(InteractiveTableModel tableModel) {
		table = tableModel;
	}

	public void setLogFilePath(String path, double burnIn) {
		indicators = new ReadLog(path, burnIn);
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
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

		ComputeBFTest();

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// Execute threads
		final int NTHREDS = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);

		executor.submit(new Places());
		executor.submit(new Rates());
		executor.shutdown();
		// Wait until all threads are finished
		while (!executor.isTerminated()) {
		}

		// generate kml
		TimeLine timeLine = new TimeLine(Double.NaN, Double.NaN,
				numberOfIntervals);
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

			for (int i = 0; i < table.getRowCount(); i++) {

				String name = String.valueOf(table.getValueAt(i, 0));

				Double longitude = Double.valueOf(String.valueOf(table
						.getValueAt(i, 2)));

				Double latitude = Double.valueOf(String.valueOf(table
						.getValueAt(i, 1)));

				placesLayer.addItem(new Place(name, null, new Coordinates(
						longitude, latitude), 0, 0));
			}

			layers.add(placesLayer);
		}
	}// END: Places

	// /////////////
	// ---RATES---//
	// /////////////
	private class Rates implements Runnable {

		public void run() {

			System.out.println("BF cutoff = " + bfCutoff);
			System.out.println("mean Poisson Prior = " + meanPoissonPrior);
			System.out.println("Poisson Prior offset = " + poissonPriorOffset);

			// this is for Branches folder:
			String ratesDescription = null;
			Layer ratesLayer = new Layer("Discrete rates", ratesDescription);

			double bfMax = Math.log(Utils.getListMax(bayesFactors));

			int branchStyleId = 1;
			for (int i = 0; i < combin.size(); i++) {

				if (bayesFactors.get(i) > bfCutoff) {

					/**
					 * Color mapping
					 * */
					double bf = Math.log(bayesFactors.get(i));

					int red = (int) Utils.map(bf, 0, bfMax,
							minBranchRedMapping, maxBranchRedMapping);
					int green = (int) Utils.map(bf, 0, bfMax,
							minBranchGreenMapping, maxBranchGreenMapping);
					int blue = (int) Utils.map(bf, 0, bfMax,
							minBranchBlueMapping, maxBranchBlueMapping);
					int alpha = (int) Utils.map(bf, 0, bfMax,
							maxBranchOpacityMapping, minBranchOpacityMapping);

					Style linesStyle = new Style(new Color(red, green, blue,
							alpha), branchWidth);
					linesStyle.setId("branch_style" + branchStyleId);

					/**
					 * altitude mapping
					 * */
					double maxAltitude = (int) Utils.map(bf, 0, bfMax, 0,
							maxAltMapping);

					String state = combin.get(i).split(":")[1];
					String parentState = combin.get(i).split(":")[0];

					float longitude = Utils.matchStateCoordinate(table, state,
							2);
					float latitude = Utils
							.matchStateCoordinate(table, state, 1);

					float parentLongitude = Utils.matchStateCoordinate(table,
							parentState, 2);
					float parentLatitude = Utils.matchStateCoordinate(table,
							parentState, 1);

					ratesLayer.addItem(new Line(
							combin.get(i) + ", BF=" + bayesFactors.get(i), // name
							new Coordinates(parentLongitude, parentLatitude),
							Double.NaN, // startime
							linesStyle, // style startstyle
							new Coordinates(longitude, latitude), // endCoords
							Double.NaN, // double endtime
							linesStyle, // style endstyle
							maxAltitude, // double maxAltitude
							Double.NaN) // double duration
							);

					branchStyleId++;

					System.out.println("BF=" + bayesFactors.get(i)
							+ " : between " + parentState + " (long: "
							+ parentLongitude + "; lat: " + parentLatitude
							+ ") and " + state + " (long: " + longitude
							+ "; lat: " + latitude + ")");

				}
			}

			layers.add(ratesLayer);
		}
	}// END: Rates

	private void ComputeBFTest() {

		int n = table.getRowCount();

		switch (meanPoissonPriorSwitcher) {
		case DEFAULT:
			meanPoissonPrior = Math.log(2);
			break;
		case USER:
			break;
		}

		switch (poissonPriorOffsetSwitcher) {
		case DEFAULT:
			poissonPriorOffset = n - 1;
			break;
		case USER:
			break;
		}

		boolean symmetrical = false;
		if (indicators.ncol == n * (n - 1)) {
			symmetrical = false;
		} else if (indicators.ncol == (n * (n - 1)) / 2) {
			symmetrical = true;
		} else {
			throw new RuntimeException(
					"the number of rate indicators does not match the number of locations!");
		}

		combin = new ArrayList<String>();
		String[] locations = table.getColumn(0);

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.subset(locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations[row] + ":" + subset[i]);
			}
		}

		if (symmetrical == false) {
			combin.addAll(combin);
		}

		double qk = Double.NaN;
		if (symmetrical) {
			qk = (meanPoissonPrior + poissonPriorOffset) / ((n * (n - 1)) / 2);
		} else {
			qk = (meanPoissonPrior + poissonPriorOffset) / ((n * (n - 1)) / 1);
		}

		double[] pk = Utils.colMeans(indicators.indicators);

		bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {
			double bf = (pk[row] / (1 - pk[row])) / denominator;
			bayesFactors.add(bf);
		}
	}// END: ComputeBFTest

}// END: RateIndicatorBF
