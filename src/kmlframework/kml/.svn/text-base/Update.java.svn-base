package org.boehn.kmlframework.kml;

import java.util.List;

public class Update extends KmlObject {

	private String targetHref;
	private List<UpdateElement> updateElements;
	
	public String getTargetHref() {
		return targetHref;
	}

	public void setTargetHref(String targetHref) {
		this.targetHref = targetHref;
	}

	public List<UpdateElement> getUpdateElements() {
		return updateElements;
	}

	public void setUpdateElements(List<UpdateElement> updateElements) {
		this.updateElements = updateElements;
	}

	public void write(Kml kml) throws KmlException {
		// We validate the data
		if (targetHref == null) {
			throw new KmlException("targetHref cannot be null in Update");
		}
		kml.println("<Update" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<targetHref>" + targetHref + "</targetHref>");
		if (updateElements != null) {
			for (UpdateElement updateElement : updateElements) {
				updateElement.write(kml);
			}
		}
		kml.println(-1, "</Update>");
	}
}
