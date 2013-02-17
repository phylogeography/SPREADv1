package kmlframework.coordinates;

import kmlframework.kml.AltitudeModeEnum;
import kmlframework.kml.Point;

public class EarthCoordinate extends Point implements Coordinate {

	public static double EARTHRADIUS = 6372795.477598; // in meters
	
	public EarthCoordinate() {}
	
	public EarthCoordinate(Double longitude, Double latitude) {
		super(longitude, latitude);
	}
	
	public EarthCoordinate(Double longitude, Double latitude, Double altitude) {
		super(longitude, latitude, altitude);
	}
	
	public EarthCoordinate(Boolean extrude, AltitudeModeEnum altitudeMode, Double longitude, Double latitude, Double altitude) {
		super(extrude, altitudeMode, longitude, latitude, altitude);
	}
	
	public double getRadius() {
		return getAltitude() + EARTHRADIUS;
	}
	
	public CartesianCoordinate toCartesianCoordinate() {
		CartesianCoordinate cartesianCoordinate = new CartesianCoordinate();
		cartesianCoordinate.setX(getRadius() * Math.sin(Math.PI/2 - getLatitude()*(Math.PI/180)) * Math.cos(getLongitude()*(Math.PI/180)));
		cartesianCoordinate.setY(getRadius() * Math.sin(Math.PI/2 - getLatitude()*(Math.PI/180)) * Math.sin(getLongitude()*(Math.PI/180)));
		cartesianCoordinate.setZ(getRadius() * Math.cos(Math.PI/2 - getLatitude()*(Math.PI/180)));
		return cartesianCoordinate;
	}
	
	public double distanceTo(EarthCoordinate earthCoordinate) {
		return toCartesianCoordinate().distanceTo(earthCoordinate.toCartesianCoordinate());
	}
	
	public String toString() {
		return "[longitude: " + getLongitude() + ", latitude: " + getLatitude() + ", altitude: " + getAltitude() + "]";
	}

	public EarthCoordinate toEarthCoordinate(EarthCoordinate earthCoordinate, Double rotation, CartesianCoordinate localReferenceCoordinate, CartesianCoordinate scale) {
		return this;
	}
}
