package org.boehn.kmlframework.kml;

public class IconStyle extends ColorStyle {

	private Double scale;
	private Double heading;
	private String iconHref;
	private Double hotSpotX;
	private Double hotSpotY;
	private UnitEnum hotSpotXunits;
	private UnitEnum hotSpotYunits;
	
	public IconStyle() {}
	
	public IconStyle(String color, ColorModeEnum colorMode, Double scale, Double heading, String iconHref, Double hotSpotX, Double hotSpotY, UnitEnum hotSpotXunits, UnitEnum hotSpotYunits) {
		super(color, colorMode);
		this.scale = scale;
		this.heading = heading;
		this.iconHref = iconHref;
		this.hotSpotX = hotSpotX;
		this.hotSpotY = hotSpotY;
		this.hotSpotXunits = hotSpotXunits;
		this.hotSpotYunits = hotSpotYunits;
	}
	
	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}

	public String getIconHref() {
		return iconHref;
	}

	public void setIconHref(String iconHref) {
		this.iconHref = iconHref;
	}

	public Double getHotSpotX() {
		return hotSpotX;
	}

	public void setHotSpotX(Double hotSpotX) {
		this.hotSpotX = hotSpotX;
	}

	public Double getHotSpotY() {
		return hotSpotY;
	}

	public void setHotSpotY(Double hotSpotY) {
		this.hotSpotY = hotSpotY;
	}

	public UnitEnum getHotSpotXunits() {
		return hotSpotXunits;
	}

	public void setHotSpotXunits(UnitEnum hotSpotXunits) {
		this.hotSpotXunits = hotSpotXunits;
	}

	public UnitEnum getHotSpotYunits() {
		return hotSpotYunits;
	}

	public void setHotSpotYunits(UnitEnum hotSpotYunits) {
		this.hotSpotYunits = hotSpotYunits;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<IconStyle" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (scale != null) {
			kml.println("<scale>" + scale + "</scale>");
		}
		if (heading != null) {
			kml.println("<heading>" + heading + "</heading>");
		}
		if (iconHref != null) {
			kml.println("<Icon>", 1);
			kml.println("<href>" + iconHref + "</href>");
			kml.println(-1, "</Icon>");
		}
		if (hotSpotX != null || hotSpotY != null || hotSpotXunits != null || hotSpotYunits != null) {
			kml.println("<hotSpot" + (hotSpotX != null ? " x=\"" + hotSpotX + "\"" : "") + (hotSpotY != null ? " y=\"" + hotSpotY + "\"" : "") + (hotSpotXunits != null ? " xunits=\"" + hotSpotXunits + "\"" : "") + (hotSpotYunits != null ? " yunits=\"" + hotSpotYunits + "\"" : "") + "/>");
		}
		kml.println(-1, "</IconStyle>");
	}
}