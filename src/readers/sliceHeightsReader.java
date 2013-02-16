package readers;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class sliceHeightsReader extends PApplet {

	private double[] sliceHeights;
	private int nrow;

	public sliceHeightsReader(String filename) {

		String[] lines = loadStrings(filename);
		nrow = lines.length;

		sliceHeights = new double[nrow];
		for (int i = 0; i < nrow; i++) {
			sliceHeights[i] = Double.parseDouble(lines[i]);
		}

	}// END: constructor

	public double[] getSliceHeights() {
		return sliceHeights;
	}// END: getTimeSlices

}// END: class
