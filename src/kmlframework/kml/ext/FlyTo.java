/*
 * FlyTo.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml.ext;

public class FlyTo extends TourPrimitive
{
	public static enum FlyToMode
	{
		BOUNCE("bounce"), SMOOTH("smooth");

		public final String kml;

		FlyToMode(String kml)
		{
			this.kml = kml;
		}
	}

	private Double duration;

	private FlyToMode flyToMode;

	private kmlframework.kml.AbstractView view;

	public FlyTo(kmlframework.kml.AbstractView view)
	{
		this.view = view;
		flyToMode = FlyToMode.BOUNCE;
	}

	public Double getDuration()
	{
		return duration;
	}

	public void setDuration(Double duration)
	{
		this.duration = duration;
	}

	public FlyToMode getFlyToMode()
	{
		return flyToMode;
	}

	public void setFlyToMode(FlyToMode flyToMode)
	{
		this.flyToMode = flyToMode;
	}

	public kmlframework.kml.AbstractView getView()
	{
		return view;
	}

	public void setView(kmlframework.kml.AbstractView view)
	{
		this.view = view;
	}

	public void write(kmlframework.kml.Kml kml)
		throws kmlframework.kml.KmlException
	{
		kml.println("<gx:FlyTo" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if(duration != null)
			kml.println("<gx:duration>" + duration + "</gx:duration>");
		if(flyToMode != null)
			kml.println("<gx:flyToMode>" + flyToMode.kml + "</gx:flyToMode>");
		view.write(kml);
		kml.println(-1, "</gx:FlyTo>");
	}

	public void writeDelete(kmlframework.kml.Kml kml)
		throws kmlframework.kml.KmlException
	{
		kml.println("<gx:FlyTo" + getIdAndTargetIdFormatted(kml) + "></gx:FlyTo>");
	}
}
