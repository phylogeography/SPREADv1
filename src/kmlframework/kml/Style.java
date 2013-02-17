package kmlframework.kml;

public class Style extends StyleSelector {

	private IconStyle iconStyle;
	private LabelStyle labelStyle;
	private LineStyle lineStyle;
	private PolyStyle polyStyle;
	private BalloonStyle ballonStyle;
	private ListStyle listStyle;

	public Style() {}
	
	public Style(IconStyle iconStyle, LabelStyle labelStyle, LineStyle lineStyle, PolyStyle polyStyle, BalloonStyle ballonStyle, ListStyle listStyle) {
		this.iconStyle = iconStyle;
		this.labelStyle = labelStyle;
		this.lineStyle = lineStyle;
		this.polyStyle = polyStyle;
		this.ballonStyle = ballonStyle;
		this.listStyle = listStyle;
	}
	
	public IconStyle getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(IconStyle iconStyle) {
		this.iconStyle = iconStyle;
	}

	public LabelStyle getLabelStyle() {
		return labelStyle;
	}

	public void setLabelStyle(LabelStyle labelStyle) {
		this.labelStyle = labelStyle;
	}

	public LineStyle getLineStyle() {
		return lineStyle;
	}

	public void setLineStyle(LineStyle lineStyle) {
		this.lineStyle = lineStyle;
	}

	public PolyStyle getPolyStyle() {
		return polyStyle;
	}

	public void setPolyStyle(PolyStyle polyStyle) {
		this.polyStyle = polyStyle;
	}

	public BalloonStyle getBallonStyle() {
		return ballonStyle;
	}

	public void setBallonStyle(BalloonStyle ballonStyle) {
		this.ballonStyle = ballonStyle;
	}

	public ListStyle getListStyle() {
		return listStyle;
	}

	public void setListStyle(ListStyle listStyle) {
		this.listStyle = listStyle;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Style" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (iconStyle != null) {
			iconStyle.write(kml);
		}
		if (labelStyle != null) {
			labelStyle.write(kml);
		}
		if (lineStyle != null) {
			lineStyle.write(kml);
		}
		if (polyStyle != null) {
			polyStyle.write(kml);
		}
		if (ballonStyle != null) {
			ballonStyle.write(kml);
		}
		if (listStyle != null) {
			listStyle.write(kml);
		}
		kml.println(-1, "</Style>");
	}
}