package test.templates;

import org.junit.Test;

import junit.framework.TestCase;
import gui.InteractiveTableModel;
import gui.LocationCoordinatesEditor;
import gui.TableRecord;
import readers.LocationsReader;
import templates.DiscreteTreeToKML;

public class DiscreteTreeToKMLTest extends TestCase {


	@Test
	public void testDiscreteTreeToKML() throws Exception {
		DiscreteTreeToKML discreteTreeToKML = new DiscreteTreeToKML();
		InteractiveTableModel table;
		LocationsReader data;

		System.out
				.println("Command line mode is experimental. Expect the unexpected.");


			table = new InteractiveTableModel(new LocationCoordinatesEditor(null)
					.getColumnNames());
			data = new LocationsReader(
					TestUtils.getResourcePath("/data/locationCoordinates_H5N1"));
					

			for (int i = 0; i < data.nrow; i++) {

				String name = String.valueOf(data.locations[i]);
				String longitude = String.valueOf(data.coordinates[i][0]);
				String latitude = String.valueOf(data.coordinates[i][1]);
				table.insertRow(i, new TableRecord(name, longitude, latitude));

			}// END: row loop

			discreteTreeToKML
				.setTreePath(TestUtils.getResourcePath("/data/H5N1_HA_discrete_MCC.tre"));

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

			discreteTreeToKML.setKmlWriterPath("output.kml");

			discreteTreeToKML.GenerateKML();

			System.out.println("Finished in: " + discreteTreeToKML.time
					+ " msec \n");

			// force quit
			//System.exit(0);

	}// END: DiscreteTreeToKMLTest
	
}// END: class
