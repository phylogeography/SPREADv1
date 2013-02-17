package org.boehn.kmlframework.kml;

public class LabelStyle extends ColorStyle {

	private Double scale;
	
	public LabelStyle() {}
	
	public LabelStyle(String color, ColorModeEnum colorMode, Double scale) {
		super(color, colorMode);
		this.scale = scale;
	}
	
	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<LabelStyle" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (scale != null) {
			kml.println("<scale>" + scale + "</scale>");
		}
		kml.println(-1, "</LabelStyle>");
	}
}