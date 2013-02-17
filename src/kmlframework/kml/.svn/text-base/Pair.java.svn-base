package org.boehn.kmlframework.kml;

public class Pair extends KmlObject {

	private StyleStateEnum key;
	private String styleUrl;
	
	public Pair() {}
	
	public Pair(StyleStateEnum key, String styleUrl) {
		this.key = key;
		this.styleUrl = styleUrl;
	}
	
 	public StyleStateEnum getKey() {
		return key;
	}

	public void setKey(StyleStateEnum key) {
		this.key = key;
	}

	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	public void write(Kml kml) throws KmlException {
		// We validate the data
		if (key == null) {
			throw new KmlException("Key missing for Pair");
		}
		if (styleUrl == null) {
			throw new KmlException("StyleUrl missing for Pair");
		}
		kml.println("<Pair" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<key>" + key + "</key>");
		kml.println("<styleUrl>" + styleUrl + "</styleUrl>");
		kml.println(-1, "</Pair>");
	}
}