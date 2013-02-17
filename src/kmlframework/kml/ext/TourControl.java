/*
 * TourControl.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml.ext;

public class TourControl extends TourPrimitive
{
	public static enum PlayModeEnum
	{
		PAUSE("pause");

		public final String kml;

		PlayModeEnum(String kml)
		{
			this.kml = kml;
		}
	}

	private PlayModeEnum playMode;

	/** Shortened form for creating a pause */
	public TourControl()
	{
		this(PlayModeEnum.PAUSE);
	}

	public TourControl(PlayModeEnum playMode)
	{
		this.playMode = playMode;
	}

	@Override
	public void write(kmlframework.kml.Kml kml)
		throws kmlframework.kml.KmlException
	{
		kml.println("<gx:TourControl" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<gx:playMode>" + playMode.kml + "</gx:playMode>");
		kml.println(-1, "</gx:TourControl>");
	}
}
