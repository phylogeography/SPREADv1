package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.ContinuousTreeToKML;
import templates.TimeSlicerToKML;
import templates.TimeSlicerToKMLCustomTimeSlices;

public class Main {

	public static void main(String[] args) {

		// ContinuousTreeToKMLTest();
		// TimeSlicerToKMLTest();
		TimeSlicerToKMLCustomTimeSlicesTest();

	}// END: main

	public static void ContinuousTreeToKMLTest() {

		try {

			ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

			continuousTreeToKML.setMrsdString("2011-07-28 AD");

			continuousTreeToKML
					.setTreePath("/home/filip/Dropbox/SPREAD/WNV_relaxed_geo_gamma_MCC.tre");

			continuousTreeToKML.setHPDString("80%HPD");

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

			// sliceHeight: 0.0
			// sliceHeight: 9.083738151471586
			// sliceHeight: 8.175364336324428
			// sliceHeight: 7.266990521177268
			// sliceHeight: 6.35861670603011
			// sliceHeight: 5.450242890882952
			// sliceHeight: 4.541869075735793
			// sliceHeight: 3.633495260588635
			// sliceHeight: 2.7251214454414763
			// sliceHeight: 1.8167476302943175
			// sliceHeight: 0.9083738151471596

			TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

			timeSlicerToKML.setMrsdString("2011-07-29 AD");

			timeSlicerToKML
					.setTreePath("/home/filip/Dropbox/SPREAD/WNV_relaxed_geo_gamma_MCC.tre");

			timeSlicerToKML
					.setTreesPath("/home/filip/Dropbox/SPREAD/WNV_relaxed_geo_gamma.trees");

			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(0);

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setTrueNoise(true);

			timeSlicerToKML.setImpute(true);

			timeSlicerToKML.setNumberOfIntervals(10);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML
					.setKmlWriterPath("/home/filip/Dropbox/SPREAD/output.kml");

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

	public static void TimeSlicerToKMLCustomTimeSlicesTest() {

		try {

			TimeSlicerToKMLCustomTimeSlices timeSlicerToKML = new TimeSlicerToKMLCustomTimeSlices();

			timeSlicerToKML.setMrsdString("2007-08-15 AD");

			timeSlicerToKML
					.setTreesPath("/home/filip/Dropbox/SPREAD/WNV_relaxed_geo_gamma.trees");

			timeSlicerToKML
					.setTimeSlices("/home/filip/Dropbox/SPREAD/sliceTimes");

			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(0);

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setTrueNoise(true);

			timeSlicerToKML.setImpute(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML
					.setKmlWriterPath("/home/filip/Dropbox/SPREAD/output.kml");

			timeSlicerToKML.setMinPolygonRedMapping(24);

			timeSlicerToKML.setMinPolygonGreenMapping(155);

			timeSlicerToKML.setMinPolygonBlueMapping(200);

			timeSlicerToKML.setMinPolygonOpacityMapping(255);

			timeSlicerToKML.setMaxPolygonRedMapping(25);

			timeSlicerToKML.setMaxPolygonGreenMapping(12);

			timeSlicerToKML.setMaxPolygonBlueMapping(56);

			timeSlicerToKML.setMaxPolygonOpacityMapping(255);

			timeSlicerToKML.GenerateKML();

			System.out.println("Finished in: " + timeSlicerToKML.time
					+ " msec \n");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	}

}// END: Main
