package utils;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class ReadTimeSlices extends PApplet {

	private double[] timeSlices;
	private int nrow;

	public ReadTimeSlices(String filename) {

		String[] lines = loadStrings(filename);
		nrow = lines.length;

		timeSlices = new double[nrow];
		for (int i = 0; i < nrow; i++) {
			timeSlices[i] = Double.parseDouble(lines[i]);
		}

	}// END ReadTimeSlices

	public double[] getTimeSlices() {
		return timeSlices;
	}// END: getTimeSlices

}// END: class
