package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.TimeSlicerToKMLCustomTimeSlices;

public class TimeSlicerToKMLCustomTimeSlicesTest {

	TimeSlicerToKMLCustomTimeSlices timeSlicerToKML = new TimeSlicerToKMLCustomTimeSlices();

	public TimeSlicerToKMLCustomTimeSlicesTest() {

		try {

			timeSlicerToKML.setMrsdString("2007-08-15 AD");

			timeSlicerToKML
					.setTreesPath("/home/filip/WNV_relaxed_geo_gamma.trees");

			timeSlicerToKML.setCustomSliceHeights("/home/filip/sliceTimes");

			timeSlicerToKML.setHPD(0.80);

			timeSlicerToKML.setGridSize(100);

			timeSlicerToKML.setBurnIn(500);

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setTrueNoise(true);

			timeSlicerToKML.setImpute(true);

			timeSlicerToKML.setTimescaler(1);

			timeSlicerToKML
					.setKmlWriterPath("/home/filip/Dropbox/SPREAD/output_test.kml");

			timeSlicerToKML.setMinPolygonRedMapping(24);

			timeSlicerToKML.setMinPolygonGreenMapping(155);

			timeSlicerToKML.setMinPolygonBlueMapping(200);

			timeSlicerToKML.setMinPolygonOpacityMapping(255);

			timeSlicerToKML.setMaxPolygonRedMapping(25);

			timeSlicerToKML.setMaxPolygonGreenMapping(12);

			timeSlicerToKML.setMaxPolygonBlueMapping(56);

			timeSlicerToKML.setMaxPolygonOpacityMapping(255);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}// END: TimeSlicerToKMLCustomTimeSlicesTest

	public void Generate() {

		try {

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

	}// END: Generate

}// END: class

