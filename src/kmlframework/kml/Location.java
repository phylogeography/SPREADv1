/*
 * Location.java Created Oct 13, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml;

public class Location extends KmlObject
{
	private Double latitude;
	private Double longitude;
	private Double altitude;

	public Location() {
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
		kml.println("<Location" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if(latitude!=null)
			kml.println("<latitude>"+latitude+"</latitude>");
		if(longitude!=null)
			kml.println("<longitude>"+longitude+"</longitude>");
		if(altitude!=null)
			kml.println("<altitude>"+altitude+"</altitude>");
		kml.println(-1, "</Location>");
	}
}
