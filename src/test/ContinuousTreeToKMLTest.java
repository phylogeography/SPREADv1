package test;

import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;
import templates.ContinuousTreeToKML;

public class ContinuousTreeToKMLTest {

	ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

	public ContinuousTreeToKMLTest() {

		try {

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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: ContinuousTreeToKMLTest

	public void generate() {

		try {

			continuousTreeToKML.GenerateKML();
			System.out.println("Finished in: " + continuousTreeToKML.time
					+ " msec \n");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImportException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}// END: Generate

}// END: class
