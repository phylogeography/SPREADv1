package templates;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;
import utils.Utils;

@SuppressWarnings("serial")
public class ReadLog extends PApplet {

	public long time;
	public double[][] indicators;
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
		// skip first line with col names and the burn in lines
		int delete = (int) (nrow * burnIn) + 1;
		indicators = new double[nrow - delete][ncol];

		int i = 0;
		for (int row = delete; row < nrow; row++) {

			String[] line = lines[row].split("\t");
			indicators[i] = parseDouble(Utils.Subset(line, list.get(0), ncol));
			i++;
		}
		indicators = (double[][]) indicators;
		nrow = indicators.length;

		// stop timing
		time += System.currentTimeMillis();

	}// END: ReadLog

	private double[] parseDouble(String[] lines) {

		int nrow = lines.length;
		double[] a = new double[nrow];
		for (int i = 0; i < nrow; i++) {
			a[i] = Double.parseDouble(lines[i]);
		}
		return a;
	}


}// END: class
