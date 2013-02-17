package kmlframework.kml;

public class Create extends UpdateElement {

	public Create() {}
	
	public Create(KmlObject kmlObject) {
		setKmlObject(kmlObject);
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<Create>", 1);
		getKmlObject().write(kml);
		kml.println(-1, "</Create>");
	}
}
