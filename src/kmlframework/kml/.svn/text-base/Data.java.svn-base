package org.boehn.kmlframework.kml;

public class Data extends KmlObject {

	private String name;
	private String displayName;
	private String value;
	
	public Data() {}
	
	public Data(String name, String displayName, String value) {
		this.name = name;
		this.displayName = displayName;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Data name=\"" + name +"\">", 1);
		if (displayName != null) {
			kml.println("<displayName>" + displayName + "</displayName>");
		}
		if (value != null) {
			kml.println("<value>" + value + "</value>");
		}
		kml.println(-1, "</Data>");
	}
}
