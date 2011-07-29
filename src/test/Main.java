package test;

import templates.ContinuousTreeToKML;

public class Main {

	public static void main(String[] args) {

		ContinuousTreeToKMLTest();

	}// END: main

	static void ContinuousTreeToKMLTest() {

		try {

			ContinuousTreeToKML continuousTreeToKML = new ContinuousTreeToKML();

			continuousTreeToKML.setHPD("80%");

			continuousTreeToKML.setCoordinatesName("location");

			continuousTreeToKML.setMaxAltitudeMapping(50000);

			continuousTreeToKML.setMinPolygonRedMapping(100);

			continuousTreeToKML.setMinPolygonGreenMapping(255);

			continuousTreeToKML.setMinPolygonBlueMapping(255);

			continuousTreeToKML.setMinPolygonOpacityMapping(25);

			continuousTreeToKML.setMaxPolygonRedMapping(255);

			continuousTreeToKML.setMaxPolygonGreenMapping(255);

			continuousTreeToKML.setMaxPolygonBlueMapping(25);

			continuousTreeToKML.setMaxPolygonOpacityMapping(255);

			continuousTreeToKML.setMinBranchRedMapping(255);

			continuousTreeToKML.setMinBranchGreenMapping(100);

			continuousTreeToKML.setMinBranchBlueMapping(255);

			continuousTreeToKML.setMinBranchOpacityMapping(122);

			continuousTreeToKML.setMaxBranchRedMapping(25);

			continuousTreeToKML.setMaxBranchGreenMapping(25);

			continuousTreeToKML.setMaxBranchBlueMapping(25);

			continuousTreeToKML.setMaxBranchOpacityMapping(255);

			continuousTreeToKML.setBranchWidth(4);

			continuousTreeToKML.setMrsdString("2011-07-29 AD");

			continuousTreeToKML.setTimescaler(1);

			continuousTreeToKML.setNumberOfIntervals(100);

			continuousTreeToKML.setKmlWriterPath("/home/filip/output.kml");

			continuousTreeToKML
					.setTreePath("/home/filip/CougarFIV_SeqCoordinates.mcc.NEW.tre");

			continuousTreeToKML.GenerateKML();

			System.out.println("Finished in: " + continuousTreeToKML.time
					+ " msec \n");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}// END: ContinuousTreeToKMLTest

}// END: Main
