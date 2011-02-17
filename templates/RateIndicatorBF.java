//http://www.merriampark.com/comb.htm
package templates;

import java.util.ArrayList;
import java.util.List;

import utils.ReadLocations;
import utils.Utils;

public class RateIndicatorBF {

	static ReadLocations locations;
	static ReadLog indicators;

	public static void main(String args[]) throws Exception {

		// this will be parsed from gui:
		locations = new ReadLocations(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/locationDegrees"

		);

		indicators = new ReadLog(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log",
				0.1

		);

		double bfCutoff = 10.0;

		int K = locations.nrow;
		boolean symmetrical = false;
		if (indicators.ncol == K * (K - 1)) {
			symmetrical = false;
		} else if (indicators.ncol == (K * (K - 1)) / 2) {
			symmetrical = true;
		} else {
			System.err
					.println("the number of rate indicators does not match the number of locations!");
		}

		// TODO: combinations

		for(int row = 0; row<locations.locations.length;row++) {
			
		}
		
		
		
		
		
		
		double qk = Double.NaN;
		if (symmetrical) {
			qk = (Math.log(2) + K - 1) / ((K * (K - 1)) / 2);
		} else {
			qk = (Math.log(2) + K - 1) / ((K * (K - 1)) / 1);
		}

		double[] pk = ColMeans(indicators.indicators);
		// Utils.PrintArray(pk);

		List<Double> bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {

			double bf = (pk[row] / (1 - pk[row])) / denominator;

			if (bf > bfCutoff) {
				bayesFactors.add(bf);
			}
		}

		Utils.PrintArray(bayesFactors.toArray());

		System.out.println("finished in: " + indicators.time + " msec");
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
