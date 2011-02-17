package templates;

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
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log"

		);

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

		double qk = Double.NaN;
		if (symmetrical) {
			qk = (Math.log(2) + K - 1) / ((K * (K - 1)) / 2);
		} else {
			qk = (Math.log(2) + K - 1) / ((K * (K - 1)) / 1);
		}

		double[] pk = colMean(indicators.indicators);

		Utils.PrintArray(pk);
		
		System.out.println(indicators.time);
	}// END: main

	private static double[] colMean(float[][] a) {

		int nrow = a.length;
		int ncol = a[0].length;
		double sum;

		double[] b = new double[ncol];
		for (int col = 0; col < ncol; col++) {
			sum = 0;
			for (int row = 0; row < nrow; row++) {
				sum += a[row][col];
			}
			b[col] = sum / ncol;
		}
		return b;
	}

	
}// END: RateIndicatorBF
