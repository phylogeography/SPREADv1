package templates;

import gui.InteractiveTableModel;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import utils.GeoIntermediate;
import utils.Holder;
import utils.ReadLog;
import utils.Utils;
import utils.Utils.PoissonPriorEnum;

@SuppressWarnings("serial")
public class RateIndicatorBFToProcessing extends PApplet {

	private InteractiveTableModel table;
	private ReadLog indicators;
	private double bfCutoff;
	private MapBackground mapBackground;
	private ArrayList<Double> bayesFactors;
	private ArrayList<String> combin;
	private BayesFactorTest bfTest;
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
	private Holder meanPoissonPrior = new Holder(0.0);
	private Holder poissonPriorOffset = new Holder(0.0);

	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

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
		poissonPriorOffset.value = offset;
	}

	public void setDefaultMeanPoissonPrior() {
		meanPoissonPriorSwitcher = PoissonPriorEnum.DEFAULT;
	}

	public void setUserMeanPoissonPrior(double mean) {
		meanPoissonPriorSwitcher = PoissonPriorEnum.USER;
		meanPoissonPrior.value = mean;
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
		computeBFTest();
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
		System.out.println("mean Poisson Prior = " + meanPoissonPrior.value);
		System.out
				.println("Poisson Prior offset = " + poissonPriorOffset.value);

		strokeWeight((float) branchWidth);

		float bfMax = (float) Math.log(Utils.getListMax(bayesFactors));
		int index = 0;
		
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

				System.out.println(index + "\t" + "BF=" + bayesFactors.get(i) + " : between "
						+ parentState + " (long: " + parentLongitude
						+ "; lat: " + parentLatitude + ") and " + state
						+ " (long: " + longitude + "; lat: " + latitude + ")");
				index++;
				
			}// END: cutoff check
		}// END: ArrayList loop
	}// END: DrawRates

	@SuppressWarnings("unused")
	private void DrawRateSlices() {

		System.out.println("BF cutoff = " + bfCutoff);
		System.out.println("mean Poisson Prior = " + meanPoissonPrior.value);
		System.out.println("Poisson Prior offset = " + poissonPriorOffset.value);

		strokeWeight((float) branchWidth);

		Integer[] sortOrder = bfTest.getSortOrder();
		float bfMax = (float) Math.log(Utils.getListMax(bayesFactors));
		int index = 0;
		
		for (int i = 0; i < combin.size(); i++) {

			if (bayesFactors.get(sortOrder[i]) > bfCutoff) {

				/**
				 * Color mapping
				 * */
				float bf = (float) Math.log(bayesFactors.get(sortOrder[i]));

				int red = (int) Utils.map(bf, 0, bfMax, minBranchRedMapping,
						maxBranchRedMapping);

				int green = (int) Utils.map(bf, 0, bfMax,
						minBranchGreenMapping, maxBranchGreenMapping);

				int blue = (int) Utils.map(bf, 0, bfMax, minBranchBlueMapping,
						maxBranchBlueMapping);

				int alpha = (int) Utils.map(bf, 0, bfMax,
						maxBranchOpacityMapping, minBranchOpacityMapping);

				stroke(red, green, blue, alpha);

				String state = combin.get(sortOrder[i]).split(":")[1];
				String parentState = combin.get(sortOrder[i]).split(":")[0];

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

				}// END: numberOfIntervals loop

				System.out.println(index + "\t" + " BF=" + bayesFactors.get(sortOrder[i]) + " : between "
						+ parentState + " (long: " + parentLongitude
						+ "; lat: " + parentLatitude + ") and " + state
						+ " (long: " + longitude + "; lat: " + latitude + ")");
				index++;
				
			}// END: cutoff check
		}// END: ArrayList loop
	}// END: DrawRatesSlices

	private void computeBFTest() {

		combin = new ArrayList<String>();
		bayesFactors = new ArrayList<Double>();

		bfTest = new BayesFactorTest(table, meanPoissonPriorSwitcher, meanPoissonPrior,
				poissonPriorOffsetSwitcher, poissonPriorOffset, indicators,
				combin, bayesFactors);
		
		bfTest.ComputeBFTest();

	}// END: computeBFTest

}// END: RateIndicatorBFToProcessing class
