package kmlframework.kml;


public abstract class AbstractView extends KmlObject {
	
	private Double longitude;
	private Double latitude;
	private Double altitude;
	private Double heading;
	private Double tilt;
	private AltitudeModeEnum altitudeMode;

	public AbstractView() {}
	
	public AbstractView(Double longitude, Double latitude, Double altitude, Double heading, Double tilt, AltitudeModeEnum altitudeMode) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.heading = heading;
		this.tilt = tilt;
		this.altitudeMode = altitudeMode;
	}
	
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}

	public Double getTilt() {
		return tilt;
	}

	public void setTilt(Double tilt) {
		this.tilt = tilt;
	}

	public AltitudeModeEnum getAltitudeMode() {
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode) {
		this.altitudeMode = altitudeMode;
	}

	public void writeInner(Kml kml) throws KmlException {
		if (longitude != null) {
			kml.println("<longitude>" + longitude + "</longitude>");
		}
		if (latitude != null) {
			kml.println("<latitude>" + latitude + "</latitude>");
		}
		if (altitude != null) {
			kml.println("<altitude>" + altitude + "</altitude>");
		}
		if (heading != null) {
			kml.println("<heading>" + heading + "</heading>");
		}
		if (tilt != null) {
			kml.println("<tilt>" + tilt + "</tilt>");
		}
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
	}
}
