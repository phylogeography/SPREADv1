package kmlframework.kml;

import java.util.List;

public class Polygon extends Geometry {

	private Boolean extrude;
	private Boolean tessellate;
	private AltitudeModeEnum altitudeMode;
	private LinearRing outerBoundary;
	private List<LinearRing> innerBoundaries;
	
	public Polygon() {}
	
	public Polygon(Boolean extrude, Boolean tessellate, AltitudeModeEnum altitudeMode, LinearRing outerBoundary, List<LinearRing> innerBoundaries) {
		this.extrude = extrude;
		this.tessellate = tessellate;
		this.altitudeMode = altitudeMode;
		this.outerBoundary = outerBoundary;
		this.innerBoundaries = innerBoundaries;
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

	public LinearRing getOuterBoundary() {
		return outerBoundary;
	}

	public void setOuterBoundary(LinearRing outerBoundary) {
		this.outerBoundary = outerBoundary;
	}

	public List<LinearRing> getInnerBoundaries() {
		return innerBoundaries;
	}

	public void setInnerBoundaries(List<LinearRing> innerBoundaries) {
		this.innerBoundaries = innerBoundaries;
	}

	public void write(Kml kml) throws KmlException {
		// We validate the data
		if (outerBoundary == null) {
			throw new KmlException("An outerBoundary is required in a Polygon");
		}
		
		kml.println("<Polygon" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (extrude != null) {
			kml.println("<extrude>" + booleanToInt(extrude) + "</extrude>");
		}
		if (tessellate != null) {
			kml.println("<tessellate>" + booleanToInt(tessellate) + "</tessellate>");
		}
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		
		kml.println("<outerBoundaryIs>", 1);
		outerBoundary.write(kml);
		kml.println(-1, "</outerBoundaryIs>");
		
		if (innerBoundaries != null) {
			for (LinearRing innerBounadry : innerBoundaries) {
				kml.println("<innerBoundaryIs>", 1);
				innerBounadry.write(kml);
				kml.println(-1, "</innerBoundaryIs>");
			}
		}
		kml.println(-1, "</Polygon>");
	}
}