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
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log",
				0.1

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

		double[] pk = ColMeans(indicators.indicators);

		
		
		
		
		Utils.PrintArray(pk);

		System.out.println("finished in: " + indicators.time + " msec");
	}// END: main

	private static double ColMean(float a[][], int col) {
		double sum = 0;
		int nrows = a.length;
		for (int row = 0; row < nrows; row++) {
			sum += a[row][col];
		}
		return sum / nrows;
	}

	private static double[] ColMeans(float a[][]) {
		int ncol = a[0].length;
		double[] b = new double[ncol];
		for (int c = 0; c < ncol; c++) {
			b[c] = ColMean(a, c);
		}
		return b;
	}

}// END: RateIndicatorBF
