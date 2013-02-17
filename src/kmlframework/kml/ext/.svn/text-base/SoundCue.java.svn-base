/*
 * SoundCue.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package org.boehn.kmlframework.kml.ext;

public class SoundCue extends TourPrimitive
{
	private String href;

	public SoundCue(String href)
	{
		this.href = href;
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	@Override
	public void write(org.boehn.kmlframework.kml.Kml kml)
		throws org.boehn.kmlframework.kml.KmlException
	{
		kml.println("<gx:SoundCue" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<href>" + href + "</href>");
		kml.println(-1, "</gx:SoundCue>");
	}
}
