package templates;

import java.io.FileReader;
import java.io.IOException;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import utils.ReadLocations;
import utils.Utils;

@SuppressWarnings("serial")
public class DiscreteTreeToProcessing extends PApplet {

	private boolean jar = false;
	private final int imageWidth = 2048;
	private final int imageHeight = 1025;

	private TreeImporter importer;
	private RootedTree tree;
	private ReadLocations data;
	private String stateAttName;
	private PImage mapImage;

	// Borders of the map coordinates
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

	public void setLocationFilePath(String path) {
		data = new ReadLocations(path);
	}

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -90;
		maxY = 90;

		width = imageWidth;
		height = imageHeight;

		size(width, height);

		// will improve font rendering speed with default renderer
		hint(ENABLE_NATIVE_FONTS);
		PFont plotFont = createFont("Arial", 12);
		textFont(plotFont);

	}// END: setup

	public void draw() {

		smooth();
		drawMapBackground();
		DrawPlaces();
		DrawBranches();
		DrawPlacesLabels();

	}// END:draw

	void drawMapBackground() {

		// World map in Equirectangular projection
		mapImage = loadImage(LoadMapBackground(jar));
		image(mapImage, 0, 0, width, height);

	}// END: drawMapPolygons

	// //////////////
	// ---PLACES---//
	// //////////////
	private void DrawPlaces() {

		float radius = 7;
		// White
		fill(255, 255, 255);
		noStroke();

		for (int row = 0; row < data.nrow; row++) {

			// Equirectangular projection:
			float X = map(data.getFloat(row, 1), minX, maxX, 0, width);
			float Y = map(data.getFloat(row, 0), maxY, minY, 0, height);

			ellipse(X, Y, radius, radius);

		}
	}// END DrawPlaces

	private void DrawPlacesLabels() {

		textSize(7);

		// Black labels
		fill(0, 0, 0);

		for (int row = 0; row < data.nrow; row++) {

			String name = data.locations[row];
			float X = map(data.getFloat(row, 1), minX, maxX, 0, width);
			float Y = map(data.getFloat(row, 0), maxY, minY, 0, height);

			text(name, X, Y);
		}
	}// END: DrawPlacesLabels

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private void DrawBranches() {

		strokeWeight(2);

		double treeHeightMax = Utils.getTreeHeightMax(tree);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				String state = (String) node.getAttribute(stateAttName);

				Node parentNode = tree.getParent(node);
				String parentState = (String) parentNode
						.getAttribute(stateAttName);

				if (!state.toLowerCase().equals(parentState.toLowerCase())) {

					float longitude = Utils
							.MatchStateCoordinate(data, state, 1);
					float latitude = Utils.MatchStateCoordinate(data, state, 0);

					float parentLongitude = Utils.MatchStateCoordinate(data,
							parentState, 1);
					float parentLatitude = Utils.MatchStateCoordinate(data,
							parentState, 0);

					float x0 = map(parentLongitude, minX, maxX, 0, width);
					float y0 = map(parentLatitude, maxY, minY, 0, height);

					float x1 = map(longitude, minX, maxX, 0, width);
					float y1 = map(latitude, maxY, minY, 0, height);

					/**
					 * Color mapping
					 * */
					double nodeHeight = tree.getHeight(node);

					int red = 255;
					int green = 0;
					int blue = (int) Utils.map(nodeHeight, 0, treeHeightMax,
							255, 0);
					int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax,
							100, 255);

					stroke(red, green, blue, alpha);
					line(x0, y0, x1, y1);
				}
			}
		}// END: nodes loop
	}// END: DrawBranches

	private String LoadMapBackground(boolean fromJar) {

		String imgPathFromJar;

		if (fromJar) {
			imgPathFromJar = "jar:"
					+ this.getClass().getResource("world_map.png").getPath();
		} else {
			imgPathFromJar = this.getClass().getResource("world_map.png")
					.getPath();
		}

		return imgPathFromJar;
	}

}// END: PlotOnMap class
