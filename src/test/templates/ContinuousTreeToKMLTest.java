package test.templates;

import junit.framework.TestCase;

import org.junit.Test;

import templates.ContinuousTreeToKML;

public class ContinuousTreeToKMLTest extends TestUtils {


	@Test
	public void testContinuousTreeToKML() throws Exception {
		ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

			continuousTreeToKML.setMrsdString("2011-07-28 AD");

			continuousTreeToKML
					.setTreePath(TestUtils.getResourcePath("/data/WNV_relaxed_geo_gamma_MCC.tre"));
			
			continuousTreeToKML.setHPDString("80%HPD");

			continuousTreeToKML.setLatitudeName("location1");
			
			continuousTreeToKML.setLongitudeName("location2");

			continuousTreeToKML.setMaxAltitudeMapping(50000);

			continuousTreeToKML.setMinPolygonRedMapping(100);

			continuousTreeToKML.setMinPolygonGreenMapping(255);

			continuousTreeToKML.setMinPolygonBlueMapping(255);

			continuousTreeToKML.setMinPolygonOpacityMapping(255);

			continuousTreeToKML.setMaxPolygonRedMapping(255);

			continuousTreeToKML.setMaxPolygonGreenMapping(255);

			continuousTreeToKML.setMaxPolygonBlueMapping(25);

			continuousTreeToKML.setMaxPolygonOpacityMapping(255);

			continuousTreeToKML.setMinBranchRedMapping(255);

			continuousTreeToKML.setMinBranchGreenMapping(100);

			continuousTreeToKML.setMinBranchBlueMapping(255);

			continuousTreeToKML.setMinBranchOpacityMapping(255);

			continuousTreeToKML.setMaxBranchRedMapping(25);

			continuousTreeToKML.setMaxBranchGreenMapping(25);

			continuousTreeToKML.setMaxBranchBlueMapping(25);

			continuousTreeToKML.setMaxBranchOpacityMapping(255);

			continuousTreeToKML.setBranchWidth(4);

			continuousTreeToKML.setTimescaler(1);

			continuousTreeToKML.setNumberOfIntervals(100);

			//continuousTreeToKML.setKmlWriterPath("/home/filip/output.kml");
			continuousTreeToKML.setKmlWriterPath("output.kml");

			continuousTreeToKML.GenerateKML();
			
			System.out.println("Finished in: " + continuousTreeToKML.time
					+ " msec \n");

	}// END: ContinuousTreeToKMLTest
	
}// END: class
