package main;

import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.TimeSlicerToKML;

public class TimeSlicerToKMLTest {

	private static TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

	private static boolean FIRST_ANALYSIS = false;
	
	public static void main(String[] args) {

		try {

			if(FIRST_ANALYSIS) {
				
				timeSlicerToKML.setAnalysisType(TimeSlicerToKML.FIRST_ANALYSIS);
				
				timeSlicerToKML.setTreePath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/Cent_ITS_broad.tree");
				
				timeSlicerToKML.setNumberOfIntervals(10);
				
			} else {
				
				timeSlicerToKML.setAnalysisType(TimeSlicerToKML.SECOND_ANALYSIS);
				
				timeSlicerToKML.setCustomSliceHeightsPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/treeslice_small.txt");
				
			}
			
			timeSlicerToKML.setTreesPath("/home/filip/Dropbox/SPREAD_dev/CustomTimeSlicing/Cent_ITS_small.trees");

			timeSlicerToKML.setBurnIn(0);
			
			timeSlicerToKML.setLocationAttributeName("coords");
			
			timeSlicerToKML.setMrsdString("2012-10-24 AD");
			
			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setRateAttributeName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setUseTrueNoise(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML.setKmlWriterPath("/home/filip/Dropbox/SPREAD_dev/output_custom.kml");
			
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

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: main

}// END: class
