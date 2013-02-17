package org.boehn.kmlframework.kml;


public class NetworkLinkControl extends KmlObject {

	private Double minRefreshPeriod;
	private Double maxSessionLength;
	private String cookie;
	private String message;
	private String linkName;
	private String linkDescription;
	private String linkSnippet;
	private Integer linkSnippetMaxLines;
	private String expires;
	private Update update;
	private AbstractView abstractView;
	
	public NetworkLinkControl() {}
	
	public NetworkLinkControl(Double minRefreshPeriod, Double maxSessionLength, String cookie, String message, String linkName, String linkDescription, String linkSnippet, Integer linkSnippetMaxLines, String expires, Update update, AbstractView abstractView) {
		this.minRefreshPeriod = minRefreshPeriod;
		this.maxSessionLength = maxSessionLength;
		this.cookie = cookie;
		this.message = message;
		this.linkName = linkName;
		this.linkDescription = linkDescription;
		this.linkSnippet = linkSnippet;
		this.linkSnippetMaxLines = linkSnippetMaxLines;
		this.expires = expires;
		this.update = update;
		this.abstractView = abstractView;
	}
	
	public Double getMinRefreshPeriod() {
		return minRefreshPeriod;
	}

	public void setMinRefreshPeriod(Double minRefreshPeriod) {
		this.minRefreshPeriod = minRefreshPeriod;
	}

	public Double getMaxSessionLength() {
		return maxSessionLength;
	}

	public void setMaxSessionLength(Double maxSessionLength) {
		this.maxSessionLength = maxSessionLength;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public String getLinkSnippet() {
		return linkSnippet;
	}

	public void setLinkSnippet(String linkSnippet) {
		this.linkSnippet = linkSnippet;
	}

	public Integer getLinkSnippetMaxLines() {
		return linkSnippetMaxLines;
	}

	public void setLinkSnippetMaxLines(Integer linkSnippetMaxLines) {
		this.linkSnippetMaxLines = linkSnippetMaxLines;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Update getUpdate() {
		return update;
	}

	public void setUpdate(Update update) {
		this.update = update;
	}

	public AbstractView getAbstractView() {
		return abstractView;
	}

	public void setAbstractView(AbstractView abstractView) {
		this.abstractView = abstractView;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<NetworkLinkControl" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (minRefreshPeriod != null) {
			kml.println("<minRefreshPeriod>" + minRefreshPeriod + "</minRefreshPeriod>");
		}
		if (maxSessionLength != null) {
			kml.println("<maxSessionLength>" + maxSessionLength + "</maxSessionLength>");
		}
		if (cookie != null) {
			kml.println("<cookie>" + cookie + "</cookie>");
		}
		if (message != null) {
			kml.println("<message>" + message + "</message>");
		}
		if (linkName != null) {
			kml.println("<linkName>" + linkName + "</linkName>");
		}
		if (linkDescription != null) {
			kml.println("<linkDescription>" + linkDescription + "</linkDescription>");
		}
		if (linkSnippet != null) {
			kml.println("<linkSnippet maxLines=\"" + (linkSnippetMaxLines != null ? linkSnippetMaxLines : "2") + "\">" + linkSnippet + "</linkSnippet>");
		}
		if (expires != null) {
			kml.println("<expires>" + expires + "</expires>");
		}
		if (update != null) {
			update.write(kml);
		}
		if (abstractView != null) {
			abstractView.write(kml);
		}
		kml.println(-1, "</NetworkLinkControl>");
	}
}