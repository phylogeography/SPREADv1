package org.boehn.kmlframework.kml;

public class TimeStamp extends TimePrimitive {

	private String when;
	
	public TimeStamp() {}
	
	public TimeStamp(String when) {
		this.when = when;
	}
	
	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<TimeStamp" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (when != null) {
			kml.println("<when>" + when + "</when>");
		}
		kml.println(-1, "</TimeStamp>");
	}
}