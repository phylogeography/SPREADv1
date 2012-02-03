package main;

import gui.InteractiveTableModel;
import gui.LocationCoordinatesEditor;
import gui.TableRecord;
import templates.RateIndicatorBFToKML;
import utils.ReadLocations;

public class RateIndicatorBFToKMLTest {

	static RateIndicatorBFToKML rateIndicatorBFToKML = new RateIndicatorBFToKML();
	private static InteractiveTableModel table;
	private static ReadLocations data;

	public static void main(String[] args) {

		System.out
				.println("Command line mode is experimental. Expect the unexpected.");

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

			rateIndicatorBFToKML.setTable(table);

			rateIndicatorBFToKML
					.setLogFilePath(
							"/home/filip/Phyleography/data/H5N1/H5N1_HA_discrete_rateMatrix.log",
							0.1);

			rateIndicatorBFToKML.setBfCutoff(3.0);

			rateIndicatorBFToKML.setMaxAltitudeMapping(50000);

			rateIndicatorBFToKML.setNumberOfIntervals(100);

			rateIndicatorBFToKML
					.setKmlWriterPath("/home/filip/Desktop/output.kml");

			rateIndicatorBFToKML.setMinBranchRedMapping(255);

			rateIndicatorBFToKML.setMinBranchGreenMapping(100);

			rateIndicatorBFToKML.setMinBranchBlueMapping(255);

			rateIndicatorBFToKML.setMinBranchOpacityMapping(255);

			rateIndicatorBFToKML.setMaxBranchRedMapping(25);

			rateIndicatorBFToKML.setMaxBranchGreenMapping(25);

			rateIndicatorBFToKML.setMaxBranchBlueMapping(25);

			rateIndicatorBFToKML.setMaxBranchOpacityMapping(255);

			rateIndicatorBFToKML.setBranchWidth(4);

			rateIndicatorBFToKML.GenerateKML();
			
			System.out.println("Finished in: " + RateIndicatorBFToKML.time
					+ " msec \n");
			
			// force quit
			System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// END: RateIndicatorBFToKMLTest

}// END: class
