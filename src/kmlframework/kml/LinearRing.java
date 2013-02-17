package kmlframework.kml;

import java.util.List;

public class LinearRing extends Geometry {

	private Boolean extrude;
	private Boolean tessellate;
	private AltitudeModeEnum altitudeMode;
	private List<Point> coordinates;
	
	public LinearRing() {}
	
	public LinearRing(Boolean extrude, Boolean tessellate, AltitudeModeEnum altitudeMode, List<Point> coordinates) {
		this.extrude = extrude;
		this.tessellate = tessellate;
		this.altitudeMode = altitudeMode;
		this.coordinates = coordinates;
	}
	
	public Boolean getExtrude() {
		return extrude;
	}

	public void setExtrude(Boolean extrude) {
		this.extrude = extrude;
	}

	public Boolean getTessellate() {
		return tessellate;
	}

	public void setTessellate(Boolean tessellate) {
		this.tessellate = tessellate;
	}

	public AltitudeModeEnum getAltitudeMode() {
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode) {
		this.altitudeMode = altitudeMode;
	}

	public List<Point> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Point> coordinates) {
		this.coordinates = coordinates;
	}

	public void write(Kml kml) throws KmlException {
		// We validate the data
		if (coordinates == null || coordinates.size() < 3) {
			throw new KmlException("LineString must contain at least 3 points");
		}
		
		kml.println("<LinearRing" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (extrude != null) {
			kml.println("<extrude>" + booleanToInt(extrude) + "</extrude>");
		}
		if (tessellate != null) {
			kml.println("<tessellate>" + booleanToInt(tessellate) + "</tessellate>");
		}
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		kml.print("<coordinates>");
		boolean firstLoop = true;
		for (Point point : coordinates) {
			if (firstLoop) {
				firstLoop = false;
			} else {
				kml.printNoIndent(" ");
			}
			kml.printNoIndent(point.getLongitudeLatitudeAltitudeString());
		}
		// We add the first coordinate to the end, as KML require the first coordinate to be equal to the last
		kml.print(" " + coordinates.get(0).getLongitudeLatitudeAltitudeString());
		
		kml.println("</coordinates>");

		kml.println(-1, "</LinearRing>");
	}
}