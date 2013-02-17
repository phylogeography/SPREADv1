package org.boehn.kmlframework.kml;

import java.util.List;

public class Schema extends KmlObject {

	private List<SimpleField> simpleFields;
	
	public Schema() {}
	
	public Schema(List<SimpleField> simpleFields) {
		this.simpleFields = simpleFields;
	}
	
	public List<SimpleField> getSimpleFields() {
		return simpleFields;
	}

	public void setSimpleFields(List<SimpleField> simpleFields) {
		this.simpleFields = simpleFields;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<Schema" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (simpleFields != null) {
			for (SimpleField simpleField : simpleFields) {
				simpleField.write(kml);
			}
		}
		kml.println(-1, "</Schema>");
	}
}