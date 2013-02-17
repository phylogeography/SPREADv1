package org.boehn.kmlframework.kml;

public class Alias extends KmlObject {

	private String targetHref;
	private String sourceHref;
	
	public Alias() {}
	
	public Alias(String targetHref, String sourceHref) {
		this.targetHref = targetHref;
		this.sourceHref = sourceHref;
	}
	
	public String getTargetHref() {
		return targetHref;
	}

	public void setTargetHref(String targetHref) {
		this.targetHref = targetHref;
	}

	public String getSourceHref() {
		return sourceHref;
	}

	public void setSourceHref(String sourceHref) {
		this.sourceHref = sourceHref;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Alias" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (targetHref != null) {
			kml.println("<targetHref>" + targetHref + "</targetHref>");
		}
		if (sourceHref != null) {
			kml.println("<sourceHref>" + sourceHref + "</sourceHref>");
		}
		kml.println(-1, "</Alias>");
	}
}