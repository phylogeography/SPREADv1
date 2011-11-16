package test;

import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.TimeSlicerToKML;

public class TimeSlicerToKMLTest {

	private static TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

	public static void main(String[] args) {

		try {

			timeSlicerToKML.setAnalysisType(TimeSlicerToKML.SECOND_ANALYSIS);

			timeSlicerToKML.setMrsdString("2011-07-29 AD");

			timeSlicerToKML.setCustomSliceHeights("/home/filip/sliceTimes");

			timeSlicerToKML
					.setTreesPath("/home/filip/WNV_relaxed_geo_gamma.trees");

			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(500);

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setTrueNoise(true);

			timeSlicerToKML.setImpute(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML.setKmlWriterPath("/home/filip/output.kml");

			timeSlicerToKML.setMinPolygonRedMapping(24);

			timeSlicerToKML.setMinPolygonGreenMapping(155);

			timeSlicerToKML.setMinPolygonBlueMapping(200);

			timeSlicerToKML.setMinPolygonOpacityMapping(255);

			timeSlicerToKML.setMaxPolygonRedMapping(25);

			timeSlicerToKML.setMaxPolygonGreenMapping(12);

			timeSlicerToKML.setMaxPolygonBlueMapping(56);

			timeSlicerToKML.setMaxPolygonOpacityMapping(255);

			timeSlicerToKML.setMaxAltitudeMapping(500000);

			timeSlicerToKML.setMinBranchRedMapping(54);

			timeSlicerToKML.setMinBranchGreenMapping(33);

			timeSlicerToKML.setMinBranchBlueMapping(120);

			timeSlicerToKML.setMinBranchOpacityMapping(255);

			timeSlicerToKML.setMaxBranchRedMapping(35);

			timeSlicerToKML.setMaxBranchGreenMapping(35);

			timeSlicerToKML.setMaxBranchBlueMapping(70);

			timeSlicerToKML.setMaxBranchOpacityMapping(255);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// END TimeSlicerToKMLTest

}// END: class
