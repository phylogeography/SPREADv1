package org.boehn.kmlframework.kml;

public abstract class UpdateElement {

	private KmlObject kmlObject;

	public UpdateElement() {}
	
	public UpdateElement(KmlObject kmlObject) {
		this.kmlObject = kmlObject;
	}
	
	public KmlObject getKmlObject() {
		return kmlObject;
	}

	public void setKmlObject(KmlObject kmlObject) {
		this.kmlObject = kmlObject;
	}
	
	public abstract void write(Kml kml) throws KmlException;
}
