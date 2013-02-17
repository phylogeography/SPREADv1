package org.boehn.kmlframework.kml;

public class Icon extends Link {

	public Icon() {}
	
	public Icon(String href, RefreshModeEnum refreshMode, Double refreshInterval, ViewRefreshModeEnum viewRefreshMode, Double viewRefreshTime, Double viewBoundScale, ViewFormat viewFormat, String httpQuery) {
		super(href, refreshMode, refreshInterval, viewRefreshMode, viewRefreshTime, viewBoundScale, viewFormat, httpQuery);
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<Icon" + getIdAndTargetIdFormatted(kml) + ">", 1);
		writeInner(kml);
		kml.println(-1, "</Icon>");
	}
}
