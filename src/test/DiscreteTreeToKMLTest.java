package test;

import gui.InteractiveTableModel;
import gui.LocationCoordinatesEditor;
import gui.TableRecord;
import templates.DiscreteTreeToKML;
import utils.ReadLocations;

public class DiscreteTreeToKMLTest {

	private DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();
	private InteractiveTableModel table;
	private ReadLocations data;

	public DiscreteTreeToKMLTest() {

		System.out.println("Command line mode is experimental. Expect the unexpected.");
		
		try {

			table = new InteractiveTableModel(new LocationCoordinatesEditor()
					.getColumnNames());
			data = new ReadLocations(
					"/home/filip/Phyleography/data/H5N1/locationCoordinates_H5N1");

			for (int i = 0; i < data.nrow; i++) {

				String name = String.valueOf(data.locations[i]);
				String longitude = String.valueOf(data.coordinates[i][0]);
				String latitude = String.valueOf(data.coordinates[i][1]);
				table.insertRow(i, new TableRecord(name, longitude, latitude));

			}// END: row loop

			// table.printTable();

			discreteTreeToKML
					.setTreePath("/home/filip/Phyleography/data/H5N1/H5N1_HA_discrete_MCC.tre");

			discreteTreeToKML.setMrsdString("2011-07-28 AD");

			discreteTreeToKML.setTable(table);

			discreteTreeToKML.setStateAttName("states");

			discreteTreeToKML.setMaxAltitudeMapping(50000);

			discreteTreeToKML.setTimescaler(1);

			discreteTreeToKML.setNumberOfIntervals(100);

			discreteTreeToKML.setMinPolygonRedMapping(100);

			discreteTreeToKML.setMinPolygonGreenMapping(255);

			discreteTreeToKML.setMinPolygonBlueMapping(255);

			discreteTreeToKML.setMinPolygonOpacityMapping(255);

			discreteTreeToKML.setMaxPolygonRedMapping(255);

			discreteTreeToKML.setMaxPolygonGreenMapping(255);

			discreteTreeToKML.setMaxPolygonBlueMapping(25);

			discreteTreeToKML.setMaxPolygonOpacityMapping(255);

			discreteTreeToKML.setMinBranchRedMapping(255);

			discreteTreeToKML.setMinBranchGreenMapping(100);

			discreteTreeToKML.setMinBranchBlueMapping(255);

			discreteTreeToKML.setMinBranchOpacityMapping(255);

			discreteTreeToKML.setMaxBranchRedMapping(25);

			discreteTreeToKML.setMaxBranchGreenMapping(25);

			discreteTreeToKML.setMaxBranchBlueMapping(25);

			discreteTreeToKML.setMaxBranchOpacityMapping(255);

			discreteTreeToKML.setBranchWidth(4);

			discreteTreeToKML.setKmlWriterPath("/home/filip/Pulpit/output.kml");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: DiscreteTreeToKMLTest

	public void generate() {

		try {

			discreteTreeToKML.GenerateKML();
			System.out.println("Finished in: " + discreteTreeToKML.time
					+ " msec \n");
			// force quit
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: Generate

}// END: class
