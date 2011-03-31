package utils;

import processing.core.PApplet;

public class ReadLocations extends PApplet {

	private static final long serialVersionUID = 1L;
	String[] lines;
	public String[] locations;
	public float[][] coordinates;
	public int nrow;

	public ReadLocations(String filename) {

		lines = loadStrings(filename);
		nrow = lines.length;
		locations = new String[nrow];
		coordinates = new float[nrow][];

		for (int i = 0; i < nrow; i++) {
			String[] line = lines[i].split("\t");
			locations[i] = line[0];
			coordinates[i] = parseFloat(subset(line, 1));
		}
		coordinates = (float[][]) coordinates;
	}// END: ReadLocation

	public String[] getLocations() {
		return locations;
	}// END: getLocations

	public float getLongMin() {
		/* this should maybe specify which column is lat/long */
		float m = Float.MAX_VALUE;
		for (int row = 0; row < nrow; row++) {
			if (coordinates[row][0] < m) {
				m = coordinates[row][0];
			}
		}
		return m;
	}// END: getLongMin

	public float getLongMax() {
		/* this should maybe specify which column is lat/long */
		float m = -Float.MAX_VALUE;
		for (int row = 0; row < nrow; row++) {
			if (coordinates[row][0] > m) {
				m = coordinates[row][0];
			}
		}
		return m;
	}// END: getLongMax

	public float getLatMin() {
		/* this should maybe specify which column is lat/long */
		float m = Float.MAX_VALUE;
		for (int row = 0; row < nrow; row++) {
			if (coordinates[row][1] < m) {
				m = coordinates[row][1];
			}
		}
		return m;
	}// END: getLatMin

	public float getLatMax() {
		/* this should maybe specify which column is lat/long */
		float m = -Float.MAX_VALUE;
		for (int row = 0; row < nrow; row++) {
			if (coordinates[row][1] > m) {
				m = coordinates[row][1];
			}
		}
		return m;
	}// END: getLatMax

	int getNrow() {
		return nrow;
	}// END: getNrow

	public float getFloat(int row, int col) {
		return coordinates[row][col];
	}// END: getFloat

}// END: class
