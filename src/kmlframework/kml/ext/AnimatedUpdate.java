/*
 * AnimatedUpdate.java Created Oct 12, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml.ext;

public class AnimatedUpdate extends TourPrimitive
{
	private Double duration;

	private kmlframework.kml.Update update;

	public AnimatedUpdate(kmlframework.kml.Update update)
	{
		this.update = update;
		if(this.update.getTargetHref() == null)
			this.update.setTargetHref("");
	}

	public Double getDuration()
	{
		return duration;
	}

	public void setDuration(Double duration)
	{
		this.duration = duration;
	}

	public kmlframework.kml.Update getUpdate()
	{
		return update;
	}

	public void setUpdate(kmlframework.kml.Update update)
	{
		this.update = update;
	}

	@Override
	public void write(kmlframework.kml.Kml kml)
		throws kmlframework.kml.KmlException
	{
		kml.println("<gx:AnimatedUpdate" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if(duration != null)
			kml.println("<gx:duration>" + duration + "</gx:duration>");
		update.write(kml);
		kml.println(-1, "</gx:AnimatedUpdate>");
	}
}
