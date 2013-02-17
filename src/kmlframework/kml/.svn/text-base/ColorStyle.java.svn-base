package org.boehn.kmlframework.kml;

public abstract class ColorStyle extends KmlObject {

	private String color;
	private ColorModeEnum colorMode;
	
	public ColorStyle() {}
	
	public ColorStyle(String color, ColorModeEnum colorMode) {
		this.color = color;
		this.colorMode = colorMode;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public ColorModeEnum getColorMode() {
		return colorMode;
	}
	
	public void setColorMode(ColorModeEnum colorMode) {
		this.colorMode = colorMode;
	}
	
	public void writeInner(Kml kml) throws KmlException {
		if (color != null) {
			kml.println("<color>" + color + "</color>");
		}
		if (colorMode != null) {
			kml.println("<colorMode>" + colorMode + "</colorMode>");
		}
	}
}