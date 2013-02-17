package kmlframework.kml;

public class SimpleField extends KmlObject {

	private SimpleFieldTypeEnum simpleFieldType;
	private String name;
	private String displayName;
	
	public SimpleField() {}
	
	public SimpleField(SimpleFieldTypeEnum simpleFieldType, String name, String displayName) {
		this.simpleFieldType = simpleFieldType;
		this.name = name;
		this.displayName = displayName;
	}
	
	public SimpleFieldTypeEnum getSimpleFieldType() {
		return simpleFieldType;
	}

	public void setSimpleFieldType(SimpleFieldTypeEnum simpleFieldType) {
		this.simpleFieldType = simpleFieldType;
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

	public void write(Kml kml) throws KmlException {
		kml.println("<SimpleField" + getIdAndTargetIdFormatted(kml) + (simpleFieldType != null ? " type=\"" + enumToString(simpleFieldType) + "\"" : "") + (name != null ? " name=\"" + name + "\"" : "") + ">", 1);
		if (displayName != null) {
			kml.println("<displayName>" + displayName + "</displayName>");
		}
		kml.println(-1, "</SimpleField>");
	}
}