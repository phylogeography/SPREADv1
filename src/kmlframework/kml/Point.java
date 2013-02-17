package kmlframework.kml;

public class Point extends Geometry {

	private Boolean extrude;
	private AltitudeModeEnum altitudeMode;
	private Double longitude;
	private Double latitude;
	private Double altitude;

	public Point() {}
	
	public Point(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public Point(Double longitude, Double latitude, Double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}
	
	public Point(Boolean extrude, AltitudeModeEnum altitudeMode, Double longitude, Double latitude, Double altitude) {
		this.extrude = extrude;
		this.altitudeMode = altitudeMode;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}
	
	public Boolean getExtrude() {
		return extrude;
	}

	public void setExtrude(Boolean extrude) {
		this.extrude = extrude;
	}

	public AltitudeModeEnum getAltitudeMode() {
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode) {
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

	public void write(Kml kml) throws KmlException {
		kml.println("<Point" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (extrude != null) {
			kml.println("<extrude>" + booleanToInt(extrude) + "</extrude>");
		}
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		if (longitude != null && latitude != null) {
			kml.println("<coordinates>" + getLongitudeLatitudeAltitudeString() + "</coordinates>");
		}
		
		kml.println(-1, "</Point>");
	}
	
	public String getLongitudeLatitudeAltitudeString() {
		return longitude +"," + latitude + (altitude != null? "," + altitude : "");
	}
}
