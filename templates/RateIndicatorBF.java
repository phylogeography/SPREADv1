//############
//#---TEST---#
//############
//   /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Test/indicators.log
//   /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Test/locationDegrees

//#################
//##---Philippe---#
//#################
//   /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log
//   /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/locationDegrees

//##############
//##---Nuno---#
//##############
//  /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Nuno/HIV2A_WAcombi_equalfreq_bssvs_rateMatrix.log
//  /home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Nuno/locationHIV2A.txt

package templates;

import generator.KMLGenerator;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import structure.Coordinates;
import structure.Layer;
import structure.Line;
import structure.Place;
import structure.Style;
import structure.TimeLine;
import utils.ReadLocations;
import utils.Utils;

public class RateIndicatorBF {

	public static long time;

	private static ReadLocations locations;
	private static ReadLog indicators;
	private static List<Layer> layers;
	private static PrintWriter writer;

	public static void main(String args[]) throws Exception {

		// start timing
		time = -System.currentTimeMillis();

		// this will be parsed from gui:
		locations = new ReadLocations(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Test/locationDegrees"

		);

		indicators = new ReadLog(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Test/indicators.log",
				0.1

		);

		double bfCutoff = 10.0;
		int numberOfIntervals = 100;
		double maxAltMapping = 500000;

		int n = locations.nrow;
		boolean symmetrical = false;
		if (indicators.ncol == n * (n - 1)) {
			symmetrical = false;
		} else if (indicators.ncol == (n * (n - 1)) / 2) {
			symmetrical = true;
		} else {
			System.err
					.println("the number of rate indicators does not match the number of locations!");
		}

		List<String> combin = new ArrayList<String>();

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.Subset(locations.locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations.locations[row] + ":" + subset[i]);
			}
		}

		if (symmetrical == false) {
			combin.addAll(combin);
		}

		double qk = Double.NaN;
		if (symmetrical) {
			qk = (Math.log(2) + n - 1) / ((n * (n - 1)) / 2);
		} else {
			qk = (Math.log(2) + n - 1) / ((n * (n - 1)) / 1);
		}

		double[] pk = ColMeans(indicators.indicators);

		List<Double> bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {
			double bf = (pk[row] / (1 - pk[row])) / denominator;
			bayesFactors.add(bf);
		}
		
//		Utils.HeadArray(bayesFactors.toArray(), 3);
		
		// this is to generate kml output
		KMLGenerator kmloutput = new KMLGenerator();
		layers = new ArrayList<Layer>();

		// //////////////
		// ---PLACES---//
		// //////////////

		// this is for Places folder:
		String placesDescription = null;
		Layer placesLayer = new Layer("Places", placesDescription);

		for (int i = 0; i < locations.nrow; i++) {
			placesLayer.addItem(new Place(locations.locations[i], null,
					new Coordinates(locations.coordinates[i][1],
							locations.coordinates[i][0]), 0, 0));
		}

		layers.add(placesLayer);

		// ////////////////
		// ---BRANCHES---//
		// ////////////////

		// this is for Branches folder:
		String branchesDescription = null;
		Layer branchesLayer = new Layer("Branches", branchesDescription);

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

				Style linesStyle = new Style(
						new Color(red, green, blue, alpha), width);
				linesStyle.setId("branch_style" + branchStyleId);

				/**
				 * altitude mapping
				 * */
				double maxAltitude = (int) Utils.map(bf, 0, bfMax, 0,
						maxAltMapping);

				String state = combin.get(i).split(":")[1];
				String parentState = combin.get(i).split(":")[0];

				double longitude = Utils.MatchStateCoordinate(locations, state,
						0);
				double latitude = Utils.MatchStateCoordinate(locations, state,
						1);

				float parentLongitude = Utils.MatchStateCoordinate(locations,
						parentState, 0);
				float parentLatitude = Utils.MatchStateCoordinate(locations,
						parentState, 1);

				branchesLayer.addItem(new Line(
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

		layers.add(branchesLayer);

		// generate kml
		writer = new PrintWriter("/home/filip/Pulpit/output.kml");
		TimeLine timeLine = new TimeLine(Double.NaN, Double.NaN,
				numberOfIntervals);
		kmloutput.generate(writer, timeLine, layers);

		// stop timing
		time += System.currentTimeMillis();
		System.out.println("finished in: " + time + " msec");
	}// END: main

	private static double ColMean(double a[][], int col) {
		double sum = 0;
		int nrows = a.length;
		for (int row = 0; row < nrows; row++) {
			sum += a[row][col];
		}
		return sum / nrows;
	}

	private static double[] ColMeans(double a[][]) {
		int ncol = a[0].length;
		double[] b = new double[ncol];
		for (int c = 0; c < ncol; c++) {
			b[c] = ColMean(a, c);
		}
		return b;
	}

}// END: RateIndicatorBF
