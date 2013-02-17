package org.boehn.kmlframework.kml;

import java.util.List;

import org.boehn.kmlframework.atom.AtomAuthor;
import org.boehn.kmlframework.atom.AtomLink;

public class ScreenOverlay extends Overlay implements Deletable {

	private Double overlayX;
	private Double overlayY;
	private UnitEnum overlayXunits;
	private UnitEnum overlayYunits;
	private Double screenX;
	private Double screenY;
	private UnitEnum screenXunits;
	private UnitEnum screenYunits;
	private Double rotationX;
	private Double rotationY;
	private UnitEnum rotationXunits;
	private UnitEnum rotationYunits;
	private Double sizeX;
	private Double sizeY;
	private UnitEnum sizeXunits;
	private UnitEnum sizeYunits;
	private Double rotation;
	
	public ScreenOverlay() {}
	
	public ScreenOverlay(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor, AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber, String snippet, Integer snippetMaxLines,String description, AbstractView abstractView, TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors, Region region, ExtendedData extendedData, String color, Integer drawOrder, Icon icon, Double overlayX, Double overlayY, UnitEnum overlayXunits, UnitEnum overlayYunits, Double screenX, Double screenY, UnitEnum screenXunits, UnitEnum screenYunits, Double rotationX, Double rotationY, UnitEnum rotationXunits, UnitEnum rotationYunits, Double sizeX, Double sizeY, UnitEnum sizeXunits, UnitEnum sizeYunits, Double rotation) {
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails, phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive, styleUrl, styleSelectors, region, extendedData, color, drawOrder, icon);
		this.overlayX = overlayX;
		this.overlayY = overlayY;
		this.overlayXunits = overlayXunits;
		this.overlayYunits = overlayYunits;
		this.screenX = screenX;
		this.screenY = screenY;
		this.screenXunits = screenXunits;
		this.screenYunits = screenYunits;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationXunits = rotationXunits;
		this.rotationYunits = rotationYunits;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeXunits = sizeXunits;
		this.sizeYunits = sizeYunits;
		this.rotation = rotation;
	}
	
	public Double getOverlayX() {
		return overlayX;
	}

	public void setOverlayX(Double overlayX) {
		this.overlayX = overlayX;
	}

	public Double getOverlayY() {
		return overlayY;
	}

	public void setOverlayY(Double overlayY) {
		this.overlayY = overlayY;
	}

	public UnitEnum getOverlayXunits() {
		return overlayXunits;
	}

	public void setOverlayXunits(UnitEnum overlayXunits) {
		this.overlayXunits = overlayXunits;
	}

	public UnitEnum getOverlayYunits() {
		return overlayYunits;
	}

	public void setOverlayYunits(UnitEnum overlayYunits) {
		this.overlayYunits = overlayYunits;
	}

	public Double getScreenX() {
		return screenX;
	}

	public void setScreenX(Double screenX) {
		this.screenX = screenX;
	}

	public Double getScreenY() {
		return screenY;
	}

	public void setScreenY(Double screenY) {
		this.screenY = screenY;
	}

	public UnitEnum getScreenXunits() {
		return screenXunits;
	}

	public void setScreenXunits(UnitEnum screenXunits) {
		this.screenXunits = screenXunits;
	}

	public UnitEnum getScreenYunits() {
		return screenYunits;
	}

	public void setScreenYunits(UnitEnum screenYunits) {
		this.screenYunits = screenYunits;
	}

	public Double getRotationX() {
		return rotationX;
	}

	public void setRotationX(Double rotationX) {
		this.rotationX = rotationX;
	}

	public Double getRotationY() {
		return rotationY;
	}

	public void setRotationY(Double rotationY) {
		this.rotationY = rotationY;
	}

	public UnitEnum getRotationXunits() {
		return rotationXunits;
	}

	public void setRotationXunits(UnitEnum rotationXunits) {
		this.rotationXunits = rotationXunits;
	}

	public UnitEnum getRotationYunits() {
		return rotationYunits;
	}

	public void setRotationYunits(UnitEnum rotationYunits) {
		this.rotationYunits = rotationYunits;
	}

	public Double getSizeX() {
		return sizeX;
	}

	public void setSizeX(Double sizeX) {
		this.sizeX = sizeX;
	}

	public Double getSizeY() {
		return sizeY;
	}

	public void setSizeY(Double sizeY) {
		this.sizeY = sizeY;
	}

	public UnitEnum getSizeXunits() {
		return sizeXunits;
	}

	public void setSizeXunits(UnitEnum sizeXunits) {
		this.sizeXunits = sizeXunits;
	}

	public UnitEnum getSizeYunits() {
		return sizeYunits;
	}

	public void setSizeYunits(UnitEnum sizeYunits) {
		this.sizeYunits = sizeYunits;
	}

	public Double getRotation() {
		return rotation;
	}

	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<ScreenOverlay" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (overlayX != null || overlayY != null || overlayXunits != null || overlayYunits != null) {
			kml.println("<overlayXY" + (overlayX != null ? " x=\"" + overlayX + "\"" : "") + (overlayY != null ? " y=\"" + overlayY + "\"" : "") + (overlayXunits != null ? " xunits=\"" + overlayXunits + "\"" : "") + (overlayYunits != null ? " yunits=\"" + overlayYunits + "\"" : "") + "/>");
		}
		if (screenX != null || screenY != null || screenXunits != null || screenYunits != null) {
			kml.println("<screenXY" + (screenX != null ? " x=\"" + screenX + "\"" : "") + (screenY != null ? " y=\"" + screenY + "\"" : "") + (screenXunits != null ? " xunits=\"" + screenXunits + "\"" : "") + (screenYunits != null ? " yunits=\"" + screenYunits + "\"" : "") + "/>");
		}
		if (rotationX != null || rotationY != null || rotationXunits != null || rotationYunits != null) {
			kml.println("<rotationXY" + (rotationX != null ? " x=\"" + rotationX + "\"" : "") + (rotationY != null ? " y=\"" + rotationY + "\"" : "") + (rotationXunits != null ? " xunits=\"" + rotationXunits + "\"" : "") + (rotationYunits != null ? " yunits=\"" + rotationYunits + "\"" : "") + "/>");
		}
		if (sizeX != null || sizeY != null || sizeXunits != null || sizeYunits != null) {
			kml.println("<sizeXY" + (sizeX != null ? " x=\"" + sizeX + "\"" : "") + (sizeY != null ? " y=\"" + sizeY + "\"" : "") + (sizeXunits != null ? " xunits=\"" + sizeXunits + "\"" : "") + (sizeYunits != null ? " yunits=\"" + sizeYunits + "\"" : "") + "/>");
		}
		if (rotation != null) {
			kml.println("<rotation>" + rotation + "</rotation>"); 
		}
		kml.println(-1, "</ScreenOverlay>");
	}
	
	public void writeDelete(Kml kml) throws KmlException {
		kml.println("<ScreenOverlay" + getIdAndTargetIdFormatted(kml) + "></ScreenOverlay>");
	}
}