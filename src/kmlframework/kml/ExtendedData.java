package kmlframework.kml;

import java.util.List;

public class ExtendedData extends KmlObject {

	private List<Data> dataElements;
	private String schemaUrl;
	private List<SimpleData> simpleDataElements;
	private String nameSpace;
	private String customContent;
	
	public ExtendedData() {}
	
	public ExtendedData(List<Data> dataElements, String schemaUrl, List<SimpleData> simpleDataElements, String nameSpace, String customContent) {
		this.dataElements = dataElements;
		this.schemaUrl = schemaUrl;
		this.simpleDataElements = simpleDataElements;
		this.nameSpace = nameSpace;
		this.customContent = customContent;
	}
	
	public List<Data> getDataElements() {
		return dataElements;
	}

	public void setDataElements(List<Data> dataElements) {
		this.dataElements = dataElements;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}

	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public List<SimpleData> getSimpleDataElements() {
		return simpleDataElements;
	}

	public void setSimpleDataElements(List<SimpleData> simpleDataElements) {
		this.simpleDataElements = simpleDataElements;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getCustomContent() {
		return customContent;
	}

	public void setCustomContent(String customContent) {
		this.customContent = customContent;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<ExtendedData" + getIdAndTargetIdFormatted(kml) + (nameSpace != null ? " mlns:prefix=\"" + nameSpace + "\"" : "") + ">", 1);
		if (dataElements != null) {
			for (Data data : dataElements) {
				data.write(kml);
			}
		}
		if (schemaUrl != null || simpleDataElements != null) {
			kml.println("<SchemaData" + (schemaUrl != null ? " schemaUrl=\"" + schemaUrl + "\"" : "") + ">", 1);
			if (simpleDataElements != null) {
				for (SimpleData simpleData : simpleDataElements) {
					simpleData.write(kml);
				}
			}
			kml.println("</SchemaData>");
		}
		if (customContent != null) {
			kml.println(customContent);
		}
		kml.println(-1, "</ExtendedData>");
	}
}
