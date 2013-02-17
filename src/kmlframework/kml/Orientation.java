/*
 * Location.java Created Oct 13, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml;

public class Orientation extends KmlObject
{
	private Double heading;
	private Double tilt;
	private Double roll;

	public Orientation() {
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

	public void write(Kml kml) throws KmlException {
		kml.println("<Orientation" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if(heading!=null)
			kml.println("<heading>"+heading+"</heading>");
		if(tilt!=null)
			kml.println("<tilt>"+tilt+"</tilt>");
		if(roll!=null)
			kml.println("<roll>"+roll+"</roll>");
		kml.println(-1, "</Orientation>");
	}
}
