package kmlframework.kml;

import java.util.List;

public class StyleMap extends StyleSelector {

	private List<Pair> pairs;
	
	public StyleMap() {}
	
	public StyleMap(List<Pair> pairs) {
		this.pairs = pairs;
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<StyleMap" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (pairs != null) {
			for (Pair pair : pairs) {
				pair.write(kml);
			}
		}
		kml.println(-1, "</StyleMap>");
	}
}
