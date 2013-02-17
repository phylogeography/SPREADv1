package org.boehn.kmlframework.kml;

import java.util.List;

import org.boehn.kmlframework.atom.AtomAuthor;
import org.boehn.kmlframework.atom.AtomLink;

public class GroundOverlay extends Overlay implements Deletable {

	private Double altitude;
	private AltitudeModeEnum altitudeMode;
	private Double north;
	private Double south;
	private Double east;
	private Double west;
	private Double rotation;
	
	public GroundOverlay() {}
	
	public GroundOverlay(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor, AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber, String snippet, Integer snippetMaxLines,String description, AbstractView abstractView, TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors, Region region, ExtendedData extendedData, String color, Integer drawOrder, Icon icon, Double alititude, AltitudeModeEnum altitudeMode, Double north, Double south, Double east, Double west, Double rotation) {
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails, phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive, styleUrl, styleSelectors, region, extendedData, color, drawOrder, icon);
		this.altitude = alititude;
		this.altitudeMode = altitudeMode;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.rotation = rotation;
	}
	
	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public AltitudeModeEnum getAltitudeMode() {
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode) {
		this.altitudeMode = altitudeMode;
	}

	public Double getNorth() {
		return north;
	}

	public void setNorth(Double north) {
		this.north = north;
	}

	public Double getSouth() {
		return south;
	}

	public void setSouth(Double south) {
		this.south = south;
	}

	public Double getEast() {
		return east;
	}

	public void setEast(Double east) {
		this.east = east;
	}

	public Double getWest() {
		return west;
	}

	public void setWest(Double west) {
		this.west = west;
	}

	public Double getRotation() {
		return rotation;
	}

	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<GroundOverlay" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (altitude != null) {
			kml.println("<altitude>" + altitude + "</altitude>");
		}
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		if (north != null || south != null || east != null || west != null || rotation != null) {
			kml.println("<LatLonBox>", 1);
			if (north != null) {
				kml.println("<north>" + north + "</north>");
			}
			if (south != null) {
				kml.println("<south>" + south + "</south>");
			}
			if (east != null) {
				kml.println("<east>" + east + "</east>");
			}
			if (west != null) {
				kml.println("<west>" + west + "</west>");
			}
			if (rotation != null) {
				kml.println("<rotation>" + rotation + "</rotation>");
			}
			kml.println(-1, "</LatLonBox>");
		}
		kml.println(-1, "</GroundOverlay>");
	}
	
	public void writeDelete(Kml kml) throws KmlException {
		kml.println("<GroundOverlay" + getIdAndTargetIdFormatted(kml) + "></GroundOverlay>");
	}
}