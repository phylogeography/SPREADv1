package test;

import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.ContinuousTreeToKML;
import templates.TimeSlicerToKML;

public class Main {

	public static void main(String[] args) {

		// ContinuousTreeToKMLTest();
		TimeSlicerToKMLTest();

	}// END: main

	public static void ContinuousTreeToKMLTest() {

		try {

			// 5/2011
			ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

			continuousTreeToKML.setMrsdString("2011-07-29 AD");

			continuousTreeToKML
					.setTreePath("/home/filip/RacRABV_cont_0.8_MCC_snyder.tre");

			continuousTreeToKML.setHPD("80%");

			continuousTreeToKML.setCoordinatesName("location");

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

			continuousTreeToKML.setKmlWriterPath("/home/filip/output.kml");

			continuousTreeToKML.GenerateKML();

			System.out.println("Finished in: " + continuousTreeToKML.time
					+ " msec \n");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: ContinuousTreeToKMLTest

	public static void TimeSlicerToKMLTest() {

		try {

			// 12/2009
			TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

			timeSlicerToKML.setMrsdString("2011-07-29 AD");

			timeSlicerToKML
					.setTreePath("/home/filip/RacRABV_cont_0.8_MCC_snyder.tre");

			timeSlicerToKML.setTreesPath("/home/filip/RacRABV_cont.trees");

			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(500);

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setTrueNoise(true);

			timeSlicerToKML.setImpute(false);

			timeSlicerToKML.setNumberOfIntervals(10);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML.setKmlWriterPath("/home/filip/output.kml");

			timeSlicerToKML.setMaxAltitudeMapping(50000);

			timeSlicerToKML.setMinPolygonRedMapping(24);

			timeSlicerToKML.setMinPolygonGreenMapping(155);

			timeSlicerToKML.setMinPolygonBlueMapping(200);

			timeSlicerToKML.setMinPolygonOpacityMapping(255);

			timeSlicerToKML.setMaxPolygonRedMapping(25);

			timeSlicerToKML.setMaxPolygonGreenMapping(12);

			timeSlicerToKML.setMaxPolygonBlueMapping(56);

			timeSlicerToKML.setMaxPolygonOpacityMapping(255);

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
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}// END TimeSlicerToKMLTest

}// END: Main
