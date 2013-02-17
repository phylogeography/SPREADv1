/*
 * Playlist.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package org.boehn.kmlframework.kml.ext;

public class Playlist extends org.boehn.kmlframework.kml.KmlObject
{
	private final java.util.ArrayList<TourPrimitive> elements;

	public Playlist()
	{
		elements = new java.util.ArrayList<TourPrimitive>();
	}

	public java.util.List<TourPrimitive> getElements()
	{
		return elements;
	}

	public void write(org.boehn.kmlframework.kml.Kml kml)
		throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Playlist" + getIdAndTargetIdFormatted(kml) + ">", 1);
		for(TourPrimitive element : elements)
			element.write(kml);
		kml.println(-1, "</gx:Playlist>");
	}

	public void writeDelete(org.boehn.kmlframework.kml.Kml kml)
		throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Playlist" + getIdAndTargetIdFormatted(kml) + "></gx:Playlist>");
	}
}
