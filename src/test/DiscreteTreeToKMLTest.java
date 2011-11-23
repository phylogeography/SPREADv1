package test;

import gui.InteractiveTableModel;
import gui.LocationCoordinatesEditor;
import gui.TableRecord;
import templates.DiscreteTreeToKML;
import utils.ReadLocations;

public class DiscreteTreeToKMLTest {

	private static DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();
	private static InteractiveTableModel table;
	private static ReadLocations data;

	public static void main(String[] args) {

		System.out
				.println("Command line mode is experimental. Expect the unexpected.");

		try {

			table = new InteractiveTableModel(new LocationCoordinatesEditor()
					.getColumnNames());
			data = new ReadLocations(
					"/home/filip/HP_locations");

			for (int i = 0; i < data.nrow; i++) {

				String name = String.valueOf(data.locations[i]);
				String longitude = String.valueOf(data.coordinates[i][0]);
				String latitude = String.valueOf(data.coordinates[i][1]);
				table.insertRow(i, new TableRecord(name, longitude, latitude));

			}// END: row loop

			discreteTreeToKML
					.setTreePath("/home/filip/HP_mtDNA.tree");

			discreteTreeToKML.setTimescaler(1);
			
			discreteTreeToKML.setMrsdString("2011-07-28 AD");

			discreteTreeToKML.setTable(table);

			discreteTreeToKML.setStateAttName("states");

			discreteTreeToKML.setMaxAltitudeMapping(5000000);

			discreteTreeToKML.setNumberOfIntervals(100);

			discreteTreeToKML.setPolygonsRadiusMultiplier(1);
			
			discreteTreeToKML.setMinPolygonRedMapping(0);

			discreteTreeToKML.setMinPolygonGreenMapping(0);

			discreteTreeToKML.setMinPolygonBlueMapping(0);

			discreteTreeToKML.setMinPolygonOpacityMapping(100);

			discreteTreeToKML.setMaxPolygonRedMapping(50);

			discreteTreeToKML.setMaxPolygonGreenMapping(255);

			discreteTreeToKML.setMaxPolygonBlueMapping(255);

			discreteTreeToKML.setMaxPolygonOpacityMapping(255);

			discreteTreeToKML.setMinBranchRedMapping(0);

			discreteTreeToKML.setMinBranchGreenMapping(0);

			discreteTreeToKML.setMinBranchBlueMapping(0);

			discreteTreeToKML.setMinBranchOpacityMapping(255);

			discreteTreeToKML.setMaxBranchRedMapping(255);

			discreteTreeToKML.setMaxBranchGreenMapping(5);

			discreteTreeToKML.setMaxBranchBlueMapping(50);

			discreteTreeToKML.setMaxBranchOpacityMapping(255);

			discreteTreeToKML.setBranchWidth(4);

			discreteTreeToKML.setKmlWriterPath("/home/filip/output.kml");

			discreteTreeToKML.GenerateKML();

			System.out.println("Finished in: " + discreteTreeToKML.time
					+ " msec \n");

			// force quit
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: DiscreteTreeToKMLTest

}// END: class
