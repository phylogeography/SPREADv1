package templates;

import gui.InteractiveTableModel;

import java.io.FileReader;
import java.io.IOException;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import processing.core.PApplet;
import processing.core.PFont;
import utils.Utils;

@SuppressWarnings("serial")
public class DiscreteTreeToProcessing extends PApplet {

	private TreeImporter importer;
	private RootedTree tree;
	private InteractiveTableModel table;
	private String stateAttName;
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

	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

	public DiscreteTreeToProcessing() {

	}// END: DiscreteTreeToProcessing

	public void setStateAttName(String name) {
		stateAttName = name;
	}

	public void setTreePath(String path) throws IOException, ImportException {
		importer = new NexusImporter(new FileReader(path));
		tree = (RootedTree) importer.importNextTree();
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
		mapBackground.drawMapBackground();
		DrawPlaces();
		DrawBranches();
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

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private void DrawBranches() {

		strokeWeight((float) branchWidth);

		double treeHeightMax = Utils.getTreeHeightMax(tree);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String state = Utils.getStringNodeAttribute(node, stateAttName);

				Node parentNode = tree.getParent(node);
				String parentState = (String) parentNode
						.getAttribute(stateAttName);

				if (!state.toLowerCase().equals(parentState.toLowerCase())) {

					float longitude = Utils.MatchStateCoordinate(table, state,
							2);
					float latitude = Utils
							.MatchStateCoordinate(table, state, 1);

					float parentLongitude = Utils.MatchStateCoordinate(table,
							parentState, 2);
					float parentLatitude = Utils.MatchStateCoordinate(table,
							parentState, 1);

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

					int green = (int) Utils.map(nodeHeight, 0, treeHeightMax,
							minBranchGreenMapping, maxBranchGreenMapping);

					int blue = (int) Utils.map(nodeHeight, 0, treeHeightMax,
							minBranchBlueMapping, maxBranchBlueMapping);

					int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax,
							maxBranchOpacityMapping, minBranchOpacityMapping);

					stroke(red, green, blue, alpha);
					line(x0, y0, x1, y1);
				}
			}
		}// END: nodes loop
	}// END: DrawBranches

}// END: PlotOnMap class
