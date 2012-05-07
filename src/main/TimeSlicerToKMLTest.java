package main;

import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.TimeSlicerToKML;

public class TimeSlicerToKMLTest {

	private static TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

	public static void main(String[] args) {

		try {

			timeSlicerToKML.setAnalysisType(TimeSlicerToKML.FIRST_ANALYSIS);
			
			timeSlicerToKML.setTreePath("/home/filip/Phyleography/data/ContinuousH5N1/HA_alignedEd_diff_gammaRRW_MCC.tre");
			
//			timeSlicerToKML.setTreePath("/home/filip/Phyleography/data/RacRABV/RacRABV_cont_0.8_MCC_snyder.tre");
			
			timeSlicerToKML.setMrsdString("2011-07-29 AD");

			timeSlicerToKML.setTreesPath("/home/filip/Phyleography/data/ContinuousH5N1/HA_alignedEd_diff_gammaRRW.trees");

//			timeSlicerToKML.setTreesPath("/home/filip/Phyleography/data/RacRABV/RacRABV_cont.trees");
			
			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(500);

			timeSlicerToKML.setLocationAttributeName("location");

			timeSlicerToKML.setRateAttributeName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setUseTrueNoise(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML.setKmlWriterPath("/home/filip/Phyleography/data/ContinuousH5N1/output_custom.kml");
			
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

	}// END TimeSlicerToKMLTest

}// END: class
