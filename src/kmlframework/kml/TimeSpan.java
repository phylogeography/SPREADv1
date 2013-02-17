package kmlframework.kml;

public class TimeSpan extends TimePrimitive {

	private String begin;
	private String end;
	
	public TimeSpan() {}
	
	public TimeSpan(String begin, String end) {
		this.begin = begin;
		this.end = end;
	}
	
	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<TimeSpan" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (begin != null) {
			kml.println("<begin>" + begin + "</begin>");
		}
		if (end != null) {
			kml.println("<end>" + end + "</end>");
		}
		kml.println(-1, "</TimeSpan>");
	}
}