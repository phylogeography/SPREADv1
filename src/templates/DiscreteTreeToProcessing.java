package templates;

import gui.InteractiveTableModel;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import processing.core.PApplet;
import processing.core.PFont;
import structure.Coordinates;
import utils.Utils;

@SuppressWarnings("serial")
public class DiscreteTreeToProcessing extends PApplet {

	public long time;

	private TreeImporter importer;
	private RootedTree tree;
	private InteractiveTableModel table;
	private String stateAttName;
	private int numberOfIntervals;
	private double rootHeight;

	private MapBackground mapBackground;

	private double minBranchRedMapping;
	private double minBranchGreenMapping;
	private double minBranchBlueMapping;
	private double minBranchOpacityMapping;

	private double maxBranchRedMapping;
	private double maxBranchGreenMapping;
	private double maxBranchBlueMapping;
	private double maxBranchOpacityMapping;

	private double minPolygonRedMapping;
	private double minPolygonGreenMapping;
	private double minPolygonBlueMapping;
	private double minPolygonOpacityMapping;

	private double maxPolygonRedMapping;
	private double maxPolygonGreenMapping;
	private double maxPolygonBlueMapping;
	private double maxPolygonOpacityMapping;

	private double branchWidth;
	private double polygonsRadiusMultiplier;
	private Random generator;

	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

	public DiscreteTreeToProcessing() {

	}// END: DiscreteTreeToProcessing

	public void setStateAttName(String name) {
		stateAttName = name;
	}

	public void setNumberOfIntervals(int number) {
		numberOfIntervals = number;
	}

	public void setTreePath(String path) throws IOException, ImportException {
		importer = new NexusImporter(new FileReader(path));
	}

	public void setTable(InteractiveTableModel tableModel) {
		table = tableModel;
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

	public void setMinPolygonRedMapping(double min) {
		minPolygonRedMapping = min;
	}

	public void setMinPolygonGreenMapping(double min) {
		minPolygonGreenMapping = min;
	}

	public void setMinPolygonBlueMapping(double min) {
		minPolygonBlueMapping = min;
	}

	public void setMinPolygonOpacityMapping(double min) {
		minPolygonOpacityMapping = min;
	}

	public void setMaxPolygonRedMapping(double max) {
		maxPolygonRedMapping = max;
	}

	public void setMaxPolygonGreenMapping(double max) {
		maxPolygonGreenMapping = max;
	}

	public void setMaxPolygonBlueMapping(double max) {
		maxPolygonBlueMapping = max;
	}

	public void setMaxPolygonOpacityMapping(double max) {
		maxPolygonOpacityMapping = max;
	}

	public void setPolygonsRadiusMultiplier(double multiplier) {
		polygonsRadiusMultiplier = multiplier;
	}

	public void setBranchWidth(double width) {
		branchWidth = width;
	}

	public void setup() {

		try {

			minX = -180;
			maxX = 180;

			minY = -90;
			maxY = 90;

			generator = new Random();

			// will improve font rendering speed with default renderer
			hint(ENABLE_NATIVE_FONTS);
			PFont plotFont = createFont("Monaco", 12);
			textFont(plotFont);

			tree = (RootedTree) importer.importNextTree();
			// this is for time calculations
			rootHeight = tree.getHeight(tree.getRootNode());

			mapBackground = new MapBackground(this);

		} catch (IOException e) {
			e.printStackTrace();

		} catch (ImportException e) {
			e.printStackTrace();
		}

	}// END: setup

	public void draw() {

		// start timing
		time = -System.currentTimeMillis();

		noLoop();
		smooth();
		mapBackground.drawMapBackground();
		drawCircles();
		drawPlaces();
		drawBranches();
		drawPlacesLabels();

		// stop timing
		time += System.currentTimeMillis();

	}// END:draw

	// //////////////
	// ---PLACES---//
	// //////////////
	private void drawPlaces() {

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

	private void drawPlacesLabels() {

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

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private void drawBranches() {

		strokeWeight((float) branchWidth);

		double treeHeightMax = Utils.getTreeHeightMax(tree);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String state = getRandomState((String) node
						.getAttribute(stateAttName), true);

				Node parentNode = tree.getParent(node);

				String parentState = getRandomState((String) parentNode
						.getAttribute(stateAttName), false);

				if (state != null && parentState != null) {

					if (!state.toLowerCase().equals(parentState.toLowerCase())) {

						float longitude = Utils.matchStateCoordinate(table,
								state, 2);
						float latitude = Utils.matchStateCoordinate(table,
								state, 1);

						float parentLongitude = Utils.matchStateCoordinate(
								table, parentState, 2);
						float parentLatitude = Utils.matchStateCoordinate(
								table, parentState, 1);

						float x0 = map(parentLongitude, minX, maxX, 0, width);
						float y0 = map(parentLatitude, maxY, minY, 0, height);

						float x1 = map(longitude, minX, maxX, 0, width);
						float y1 = map(latitude, maxY, minY, 0, height);

						/**
						 * Color mapping
						 * */
						double nodeHeight = tree.getHeight(node);

						int red = (int) Utils.map(nodeHeight, 0, treeHeightMax,
								minBranchRedMapping, maxBranchRedMapping);

						int green = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minBranchGreenMapping,
								maxBranchGreenMapping);

						int blue = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, minBranchBlueMapping,
								maxBranchBlueMapping);

						int alpha = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, maxBranchOpacityMapping,
								minBranchOpacityMapping);

						stroke(red, green, blue, alpha);
						line(x0, y0, x1, y1);

					}// END: state and parent state equality check
				}// END: null check
			}// END: root check
		}// END: nodes loop
	}// END: DrawBranches

	// ///////////////
	// ---CIRCLES---//
	// ///////////////
	private void drawCircles() {

		double[][] numberOfLineages = CountLineagesHoldingState(
				numberOfIntervals, rootHeight);
		double lineagesCountMax = Utils.get2DArrayMax(numberOfLineages);

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			for (int j = 0; j < (table.getRowCount()); j++) {

				if (numberOfLineages[i][j + 1] > 0) {

					/**
					 * Color mapping
					 * */
					int red = (int) Utils.map(numberOfLineages[i][j + 1], 0,
							lineagesCountMax, minPolygonRedMapping,
							maxPolygonRedMapping);

					int green = (int) Utils.map(numberOfLineages[i][j + 1], 0,
							lineagesCountMax, minPolygonGreenMapping,
							maxPolygonGreenMapping);

					int blue = (int) Utils.map(numberOfLineages[i][j + 1], 0,
							lineagesCountMax, minPolygonBlueMapping,
							maxPolygonBlueMapping);

					/**
					 * Opacity mapping
					 * 
					 * Larger the values more opaque the colors
					 * */
					int alpha = (int) Utils.map(numberOfLineages[i][j + 1], 0,
							lineagesCountMax, maxPolygonOpacityMapping,
							minPolygonOpacityMapping);

					stroke(red, green, blue, alpha);
					fill(red, green, blue, alpha);

					double radius = Math.round(100 * Math
							.sqrt(numberOfLineages[i][j + 1]))
							* polygonsRadiusMultiplier;

					Double longitude = Double.valueOf(String.valueOf(table
							.getValueAt(j, 1)));

					Double latitude = Double.valueOf(String.valueOf(table
							.getValueAt(j, 2)));

					List<Coordinates> coordinates = Utils.generateCircle(
							latitude, // centerLat
							longitude, // centerLong
							radius, // radius
							36); // numPoints

					beginShape();

					for (int row = 0; row < coordinates.size() - 1; row++) {

						double X = Utils.map(coordinates.get(row)
								.getLongitude(), minX, maxX, 0, width);
						double Y = Utils.map(
								coordinates.get(row).getLatitude(), maxY, minY,
								0, height);

						double XEND = Utils.map(coordinates.get(row + 1)
								.getLongitude(), minX, maxX, 0, width);
						double YEND = Utils.map((coordinates.get(row + 1)
								.getLatitude()), maxY, minY, 0, height);

						vertex((float) X, (float) Y);
						vertex((float) XEND, (float) YEND);

					}// END: coordinates loop

					endShape(CLOSE);

				}// END: if numberOfLineages

			}// END: table rows loop
		}// END: numberOfIntervals loop

	}// END: drawCircles

	private double[][] CountLineagesHoldingState(int numberOfIntervals,
			double rootHeight) {

		double delta = rootHeight / numberOfIntervals;
		double[][] numberOfLineages = new double[(numberOfIntervals - 1)][table
				.getRowCount() + 1];

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			numberOfLineages[i][0] = rootHeight - ((i + 1) * delta);
		}

		for (int i = 0; i < (numberOfIntervals - 1); i++) {
			for (int j = 0; j < table.getRowCount(); j++) {

				int numberOfLineagesOfState = 0;

				for (Node node : tree.getNodes()) {
					if (!tree.isRoot(node)) {

						String state = getRandomState((String) node
								.getAttribute(stateAttName), false);

						Node parentNode = tree.getParent(node);

						String parentState = getRandomState((String) parentNode
								.getAttribute(stateAttName), false);

						if ((tree.getHeight(node) <= numberOfLineages[i][0])
								&& (tree.getHeight(parentNode) > numberOfLineages[i][0])) {

							String name = String
									.valueOf(table.getValueAt(j, 0));

							if ((state.toLowerCase().equals(parentState
									.toLowerCase()))
									&& (parentState.toLowerCase().equals(name
											.toLowerCase()))) {

								numberOfLineagesOfState++;

							}
						}
					}
				}// END: node loop

				numberOfLineages[i][j + 1] = numberOfLineagesOfState;

			}// END: col loop
		}// END: row loop

		return numberOfLineages;
	}// END: CountLineagesHoldingState

	private String getRandomState(String state, boolean verbose) {

		generator.setSeed(time);

		if (!state.contains("+")) {
			return state;

		} else {// this breaks ties

			if (verbose)
				System.out.println("Found combined " + stateAttName
						+ " attribute: " + state);

			state = Utils.pickRand(state.split("\\+"), generator);

			if (verbose)
				System.out.println("Randomly picking: " + state);

			return state;
		}

	}// END: getRandomState

}// END: PlotOnMap class
