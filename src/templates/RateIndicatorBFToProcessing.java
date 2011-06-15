package templates;

import gui.InteractiveTableModel;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import utils.GeoIntermediate;
import utils.ReadLog;
import utils.Utils;

@SuppressWarnings("serial")
public class RateIndicatorBFToProcessing extends PApplet {

	private InteractiveTableModel table;
	private ReadLog indicators;
	private double bfCutoff;
	private List<Double> bayesFactors;
	private List<String> combin;
	private MapBackground mapBackground;
	// private int numberOfIntervals;

	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double branchWidth;
	private double meanPoissonPrior;
	private double poissonPriorOffset;

	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

	private enum PoissonPriorEnum {
		DEFAULT, USER
	}

	private PoissonPriorEnum poissonPriorOffsetSwitcher;
	private PoissonPriorEnum meanPoissonPriorSwitcher;

	public RateIndicatorBFToProcessing() {
	}// END: RateIndicatorBFToProcessing()

	// public void setNumberOfIntervals(int number) {
	// numberOfIntervals = number;
	// }

	public void setDefaultPoissonPriorOffset() {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserPoissonPriorOffset(double offset) {
		poissonPriorOffsetSwitcher = PoissonPriorEnum.USER;
		poissonPriorOffset = offset;
	}

	public void setDefaultMeanPoissonPrior() {
		meanPoissonPriorSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserMeanPoissonPrior(double mean) {
		meanPoissonPriorSwitcher = PoissonPriorEnum.USER;
		meanPoissonPrior = mean;
	}

	public void setBfCutoff(double cutoff) {
		bfCutoff = cutoff;
	}

	public void setTable(InteractiveTableModel tableModel) {
		table = tableModel;
	}

	public void setLogFilePath(String path, double burnIn) {
		indicators = new ReadLog(path, burnIn);
	}

	public void setMinBranchRedMapping(double min) {
		minBranchRedMapping = min;
	}

	public void setMinBranchGreenMapping(double min) {
		minBranchGreenMapping = min;
	}

	public void setMinBranchBlueMapping(double min) {
		minBranchBlueMapping = min;
	}

	public void setMinBranchOpacityMapping(double min) {
		minBranchOpacityMapping = min;
	}

	public void setMaxBranchRedMapping(double max) {
		maxBranchRedMapping = max;
	}

	public void setMaxBranchGreenMapping(double max) {
		maxBranchGreenMapping = max;
	}

	public void setMaxBranchBlueMapping(double max) {
		maxBranchBlueMapping = max;
	}

	public void setMaxBranchOpacityMapping(double max) {
		maxBranchOpacityMapping = max;
	}

	public void setBranchWidth(double width) {
		branchWidth = width;
	}

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -90;
		maxY = 90;

		// will improve font rendering speed with default renderer
		hint(ENABLE_NATIVE_FONTS);
		PFont plotFont = createFont("Monaco", 12);
		textFont(plotFont);

		mapBackground = new MapBackground(this);

	}// END: setup

	public void draw() {

		noLoop();
		smooth();
		ComputeBFTest();
		mapBackground.drawMapBackground();
		DrawPlaces();
		DrawRates();
		// DrawRateSlices();
		DrawPlacesLabels();

	}// END:draw

	// //////////////
	// ---PLACES---//
	// //////////////
	private void DrawPlaces() {

		float radius = 7;

		// White
		fill(255, 255, 255);
		noStroke();

		for (int row = 0; row < table.getRowCount(); row++) {

			Float longitude = Float.valueOf(String.valueOf(table.getValueAt(
					row, 2)));

			Float latitude = Float.valueOf(String.valueOf(table.getValueAt(row,
					1)));

			// Equirectangular projection:
			float X = map(longitude, minX, maxX, 0, width);
			float Y = map(latitude, maxY, minY, 0, height);

			ellipse(X, Y, radius, radius);

		}
	}// END DrawPlaces

	private void DrawPlacesLabels() {

		textSize(7);

		// Black labels
		fill(0, 0, 0);

		for (int row = 0; row < table.getRowCount(); row++) {

			String name = String.valueOf(table.getValueAt(row, 0));

			Float longitude = Float.valueOf(String.valueOf(table.getValueAt(
					row, 2)));

			Float latitude = Float.valueOf(String.valueOf(table.getValueAt(row,
					1)));

			float X = map(longitude, minX, maxX, 0, width);
			float Y = map(latitude, maxY, minY, 0, height);

			text(name, X, Y);
		}
	}// END: DrawPlacesLabels

	private void DrawRates() {

		System.out.println("BF cutoff = " + bfCutoff);
		System.out.println("mean Poisson Prior = " + meanPoissonPrior);
		System.out.println("Poisson Prior offset = " + poissonPriorOffset);

		strokeWeight((float) branchWidth);

		float bfMax = (float) Math.log(Utils.getListMax(bayesFactors));

		for (int i = 0; i < combin.size(); i++) {

			if (bayesFactors.get(i) > bfCutoff) {

				/**
				 * Color mapping
				 * */
				float bf = (float) Math.log(bayesFactors.get(i));

				int red = (int) Utils.map(bf, 0, bfMax, minBranchRedMapping,
						maxBranchRedMapping);

				int green = (int) Utils.map(bf, 0, bfMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(bf, 0, bfMax, minBranchBlueMapping,
						maxBranchBlueMapping);

				int alpha = (int) Utils.map(bf, 0, bfMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);

				String state = combin.get(i).split(":")[1];
				String parentState = combin.get(i).split(":")[0];

				float longitude = Utils.matchStateCoordinate(table, state, 2);
				float latitude = Utils.matchStateCoordinate(table, state, 1);

				float parentLongitude = Utils.matchStateCoordinate(table,
						parentState, 2);
				float parentLatitude = Utils.matchStateCoordinate(table,
						parentState, 1);

				float x0 = map(parentLongitude, minX, maxX, 0, width);
				float y0 = map(parentLatitude, maxY, minY, 0, height);

				float x1 = map(longitude, minX, maxX, 0, width);
				float y1 = map(latitude, maxY, minY, 0, height);

				line(x0, y0, x1, y1);

				System.out.println("BF=" + bayesFactors.get(i) + " : between "
						+ parentState + " (long: " + parentLongitude
						+ "; lat: " + parentLatitude + ") and " + state
						+ " (long: " + longitude + "; lat: " + latitude + ")");
			}
		}// END: ArrayList loop
	}// END: DrawRates

	@SuppressWarnings("unused")
	private void DrawRateSlices() {

		System.out.println("BF cutoff = " + bfCutoff);
		System.out.println("mean Poisson Prior = " + meanPoissonPrior);
		System.out.println("Poisson Prior offset = " + poissonPriorOffset);

		strokeWeight((float) branchWidth);

		float bfMax = (float) Math.log(Utils.getListMax(bayesFactors));

		for (int i = 0; i < combin.size(); i++) {

			if (bayesFactors.get(i) > bfCutoff) {

				/**
				 * Color mapping
				 * */
				float bf = (float) Math.log(bayesFactors.get(i));

				int red = (int) Utils.map(bf, 0, bfMax, minBranchRedMapping,
						maxBranchRedMapping);

				int green = (int) Utils.map(bf, 0, bfMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(bf, 0, bfMax, minBranchBlueMapping,
						maxBranchBlueMapping);

				int alpha = (int) Utils.map(bf, 0, bfMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);

				String state = combin.get(i).split(":")[1];
				String parentState = combin.get(i).split(":")[0];

				float longitude = Utils.matchStateCoordinate(table, state, 2);
				float latitude = Utils.matchStateCoordinate(table, state, 1);

				float parentLongitude = Utils.matchStateCoordinate(table,
						parentState, 2);
				float parentLatitude = Utils.matchStateCoordinate(table,
						parentState, 1);

				GeoIntermediate rhumbIntermediate = new GeoIntermediate(
						parentLongitude, parentLatitude, longitude, latitude,
						100);
				double[][] coords = rhumbIntermediate.getCoords();

				for (int row = 0; row < coords.length - 1; row++) {

					float x0 = map((float) coords[row][0], minX, maxX, 0, width);
					float y0 = map((float) coords[row][1], maxY, minY, 0,
							height);

					float x1 = map((float) coords[row + 1][0], minX, maxX, 0,
							width);
					float y1 = map((float) coords[row + 1][1], maxY, minY, 0,
							height);

					line(x0, y0, x1, y1);

				}// numberOfIntervals loop

				System.out.println("BF=" + bayesFactors.get(i) + " : between "
						+ parentState + " (long: " + parentLongitude
						+ "; lat: " + parentLatitude + ") and " + state
						+ " (long: " + longitude + "; lat: " + latitude + ")");
			}
		}// END: ArrayList loop
	}// END: DrawRatesSlices

	private void ComputeBFTest() {

		int n = table.getRowCount();

		switch (meanPoissonPriorSwitcher) {
		case DEFAULT:
			meanPoissonPrior = Math.log(2);
			break;
		case USER:
			break;
		}

		switch (poissonPriorOffsetSwitcher) {
		case DEFAULT:
			poissonPriorOffset = n - 1;
			break;
		case USER:
			break;
		}

		boolean symmetrical = false;
		if (indicators.ncol == n * (n - 1)) {
			symmetrical = false;
		} else if (indicators.ncol == (n * (n - 1)) / 2) {
			symmetrical = true;
		} else {
			throw new RuntimeException(
					"the number of rate indicators does not match the number of locations!");
		}

		combin = new ArrayList<String>();
		String[] locations = table.getColumn(0);

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.subset(locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations[row] + ":" + subset[i]);
			}
		}

		if (symmetrical == false) {
			combin.addAll(combin);
		}

		double qk = Double.NaN;
		if (symmetrical) {
			qk = (meanPoissonPrior + poissonPriorOffset) / ((n * (n - 1)) / 2);
		} else {
			qk = (meanPoissonPrior + poissonPriorOffset) / ((n * (n - 1)) / 1);
		}

		double[] pk = Utils.colMeans(indicators.indicators);

		bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {
			double bf = (pk[row] / (1 - pk[row])) / denominator;
			bayesFactors.add(bf);
		}
	}// END: ComputeBFTest

}// END: RateIndicatorBFToProcessing class
