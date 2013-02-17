package kmlframework.kml;

import java.util.List;

public class Model extends Geometry {

	private AltitudeModeEnum altitudeMode;
	private Double longitude;
	private Double latitude;
	private Double altitude;
	private Double heading;
	private Double tilt;
	private Double roll;
	private Double scaleX;
	private Double scaleY;
	private Double scaleZ;
	private Link link;
	private List<Alias> resourceMap;
	private String locationID;
	private String orientationID;
	private String scaleID;
	
	public Model() {}
	
	public Model(AltitudeModeEnum altitudeMode, Double longitude, Double latitude, Double altitude, Double heading, Double tilt, Double roll, Double scaleX, Double scaleY, Double scaleZ, Link link, List<Alias> resourceMap) {
		this.altitudeMode = altitudeMode;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.heading = heading;
		this.tilt = tilt;
		this.roll = roll;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.link = link;
		this.resourceMap = resourceMap;
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

	public Double getRoll() {
		return roll;
	}

	public void setRoll(Double roll) {
		this.roll = roll;
	}

	public Double getScaleX() {
		return scaleX;
	}

	public void setScaleX(Double scaleX) {
		this.scaleX = scaleX;
	}

	public Double getScaleY() {
		return scaleY;
	}

	public void setScaleY(Double scaleY) {
		this.scaleY = scaleY;
	}

	public Double getScaleZ() {
		return scaleZ;
	}

	public void setScaleZ(Double scaleZ) {
		this.scaleZ = scaleZ;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public List<Alias> getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(List<Alias> resourceMap) {
		this.resourceMap = resourceMap;
	}

	public String getLocationID() {
		return locationID;
	}

	public void setLocationID(String id) {
		locationID=id;
	}

	public String getOrientationID() {
		return orientationID;
	}

	public void setOrientationID(String id) {
		orientationID=id;
	}

	public String getScaleID() {
		return scaleID;
	}

	public void setScaleID(String id) {
		scaleID=id;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Model" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (altitudeMode != null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		if (longitude != null || latitude != null || altitude != null) {
			kml.println("<Location"+
				(locationID==null ? "" : " id=\""+locationID+"\"")
				+">", 1);
			if (longitude != null) {				
				kml.println("<longitude>" + longitude + "</longitude>");
			}
			if (latitude != null) {				
				kml.println("<latitude>" + latitude + "</latitude>");
			}
			if (altitude != null) {
				kml.println("<altitude>" + altitude + "</altitude>");
			}
			kml.println(-1, "</Location>");
		}
		if (heading != null || tilt != null || roll != null) {
			kml.println("<Orientation"
				+(orientationID==null ? "" : " id=\""+orientationID+"\"")
				+">", 1);
			if (heading != null) {				
				kml.println("<heading>" + heading + "</heading>");
			}
			if (tilt != null) {				
				kml.println("<tilt>" + tilt + "</tilt>");
			}
			if (roll != null) {
				kml.println("<roll>" + roll + "</roll>");
			}
			kml.println(-1, "</Orientation>");
		}
		if (scaleX != null || scaleY != null || scaleZ != null) {
			kml.println("<Scale"
				+(scaleID==null ? "" : " id=\""+scaleID+"\"")
				+">", 1);
			if (scaleX != null) {				
				kml.println("<x>" + scaleX + "</x>");
			}
			if (scaleY != null) {				
				kml.println("<y>" + scaleY+ "</y>");
			}
			if (scaleZ != null) {
				kml.println("<z>" + scaleZ + "</z>");
			}
			kml.println(-1, "</Scale>");
		}
		if (link != null) {
			link.write(kml);
		}
		if (resourceMap != null) {
			kml.println("<ResourceMap>", -1);
			for (Alias alias : resourceMap) {
				alias.write(kml);
			}
			kml.println(-1, "</ResourceMap>");
		}
		kml.println(-1, "</Model>");
	}
}