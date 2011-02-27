package structure;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrew Rambaut
 * @version $Id$
 */
public class TimeLine {

	public TimeLine(final double startTime, final double endTime,
			final int sliceCount) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.sliceCount = sliceCount;
	}

	public double getStartTime() {
		return startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public int getSliceCount() {
		return sliceCount;
	}

	public boolean isInstantaneous() {
		return sliceCount == 0 || startTime == endTime;
	}

	public Date getDate(final double startTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis((long) startTime);
		return cal.getTime();
	}

	private final double startTime;
	private final double endTime;
	private final int sliceCount;
}
