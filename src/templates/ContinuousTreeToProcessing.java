package templates;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.ImportException;
import jebl.evolution.io.NexusImporter;
import jebl.evolution.io.TreeImporter;
import jebl.evolution.trees.RootedTree;
import processing.core.PApplet;
import processing.core.PImage;
import structure.Coordinates;
import utils.Utils;

@SuppressWarnings("serial")
public class ContinuousTreeToProcessing extends PApplet {

	private final int imageWidth = 2048;
	private final int imageHeight = 1025;

	private String coordinatesName;
	private TreeImporter importer;
	private RootedTree tree;
	private String longitudeName;
	private String latitudeName;
	private double treeHeightMax;
	private String HPD;
	private PImage mapImage;

	// Borders of the map coordinates
	// min/max longitude
	private float minX, maxX;
	// min/max latitude
	private float minY, maxY;

	public ContinuousTreeToProcessing() {

	}// END:ContinuousTreeToProcessing

	public void setHPD(String percent) throws RuntimeException {
		HPD = percent;
	}

	public void setCoordinatesName(String name) {

		coordinatesName = name;
		// this is for coordinate attribute names
		longitudeName = (coordinatesName + 2);
		latitudeName = (coordinatesName + 1);
	}

	public void setTreePath(String path) throws IOException, ImportException {

		importer = new NexusImporter(new FileReader(path));
		tree = (RootedTree) importer.importNextTree();
		// this is for mappings
		treeHeightMax = Utils.getTreeHeightMax(tree);
	}

	public void setup() {

		minX = -180;
		maxX = 180;

		minY = -80;
		maxY = 90;

		width = imageWidth;
		height = imageHeight;

		size(width, height);

	}// END:setup

	public void draw() {

		smooth();
		drawMapBackground();
		drawPolygons();
		drawBranches();

	}// END:draw

	private void drawMapBackground() {

		// World map in Equirectangular projection
		mapImage = loadImage(LoadMapBackground(false));
		image(mapImage, 0, 0, width, height);

	}// END: drawMapPolygons

	// ////////////////
	// ---BRANCHES---//
	// ////////////////
	private void drawBranches() {

		strokeWeight(2);

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {

				float longitude = (float) Utils.getDoubleNodeAttribute(node,
						longitudeName);

				float latitude = (float) Utils.getDoubleNodeAttribute(node,
						latitudeName);

				Node parentNode = tree.getParent(node);
				float parentLongitude = (float) Utils.getDoubleNodeAttribute(
						parentNode, longitudeName);
				float parentLatitude = (float) Utils.getDoubleNodeAttribute(
						parentNode, latitudeName);

				// Equirectangular projection:
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
				int blue = (int) Utils
						.map(nodeHeight, 0, treeHeightMax, 255, 0);
				int alpha = (int) Utils.map(nodeHeight, 0, treeHeightMax, 100,
						255);

				stroke(red, green, blue, alpha);
				line(x0, y0, x1, y1);
			}
		}// END: nodes loop
	}// END: DrawBranches

	// ////////////////
	// ---POLYGONS---//
	// ////////////////
	private void drawPolygons() {

		for (Node node : tree.getNodes()) {
			if (!tree.isRoot(node)) {
				if (!tree.isExternal(node)) {

					int modality = Utils.getIntegerNodeAttribute(node,
							coordinatesName + "_" + HPD + "HPD_modality");

					for (int i = 1; i <= modality; i++) {

						Object[] longitudeHPD = Utils.getArrayNodeAttribute(
								node, longitudeName + "_" + HPD + "HPD_" + i);
						Object[] latitudeHPD = Utils.getArrayNodeAttribute(
								node, latitudeName + "_" + HPD + "HPD_" + i);
						/**
						 * Color mapping
						 * */
						double nodeHeight = tree.getHeight(node);

						int red = 55;
						int green = 0;
						int blue = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, 255, 0);
						int alpha = (int) Utils.map(nodeHeight, 0,
								treeHeightMax, 100, 255);

						stroke(red, green, blue, alpha);
						fill(red, green, blue, alpha);

						List<Coordinates> coordinates = Utils.ParsePolygons(
								longitudeHPD, latitudeHPD);

						beginShape();

						for (int row = 0; row < coordinates.size() - 1; row++) {

							float X = map((float) coordinates.get(row)
									.getLongitude(), minX, maxX, 0, width);
							float Y = map((float) coordinates.get(row)
									.getLatitude(), maxY, minY, 0, height);

							float XEND = map((float) coordinates.get(row + 1)
									.getLongitude(), minX, maxX, 0, width);
							float YEND = map((float) (coordinates.get(row + 1)
									.getLatitude()), maxY, minY, 0, height);

							vertex(X, Y);
							vertex(XEND, YEND);

						}// END: coordinates loop

						endShape(CLOSE);
					}// END: modality loop
				}
			}
		}// END: node loop
	}// END: drawPolygons

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
