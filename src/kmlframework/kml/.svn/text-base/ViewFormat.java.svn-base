package org.boehn.kmlframework.kml;

public class ViewFormat extends KmlObject {

	private boolean includeBBox = true;
	private boolean includeCamera = true;
	private boolean includeView = true;
	
	public ViewFormat() {}
	
	public ViewFormat(boolean includeBBox, boolean includeCamera, boolean includeView) {
		this.includeBBox = includeBBox;
		this.includeCamera = includeCamera;
		this.includeView = includeView;
	}
	
	public void write(Kml kml) throws KmlException {
		kml.print("<viewFormat" + getIdAndTargetIdFormatted(kml) + ">");
		if (includeBBox) {
			kml.print("BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]");
		}
		if (includeBBox && includeCamera) {
			kml.print(";");
		}
		if (includeCamera) {
			kml.print("CAMERA=[lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading]");
		}
		if (includeCamera && includeView) {
			kml.print(";");
		}
		if (includeView) {
			kml.print("VIEW=[horizFov],[vertFov],[horizPixels],[vertPixels],[terrainEnabled]");
		}
		kml.println("</viewFormat>");
	}
}