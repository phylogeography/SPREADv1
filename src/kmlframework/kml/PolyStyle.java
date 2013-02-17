package kmlframework.kml;

public class PolyStyle extends ColorStyle {

	private Boolean fill;
	private Boolean outline;
	
	public PolyStyle() {}
	
	public PolyStyle(String color, ColorModeEnum colorMode, Boolean fill, Boolean outline) {
		super(color, colorMode);
		this.fill = fill;
		this.outline = outline;
	}
	
	public Boolean getFill() {
		return fill;
	}

	public void setFill(Boolean fill) {
		this.fill = fill;
	}

	public Boolean getOutline() {
		return outline;
	}

	public void setOutline(Boolean outline) {
		this.outline = outline;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<PolyStyle" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (fill != null) {
			kml.println("<fill>" + booleanToInt(fill) + "</fill>");
		}
		if (outline != null) {
			kml.println("<outline>" + booleanToInt(outline) + "</outline>");
		}
		kml.println(-1, "</PolyStyle>");
	}
}