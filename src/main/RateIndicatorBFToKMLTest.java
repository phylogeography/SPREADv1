package main;

import gui.InteractiveTableModel;
import gui.LocationCoordinatesEditor;
import gui.TableRecord;
import readers.locationsReader;
import templates.RateIndicatorBFToKML;

public class RateIndicatorBFToKMLTest {

	static RateIndicatorBFToKML rateIndicatorBFToKML = new RateIndicatorBFToKML();
	private static InteractiveTableModel table;
	private static locationsReader data;

	public static void main(String[] args) {

		System.out
				.println("Command line mode is experimental. Expect the unexpected.");

		try {

			table = new InteractiveTableModel(new LocationCoordinatesEditor(null)
					.getColumnNames());
			data = new locationsReader(
					"/home/filip/Phyleography/data/H5N1/locationCoordinates_H5N1");

			String indicatorAttributeName = "indicator";
			
			for (int i = 0; i < data.nrow; i++) {

				String name = String.valueOf(data.locations[i]);
				String longitude = String.valueOf(data.coordinates[i][0]);
				String latitude = String.valueOf(data.coordinates[i][1]);
				table.insertRow(i, new TableRecord(name, longitude, latitude));

			}// END: row loop

			rateIndicatorBFToKML.setTable(table);

			rateIndicatorBFToKML
					.setLogFileParser(
							"/home/filip/Phyleography/data/H5N1/H5N1_HA_discrete_rateMatrix.log",
							0.1, indicatorAttributeName);

			rateIndicatorBFToKML.setBfCutoff(3.0);

			rateIndicatorBFToKML.setMaxAltitudeMapping(50000);

			rateIndicatorBFToKML.setNumberOfIntervals(100);

			rateIndicatorBFToKML.setDefaultMeanPoissonPrior();
			
			rateIndicatorBFToKML.setDefaultPoissonPriorOffset();
			
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
