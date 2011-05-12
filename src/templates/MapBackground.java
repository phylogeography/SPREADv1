package templates;

import processing.core.PApplet;
import processing.core.PImage;

public class MapBackground {

	private PApplet parent;
	private PImage mapImage;
	private String imgPath;

	boolean fromJar = false;

	public MapBackground(PApplet p) {

		parent = p;
		
		if (fromJar) {
			imgPath = "jar:"
					+ this.getClass().getResource("world_map.png").getPath();
		} else {
			imgPath = this.getClass().getResource("world_map.png").getPath();
		}

		// World map in Equirectangular projection
		mapImage = parent.loadImage(imgPath);

	}// END: MapBackground(PApplet p)

	public void drawMapBackground() {

		parent.image(mapImage, 0, 0, parent.width, parent.height);

	}// END: drawMapBackground

}// END: MapBackground class
