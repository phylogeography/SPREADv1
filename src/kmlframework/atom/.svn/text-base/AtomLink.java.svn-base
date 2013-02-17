package org.boehn.kmlframework.atom;

import org.boehn.kmlframework.kml.Kml;
import org.boehn.kmlframework.kml.KmlException;

public class AtomLink {

	private String href;

	public AtomLink() {}
	
	public AtomLink(String href) {
		this.href = href;
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	public void write(Kml kml) throws KmlException {
		if (href == null) {
			throw new KmlException("href not set for atom:Link");
		}
		kml.println("<atom:link href=\"" + href + "\" />");
		kml.setAtomElementsIncluded(true);
	}
	
}
