package templates;

import java.util.ArrayList;
import java.util.List;

import utils.ReadLocations;
import utils.Utils;

public class RateIndicatorBF {

	public static long time;

	private static ReadLocations locations;
	private static ReadLog indicators;

	public static void main(String args[]) throws Exception {

		// start timing
		time = -System.currentTimeMillis();

		// this will be parsed from gui:
		locations = new ReadLocations(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/locationDegrees"

		);

		indicators = new ReadLog(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log",
				0.1

		);

		double bfCutoff = 10.0;

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
		// List<String> from = new ArrayList<String>();
		// List<String> to = new ArrayList<String>();

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.Subset(locations.locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations.locations[row] + ":" + subset[i]);
				// from.add(locations.locations[row]);
				// to.add(subset[i]);
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
		// Utils.PrintArray(pk);

		List<Double> bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {

			double bf = (pk[row] / (1 - pk[row])) / denominator;

			bayesFactors.add(bf);
		}

		
		
		Utils.PrintTwoArrays(combin.toArray(), bayesFactors.toArray());

		
		
		
		
		
		
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
