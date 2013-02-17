/*
 * Wait.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package org.boehn.kmlframework.kml.ext;

public class Wait extends TourPrimitive
{
	private double duration;

	public Wait(double duration)
	{
		this.duration = duration;
	}

	public double getDuration()
	{
		return duration;
	}

	public void setDuration(double duration)
	{
		this.duration = duration;
	}

	public void write(org.boehn.kmlframework.kml.Kml kml)
		throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Wait" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<gx:duration>" + duration + "</gx:duration>");
		kml.println(-1, "</gx:Wait>");
	}

	public void writeDelete(org.boehn.kmlframework.kml.Kml kml)
		throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Wait" + getIdAndTargetIdFormatted(kml) + "></gx:Wait>");
	}
}
