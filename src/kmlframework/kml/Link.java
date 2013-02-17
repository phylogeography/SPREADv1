package kmlframework.kml;

public class Link extends KmlObject {

	private String href;
	private RefreshModeEnum refreshMode;
	private Double refreshInterval;
	private ViewRefreshModeEnum viewRefreshMode;
	private Double viewRefreshTime;
	private Double viewBoundScale;
	private ViewFormat viewFormat;
	private String httpQuery;
	
	public Link() {}
	
	public Link(String href, RefreshModeEnum refreshMode, Double refreshInterval, ViewRefreshModeEnum viewRefreshMode, Double viewRefreshTime, Double viewBoundScale, ViewFormat viewFormat, String httpQuery) {
		this.href = href;
		this.refreshMode = refreshMode;
		this.refreshInterval = refreshInterval;
		this.viewRefreshMode = viewRefreshMode;
		this.viewRefreshTime = viewRefreshTime;
		this.viewFormat = viewFormat;
		this.httpQuery = httpQuery;
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public RefreshModeEnum getRefreshMode() {
		return refreshMode;
	}

	public void setRefreshMode(RefreshModeEnum refreshMode) {
		this.refreshMode = refreshMode;
	}

	public Double getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(Double refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public ViewRefreshModeEnum getViewRefreshMode() {
		return viewRefreshMode;
	}

	public void setViewRefreshMode(ViewRefreshModeEnum viewRefreshMode) {
		this.viewRefreshMode = viewRefreshMode;
	}

	public Double getViewRefreshTime() {
		return viewRefreshTime;
	}

	public void setViewRefreshTime(Double viewRefreshTime) {
		this.viewRefreshTime = viewRefreshTime;
	}

	public Double getViewBoundScale() {
		return viewBoundScale;
	}

	public void setViewBoundScale(Double viewBoundScale) {
		this.viewBoundScale = viewBoundScale;
	}

	public ViewFormat getViewFormat() {
		return viewFormat;
	}

	public void setViewFormat(ViewFormat viewFormat) {
		this.viewFormat = viewFormat;
	}

	public String getHttpQuery() {
		return httpQuery;
	}

	public void setHttpQuery(String httpQuery) {
		this.httpQuery = httpQuery;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Link" + getIdAndTargetIdFormatted(kml) + ">", 1);
		writeInner(kml);
		kml.println(-1, "</Link>");
	}

	protected void writeInner(Kml kml) throws KmlException {
		if (href != null) {
			kml.println("<href>" + href + "</href>");
		}
		if (refreshMode != null) {
			kml.println("<refreshMode>" + refreshMode + "</refreshMode>");
		}
		if (refreshInterval != null) {
			kml.println("<refreshInterval>" + refreshInterval + "</refreshInterval>");
		}
		if (viewRefreshMode != null) {
			kml.println("<viewRefreshMode>" + viewRefreshMode + "</viewRefreshMode>");
		}
		if (viewRefreshTime != null) {
			kml.println("<viewRefreshTime>" + viewRefreshTime + "</viewRefreshTime>");
		}
		if (viewBoundScale != null) {
			kml.println("<viewBoundScale>" + viewBoundScale + "</viewBoundScale>");
		}
		if (viewFormat != null) {
			viewFormat.write(kml);
		}
		if (httpQuery != null) {
			kml.println("<httpQuery>" + httpQuery + "</httpQuery>");
		}
	}
}