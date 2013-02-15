package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class LogFileParser extends PApplet {

	private static final int HEADER_ROW = 0;
	
	public double[][] indicators;
	public int nrow;
	public int ncol;

	public LogFileParser(String filename, double burnIn, String indicatorName) {

		String[] lines = LoadStrings(filename);

		// count commented lines
//		int commentedLines = 0;
//		for (int row = 0; row < lines.length; row++) {
//			if (lines[row].startsWith("#")) {
//				commentedLines++;
//			}
//		}
//		nrow = lines.length - commentedLines - 1;
//		String[] colNames = lines[commentedLines].split("\t");

		nrow = lines.length - 1;
		String[] colNames = lines[HEADER_ROW].split("\t");		
		
		List<Integer> list = new ArrayList<Integer>();

		// Create a pattern to match
		Pattern pattern = Pattern.compile(indicatorName);
		for (int row = 0; row < colNames.length; row++) {

			// Look for matches in column names
			Matcher matcher = pattern.matcher(colNames[row]);

			if (matcher.find()) {
				list.add(row);
			}
		}
		
//		if (list.size() == 0) {
//			// did not find any indicator columns, try "rateIndicator." instead
//			pattern = Pattern.compile("rateIndicator");
//			for (int row = 0; row < colNames.length; row++) {
//
//				// Look for matches in column names
//				Matcher matcher = pattern.matcher(colNames[row]);
//
//				if (matcher.find()) {
//					list.add(row);
//				}
//			}
//		}

		ncol = list.size();
		// skip first line with col names and the burn in lines
		int delete = (int) (nrow * burnIn) + 1;
		indicators = new double[nrow - delete][ncol];

		int i = 0;
		for (int row = delete; row < nrow; row++) {

			String[] line = lines[row].split("\t");
			indicators[i] = Utils.parseDouble(Utils.subset(line, list.get(0),
					ncol));
			i++;

		}
		indicators = (double[][]) indicators;
		nrow = indicators.length;

	}// END: ReadLog

	private String[] LoadStrings(String filename) {
		InputStream is = createInput(filename);
		if (is != null)
			return LoadStrings(is);

		System.err.println("The file \"" + filename + "\" "
				+ "is missing or inaccessible, make sure "
				+ "the URL is valid or that the file is readable.");
		return null;
	}

	private String[] LoadStrings(InputStream input) {

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, "UTF-8"));

			String lines[] = new String[100];
			int lineCount = 0;
			String line = null;
			while (((line = reader.readLine()) != null)) {

				if (lineCount == lines.length) {

					String temp[] = new String[lineCount << 1];

					System.arraycopy(lines, 0, temp, 0, lineCount);
					lines = temp;
				}
				// suppress commented lines
				if (!line.startsWith("#")) {
					lines[lineCount++] = line;
				}
			}
			reader.close();

			if (lineCount == lines.length) {
				return lines;
			}

			// resize array to appropriate amount for these lines
			String output[] = new String[lineCount];
			System.arraycopy(lines, 0, output, 0, lineCount);

			return output;

		} catch (IOException e) {
			e.printStackTrace();
			// throw new RuntimeException("Error inside loadStrings()");
		}
		return null;

	}

}// END: class
