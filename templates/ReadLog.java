package templates;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class ReadLog extends PApplet {

	public long time;
	public float[][] indicators;
	public int nrow;
	public int ncol;

	public ReadLog(String filename, double burnIn) {

		// start timing
		time = -System.currentTimeMillis();

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
		int delete = (int) (nrow * burnIn) + 1;
		indicators = new float[nrow - delete][ncol];

		int i = 0;
		for (int row = delete; row < nrow; row++) {

			// skip first line with col names
			String[] line = lines[row].split("\t");
			indicators[i] = parseFloat(subset(line, list.get(0), ncol));
			i++;
		}
		indicators = (float[][]) indicators;
		nrow = indicators.length;

		// stop timing
		time += System.currentTimeMillis();

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
