package org.boehn.kmlframework.kml;

public class SimpleData extends KmlObject {

	private String name;
	private String value;
	
	public SimpleData() {}
	
	public SimpleData(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<SimpleData name=\"" + name +"\">" + value + "</SimpleData>");
	}
}
