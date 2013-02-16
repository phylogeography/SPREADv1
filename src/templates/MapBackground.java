package templates;

import processing.core.PApplet;
import processing.core.PImage;

public class MapBackground {

	private PApplet parent;
	private PImage mapImage;
	private String imgPath;
	public static int MAP_IMAGE_WIDTH = 2048;
	public static int MAP_IMAGE_HEIGHT = 1025;

	public MapBackground(PApplet p) {

		parent = p;

		String runningJarName = getRunningJarName();
		if (runningJarName != null) {
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

	public String getRunningJarName() {
		
		String className = this.getClass().getName().replace('.', '/');
		String classJar = this.getClass().getResource("/" + className + ".class").toString();
		
		if (classJar.startsWith("jar:")) {
			String vals[] = classJar.split("/");
			for (String val : vals) {
				if (val.contains("!")) {
					return val.substring(0, val.length() - 1);
				}
			}
		}
		
		return null;
	}// END: getRunningJarName
	
}// END: MapBackground class
