package org.boehn.kmlframework.kml;

import java.util.UUID;

public abstract class KmlObject {

	private String id;
	private String targetId;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTargetId() {
		return targetId;
	}
	
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public abstract void write(Kml kml) throws KmlException;
	
	protected String getIdAndTargetIdFormatted(Kml kml) {
		if (kml.isGenerateObjectIds() && id == null) {
			setId("id"+UUID.randomUUID().toString());
		}
		String result = "";
		if (id != null) {
			result += " id=\"" + id + "\"";
		}
		if (targetId != null) {
			result += " targetId=\"" + targetId + "\"";
		}
		return result;
	}
	
	public static int booleanToInt(boolean booleanValue) {
		return (booleanValue? 1 : 0);
	}
	
	@SuppressWarnings("unchecked")
	public static String enumToString(Enum _enum) {
		if (_enum.toString().startsWith("_")) {
			return _enum.toString().substring(1);
		} else {
			return _enum.toString();
		}
	}
}
