package org.boehn.kmlframework.kml;


public class LookAt extends AbstractView {

	private Double range;

	public LookAt() {}
	
	public LookAt(Double longitude, Double latitude, Double altitude, Double heading, Double tilt, AltitudeModeEnum altitudeMode, Double range) {
		super(longitude, latitude, altitude, heading,tilt, altitudeMode);
		this.range = range;
	}
	
	public Double getRange() {
		return range;
	}

	public void setRange(Double range) {
		this.range = range;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<LookAt" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if (range != null) {
			kml.println("<range>" + range + "</range>");
		}
		kml.println(-1, "</LookAt>");
	}
}