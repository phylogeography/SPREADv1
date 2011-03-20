package templates;


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
			
			timeSlicerToKML.setBurnIn(4990); // 4990

			timeSlicerToKML.setLocationAttName("location");

			timeSlicerToKML.setRateAttName("rate");

			timeSlicerToKML.setPrecisionAttName("precision");
			
			timeSlicerToKML.setTrueNoise(false);
			
			timeSlicerToKML.setMrsdString("2006-12-31");

			timeSlicerToKML.setNumberOfIntervals(100);

			timeSlicerToKML.setKmlWriterPath("/home/filip/Pulpit/output.kml");

			timeSlicerToKML.GenerateKML();


		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}
