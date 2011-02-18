package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Place;
import structure.Style;
import structure.TimeLine;
import utils.ReadLocations;
import utils.ReadLog;
import utils.Utils;

public class RateIndicatorBFToKML {

	public static long time;

	private static ReadLocations locations;
	private static ReadLog indicators;
	private static List<Layer> layers;
	private static PrintWriter writer;
	private static int numberOfIntervals;
	private static double maxAltMapping;
	private static double bfCutoff;
	private static List<Double> bayesFactors;
	private static List<String> combin;

	public RateIndicatorBFToKML() throws RuntimeException {

	}// END: RateIndicatorBF()

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setMaxAltitudeMapping(double max) {
		maxAltMapping = max;
	}

	public void setBfCutoff(double cutoff) {
		bfCutoff = cutoff;
	}

	public void setLocationFilePath(String path) {

		locations = new ReadLocations(path);
	}

	public void setLogFilePath(String path, double burnIn) {

		indicators = new ReadLog(path, burnIn);
	}

	public void setKmlWriterPath(String kmlpath) throws FileNotFoundException {
		writer = new PrintWriter(kmlpath);
	}

	public void GenerateKML() throws IOException {

		// Utils.Print2DArray(indicators.indicators);

		// start timing
		time = -System.currentTimeMillis();

		int n = locations.nrow;

		// System.out.println(n + " " + indicators.ncol);

		boolean symmetrical = false;
		if (indicators.ncol == n * (n - 1)) {
			symmetrical = false;
		} else if (indicators.ncol == (n * (n - 1)) / 2) {
			symmetrical = true;
		} else {
			System.err.println("the number of rate indicators does not match "
					+ "the number of locations!");
		}

		combin = new ArrayList<String>();

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.Subset(locations.locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations.locations[row] + ":" + subset[i]);
			}
		}

		if (symmetrical == false) {
			combin.addAll(combin);
		}

		// Utils.PrintArray(combin.toArray());

		double qk = Double.NaN;
		if (symmetrical) {
			qk = (Math.log(2) + n - 1) / ((n * (n - 1)) / 2);
		} else {
			qk = (Math.log(2) + n - 1) / ((n * (n - 1)) / 1);
		}

		double[] pk = Utils.ColMeans(indicators.indicators);

		bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {
			double bf = (pk[row] / (1 - pk[row])) / denominator;
			bayesFactors.add(bf);
		}

		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// Execute threads
		final int NTHREDS = 10;
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

	}// :END: GenerateKML

	// //////////////
	// ---PLACES---//
	// //////////////
	private static class Places implements Runnable {

		public void run() {

			// this is for Places folder:
			String placesDescription = null;
			Layer placesLayer = new Layer("Places", placesDescription);

			for (int i = 0; i < locations.nrow; i++) {
				placesLayer.addItem(new Place(locations.locations[i], null,
						new Coordinates(locations.coordinates[i][1],
								locations.coordinates[i][0]), 0, 0));
			}

			layers.add(placesLayer);

		}
	}// END: Places

	private static class Rates implements Runnable {

		public void run() {

			// /////////////
			// ---RATES---//
			// /////////////

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

					int red = 255;
					int green = 0;
					int blue = (int) Utils.map(bf, 0, bfMax, 255, 0);
					int alpha = (int) Utils.map(bf, 0, bfMax, 100, 255);

					/**
					 * width mapping
					 * */
					double width = Utils.map(bf, 0, bfMax, 3.5, 10.0);
					// System.out.println(width);

					Style linesStyle = new Style(new Color(red, green, blue,
							alpha), width);
					linesStyle.setId("branch_style" + branchStyleId);

					/**
					 * altitude mapping
					 * */
					double maxAltitude = (int) Utils.map(bf, 0, bfMax, 0,
							maxAltMapping);

					String state = combin.get(i).split(":")[1];
					String parentState = combin.get(i).split(":")[0];

					double longitude = Utils.MatchStateCoordinate(locations,
							state, 0);
					double latitude = Utils.MatchStateCoordinate(locations,
							state, 1);

					float parentLongitude = Utils.MatchStateCoordinate(
							locations, parentState, 0);
					float parentLatitude = Utils.MatchStateCoordinate(
							locations, parentState, 1);

					ratesLayer.addItem(new Line(
							combin.get(i) + ", BF=" + bayesFactors.get(i), // string
							// name
							new Coordinates(parentLatitude, parentLongitude),
							Double.NaN, // double
							// startime
							linesStyle, // style startstyle
							new Coordinates(latitude, longitude), // endCoords
							Double.NaN, // double endtime
							linesStyle, // style endstyle
							maxAltitude, // double maxAltitude
							Double.NaN) // double duration
							);

					branchStyleId++;
				}
			}

			layers.add(ratesLayer);

		}
	}// END: Rates

}// END: RateIndicatorBF
