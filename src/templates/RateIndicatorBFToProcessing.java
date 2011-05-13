package templates;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import utils.ReadLocations;
import utils.ReadLog;
import utils.Utils;

@SuppressWarnings("serial")
public class RateIndicatorBFToProcessing extends PApplet {

	private ReadLocations locations;
	private ReadLog indicators;
	private double bfCutoff;
	private List<Double> bayesFactors;
	private List<String> combin;
	private MapBackground mapBackground;

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

	public void setLocationFilePath(String path) throws ParseException {
		locations = new ReadLocations(path);
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

		for (int row = 0; row < locations.nrow; row++) {

			// Equirectangular projection:
			float X = map(locations.getFloat(row, 1), minX, maxX, 0, width);
			float Y = map(locations.getFloat(row, 0), maxY, minY, 0, height);

			ellipse(X, Y, radius, radius);

		}
	}// END DrawPlaces

	private void DrawPlacesLabels() {

		textSize(7);

		// Black labels
		fill(0, 0, 0);

		for (int row = 0; row < locations.nrow; row++) {

			String name = locations.locations[row];
			float X = map(locations.getFloat(row, 1), minX, maxX, 0, width);
			float Y = map(locations.getFloat(row, 0), maxY, minY, 0, height);

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

				float longitude = Utils.MatchStateCoordinate(locations, state,
						1);
				float latitude = Utils
						.MatchStateCoordinate(locations, state, 0);

				float parentLongitude = Utils.MatchStateCoordinate(locations,
						parentState, 1);
				float parentLatitude = Utils.MatchStateCoordinate(locations,
						parentState, 0);

				//

				float x0 = map(parentLongitude, minX, maxX, 0, width);
				float y0 = map(parentLatitude, maxY, minY, 0, height);

				float x1 = map(longitude, minX, maxX, 0, width);
				float y1 = map(latitude, maxY, minY, 0, height);

				line(x0, y0, x1, y1);

				//

				System.out.println("BF=" + bayesFactors.get(i) + " : between "
						+ parentState + " (long: " + parentLongitude
						+ "; lat: " + parentLatitude + ") and " + state
						+ " (long: " + longitude + "; lat: " + latitude + ")");
			}
		}// END: ArrayList loop
	}// END: DrawRates

	private void ComputeBFTest() {

		int n = locations.nrow;

		switch (meanPoissonPriorSwitcher) {
		case DEFAULT:
			meanPoissonPrior = Math.log(2);
			break;
		case USER:
			break;
		}

		switch (poissonPriorOffsetSwitcher) {
		case DEFAULT:
			poissonPriorOffset = locations.nrow - 1;
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

		for (int row = 0; row < n - 1; row++) {

			String[] subset = Utils.Subset(locations.locations, row, n - row);

			for (int i = 1; i < subset.length; i++) {
				combin.add(locations.locations[row] + ":" + subset[i]);
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

		double[] pk = Utils.ColMeans(indicators.indicators);

		bayesFactors = new ArrayList<Double>();
		double denominator = qk / (1 - qk);

		for (int row = 0; row < pk.length; row++) {
			double bf = (pk[row] / (1 - pk[row])) / denominator;
			bayesFactors.add(bf);
		}
	}// END: ComputeBFTest

}// END: RateIndicatorBFToProcessing class
