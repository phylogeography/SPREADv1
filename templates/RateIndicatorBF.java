package templates;

import utils.ReadLocations;
import utils.Utils;

public class RateIndicatorBF {

	static ReadLocations data;
	static ReadLog indicators;

	public static void main(String args[]) throws Exception {

		// this will be parsed from gui:
		data = new ReadLocations(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/locationDegrees"

		);

		indicators = new ReadLog(
				"/home/filip/Dropbox/Java-ML/JavaProjects/TestlabOutbreak/data/Philippe/genomes.HKYG.UCLN.EGC.DISC.BSSVS.Indicator.log"

		);

//		Utils.Head2DArray(indicators.indicators, 2);
//		Utils.Print2DArray(indicators.indicators);
//		Utils.PrintArray(indicators.colNames);
//		System.out.println(indicators.nrow);
//		System.out.println(indicators.ncol);
//		System.out.println(indicators.indicators[0].length);

		System.out.println("Done");
	}// END: main
	
	

}// END: RateIndicatorBF
