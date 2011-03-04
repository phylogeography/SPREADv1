package templates;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import jebl.evolution.io.ImportException;

public class Main {

	private static TimeSlicerToKML timeSlicerToKML = new TimeSlicerToKML();

	public static void main(String[] args) {

		try {

			timeSlicerToKML.setMccTreePath(

			"/home/filip/Dropbox/Phyleography/data/WNX/WNV_relaxed_geo_gamma_MCC.tre"

			);

			timeSlicerToKML.setTreesPath(

			"/home/filip/Dropbox/Phyleography/data/WNX/WNV_relaxed_geo_gamma.trees"

			);

			timeSlicerToKML.setMrsdString("2006-12-31");

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");

			timeSlicerToKML.setNumberOfIntervals(10);

			timeSlicerToKML.setBurnIn(0.998); // 0.998

			timeSlicerToKML.setKmlWriterPath("/home/filip/Pulpit/output.kml");

			timeSlicerToKML.GenerateKML();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (ImportException e) {
			e.printStackTrace();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
