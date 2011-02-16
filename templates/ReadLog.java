package templates;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class ReadLog extends PApplet {

	public float[][] indicators;
	public int nrow;
	public int ncol;

	public ReadLog(String filename) {

		String[] lines = loadStrings(filename);
		nrow = lines.length - 1;
		String[] colNames = lines[0].split("\t");

		List<Integer> list = new ArrayList<Integer>();

		// Create a pattern to match
		Pattern pattern = Pattern.compile("indicators");
		for (int row = 0; row < colNames.length; row++) {

			// Look for matches in column names
			Matcher matcher = pattern.matcher(colNames[row]);

			if (matcher.find()) {
				list.add(row);
			}
		}

		ncol = list.size();
		indicators = new float[nrow][ncol];
		for (int i = 0; i < nrow; i++) {

			// skip first line with col names
			String[] line = lines[i + 1].split("\t");
			indicators[i] = parseFloat(subset(line, list.get(0), ncol));
		}
		indicators = (float[][]) indicators;

	}// END: ReadLog

	int getNrow() {
		return nrow;
	}// END: getNrow

	public float getFloat(int row, int col) {
		return indicators[row][col];
	}// END: getFloat

	public int getInt(int row, int col) {
		return (int) indicators[row][col];
	}// END: getFloat

	public float[][] getIndicators() {

		return indicators;
	}

}// END: class
