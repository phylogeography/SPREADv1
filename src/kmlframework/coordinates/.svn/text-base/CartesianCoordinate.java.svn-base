package org.boehn.kmlframework.coordinates;

import org.boehn.kmlframework.utils.Ellipsoid;

public class CartesianCoordinate implements Coordinate {

	private double x;
	private double y;
	private double z;
	
	public CartesianCoordinate() {}
	
	public CartesianCoordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public double distanceTo(CartesianCoordinate cartesianCoordinate) {
		return Math.sqrt( Math.pow(Math.abs(cartesianCoordinate.getX()-x), 2) + Math.pow(Math.abs(cartesianCoordinate.getY()-y), 2) + Math.pow(Math.abs(cartesianCoordinate.getZ()-z), 2));
	}
	
	public void rotateAroundZAxis(double rotation) {
		double xTemp = Math.cos(rotation) * x - Math.sin(rotation) * y;
		y = Math.sin(rotation) * x + Math.cos(rotation) * y;
		x = xTemp;
	}
	
	public void rotateAroundYAxis(double rotation) {
		double xTemp = Math.cos(rotation) * x + Math.sin(rotation) * z;
		z = - Math.sin(rotation) * x + Math.cos(rotation) * z;
		x = xTemp;
	}
	
	public void rotateAroundXAxis(double rotation) {
		double yTemp = Math.cos(rotation) * y - Math.sin(rotation) * z;
		z = Math.sin(rotation) * y + Math.cos(rotation) * z;
		y = yTemp;
	}
	
	public void add(CartesianCoordinate cartesianCoordinate) {
		x += cartesianCoordinate.getX();
		y += cartesianCoordinate.getY();
		z += cartesianCoordinate.getZ();
	}
	
	public void subtract(CartesianCoordinate cartesianCoordinate) {
		x -= cartesianCoordinate.getX();
		y -= cartesianCoordinate.getY();
		z -= cartesianCoordinate.getZ();
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize() {
		double length = length();
		x /= length;
		y /= length;
		z /= length;
	}
	
	public void scale(double scalingFactor) {
		x *= scalingFactor;
		y *= scalingFactor;
		z *= scalingFactor;
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}
	
	public EarthCoordinate toEarthCoordinate(EarthCoordinate location, Double rotation, CartesianCoordinate localReferenceCoordinate, CartesianCoordinate scale) {
		// We scale the coordinates
		double xTransformed = x;
		double yTransformed = y;
		double zTransformed = z;
		
		if (scale != null) {
			xTransformed = x * scale.getX();
			yTransformed = y * scale.getY();
			zTransformed = z * scale.getZ();
		}
		
		// We move the coordinates according to the local reference coordinate
		if (localReferenceCoordinate != null) {
			xTransformed -= localReferenceCoordinate.getX();
			yTransformed -= localReferenceCoordinate.getY();
			zTransformed -= localReferenceCoordinate.getZ();
		}
		
		//rotation = Math.PI/4;
		// We rotate the coordinates according to the rotation. We do only support rotation around the z axis
		if (rotation != null) {
			double xTmp = xTransformed;
			xTransformed = Math.cos(rotation) * xTmp + Math.sin(rotation) * yTransformed;
			yTransformed = -Math.sin(rotation) * xTmp + Math.cos(rotation) * yTransformed;
		}
		
		// Move to world coordinates
		if (location != null) {
			xTransformed = location.getLongitude() + xTransformed * Ellipsoid.meterToLongitude(location.getLatitude());
			yTransformed = location.getLatitude() + yTransformed * Ellipsoid.meterToLatitude(location.getLatitude());
			zTransformed += location.getAltitude();
		}
		return new EarthCoordinate(zTransformed, yTransformed, xTransformed);
	}
}
