package kmlframework.kml;

import java.util.List;

public class MultiGeometry extends Geometry {

	private List<Geometry> geometries;
	
	public MultiGeometry() {}
	
	public MultiGeometry(List<Geometry> geometries) {
		this.geometries = geometries;
	}
	
	public List<Geometry> getGeometries() {
		return geometries;
	}

	public void setGeometries(List<Geometry> geometries) {
		this.geometries = geometries;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<MultiGeometry" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (geometries != null) {
			for (Geometry geometry : geometries) {
				geometry.write(kml);
			}
		}
		kml.println(-1, "</MultiGeometry>");
	}
}