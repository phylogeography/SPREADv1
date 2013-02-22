package test.templates;

import org.junit.Test;

import junit.framework.TestCase;

import templates.TimeSlicerToKML;

public class TimeSlicerToKMLTest extends TestCase {

	private static TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

	private static boolean FIRST_ANALYSIS = true;
	
	@Test
	public void testTimeSlicerToKML() throws Exception {


			if(FIRST_ANALYSIS) {
				
				timeSlicerToKML.setAnalysisType(TimeSlicerToKML.FIRST_ANALYSIS);
				
				timeSlicerToKML.setTreePath(ContinuousTreeToKMLTest.getResourcePath("/data/WNV_relaxed_geo_gamma_MCC.tre"));
				
				timeSlicerToKML.setNumberOfIntervals(10);
				
			} else {
				
				timeSlicerToKML.setAnalysisType(TimeSlicerToKML.SECOND_ANALYSIS);
				
				timeSlicerToKML.setCustomSliceHeightsPath(ContinuousTreeToKMLTest.getResourcePath("/data/treeslice_WNV.txt"));
				
			}
			
			timeSlicerToKML.setTreesPath(ContinuousTreeToKMLTest.getResourcePath("/data/WNV_relaxed_geo_gamma.trees"));

			timeSlicerToKML.setBurnIn(0);
			
			timeSlicerToKML.setLocationAttributeName("coords");
			
			timeSlicerToKML.setMrsdString("2012-10-24 AD");
			
			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setRateAttributeName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setUseTrueNoise(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML.setKmlWriterPath("output_custom.kml");
			
			timeSlicerToKML.setMinPolygonRedMapping(0);

			timeSlicerToKML.setMinPolygonGreenMapping(0);

			timeSlicerToKML.setMinPolygonBlueMapping(0);

			timeSlicerToKML.setMinPolygonOpacityMapping(100);
			
			timeSlicerToKML.setMaxPolygonRedMapping(50);

			timeSlicerToKML.setMaxPolygonGreenMapping(255);

			timeSlicerToKML.setMaxPolygonBlueMapping(255);

			timeSlicerToKML.setMaxPolygonOpacityMapping(255);
			
			timeSlicerToKML.setMinBranchRedMapping(0);

			timeSlicerToKML.setMinBranchGreenMapping(0);

			timeSlicerToKML.setMinBranchBlueMapping(0);

			timeSlicerToKML.setMinBranchOpacityMapping(255);

			timeSlicerToKML.setMaxBranchRedMapping(255);

			timeSlicerToKML.setMaxBranchGreenMapping(5);

			timeSlicerToKML.setMaxBranchBlueMapping(50);

			timeSlicerToKML.setMaxBranchOpacityMapping(255);
			
			timeSlicerToKML.setMaxAltitudeMapping(500000);

			timeSlicerToKML.setBranchWidth(4);

			timeSlicerToKML.GenerateKML();

			System.out.println("Finished in: " + timeSlicerToKML.time
					+ " msec \n");

	}// END: main

	
}// END: class
