package org.boehn.kmlframework.kml;

public class LineStyle extends ColorStyle {

	private Double width;
	
	public LineStyle() {}
	
	public LineStyle(String color, ColorModeEnum colorMode, Double width) {
		super(color, colorMode);
		this.width = width;
	}
	
	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<LineStyle" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (width != null) {
			kml.println("<width>" + width + "</width>");
		}
		kml.println(-1, "</LineStyle>");
	}
}