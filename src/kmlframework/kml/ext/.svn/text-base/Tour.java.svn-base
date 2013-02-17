/*
 * Tour.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package org.boehn.kmlframework.kml.ext;

import org.boehn.kmlframework.kml.Kml;

public class Tour extends org.boehn.kmlframework.kml.Feature
{
	private Playlist playlist;

	public Playlist getPlaylist()
	{
		return playlist;
	}

	public void setPlaylist(Playlist playlist)
	{
		this.playlist = playlist;
	}

	public void write(Kml kml) throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Tour" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if(getName() != null)
			kml.println("<name>" + getName() + "</name>");
		if(getDescription() != null)
			kml.println("<description>" + getDescription() + "</description>");
		if(playlist != null)
			playlist.write(kml);
		kml.println(-1, "</gx:Tour>");
	}

	public void writeDelete(Kml kml) throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:Tour" + getIdAndTargetIdFormatted(kml) + "></gx:Tour>");
	}
}
