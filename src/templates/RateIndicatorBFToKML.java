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
import utils.Holder;
import utils.ReadLog;
import utils.Utils;
import utils.Utils.PoissonPriorEnum;

public class RateIndicatorBFToKML {

	public static long time;

	private InteractiveTableModel table;
	private ReadLog indicators;
	private List<Layer> layers;
	private PrintWriter writer;
	private int numberOfIntervals;
	private double maxAltMapping;
	private double bfCutoff;
	private ArrayList<Double> bayesFactors;
	private ArrayList<String> combin;
	private BayesFactorTest bfTest;
	
	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double branchWidth;

	private Holder meanPoissonPrior = new Holder(0.0);
	private Holder poissonPriorOffset = new Holder(0.0);

	private PoissonPriorEnum poissonPriorOffsetSwitcher;
	private PoissonPriorEnum meanPoissonPriorSwitcher;

	public RateIndicatorBFToKML() {
	}// END: RateIndicatorBF()

	public void setDefaultPoissonPriorOffset() {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserPoissonPriorOffset(double offset) {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.USER;
		poissonPriorOffset.value = offset;
	}

	public void setDefaultMeanPoissonPrior() {
		meanPoissonPriorSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserMeanPoissonPrior(double mean) {
		meanPoissonPriorSwitcher = PoissonPriorEnum.USER;
		meanPoissonPrior.value = mean;
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

	public void setTable(InteractiveTableModel tableModel) {
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

		combin = new ArrayList<String>();
		bayesFactors = new ArrayList<Double>();

		bfTest = new BayesFactorTest(table, meanPoissonPriorSwitcher, meanPoissonPrior,
				poissonPriorOffsetSwitcher, poissonPriorOffset, indicators,
				combin, bayesFactors);
		
		bfTest.ComputeBFTest();

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

			try {

				System.out.println("BF cutoff = " + bfCutoff);
				System.out.println("mean Poisson Prior = "
						+ meanPoissonPrior.value);
				System.out.println("Poisson Prior offset = "
						+ poissonPriorOffset.value);

				// this is for Branches folder:
				String ratesDescription = null;
				Layer ratesLayer = new Layer("Discrete rates", ratesDescription);

				double bfMax = Math.log(Utils.getListMax(bayesFactors));
                Integer[] sortOrder = bfTest.getSortOrder();
				
				int branchStyleId = 1;
				int index = 0;
				
				for (int i = 0; i < combin.size(); i++) {

					if (bayesFactors.get(sortOrder[i]) > bfCutoff) {

						/**
						 * Color mapping
						 * */
						double bf = Math.log(bayesFactors.get(sortOrder[i]));

						int red = (int) Utils.map(bf, 0, bfMax,
								minBranchRedMapping, maxBranchRedMapping);
						int green = (int) Utils.map(bf, 0, bfMax,
								minBranchGreenMapping, maxBranchGreenMapping);
						int blue = (int) Utils.map(bf, 0, bfMax,
								minBranchBlueMapping, maxBranchBlueMapping);
						int alpha = (int) Utils.map(bf, 0, bfMax,
								maxBranchOpacityMapping,
								minBranchOpacityMapping);

						Style linesStyle = new Style(new Color(red, green,
								blue, alpha), branchWidth);
						linesStyle.setId("branch_style" + branchStyleId);

						/**
						 * altitude mapping
						 * */
						double maxAltitude = (int) Utils.map(bf, 0, bfMax, 0,
								maxAltMapping);

						String state = combin.get(sortOrder[i]).split(":")[1];
						String parentState = combin.get(sortOrder[i]).split(":")[0];

						float longitude = Utils.matchStateCoordinate(table,
								state, 2);
						float latitude = Utils.matchStateCoordinate(table,
								state, 1);

						float parentLongitude = Utils.matchStateCoordinate(
								table, parentState, 2);
						float parentLatitude = Utils.matchStateCoordinate(
								table, parentState, 1);

						ratesLayer.addItem(new Line(combin.get(sortOrder[i]) + ", BF="
										+ bayesFactors.get(sortOrder[i]), // name
										new Coordinates(parentLongitude,
												parentLatitude), Double.NaN, // startime
										linesStyle, // style startstyle
										new Coordinates(longitude, latitude), // endCoords
										Double.NaN, // double endtime
										linesStyle, // style endstyle
										maxAltitude, // double maxAltitude
										Double.NaN) // double duration
								);

						branchStyleId++;

						System.out.println(index + "\t" + " BF=" + bayesFactors.get(sortOrder[i])
								+ " : between " + parentState + " (long: "
								+ parentLongitude + "; lat: " + parentLatitude
								+ ") and " + state + " (long: " + longitude
								+ "; lat: " + latitude + ")");
						index++;
						
					}// END: cutoff check
				}// END: i loop

				layers.add(ratesLayer);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}// END: run
	}// END: Rates

}// END: RateIndicatorBF
