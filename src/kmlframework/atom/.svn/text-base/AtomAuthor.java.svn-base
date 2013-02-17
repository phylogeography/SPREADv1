package org.boehn.kmlframework.atom;

import org.boehn.kmlframework.kml.Kml;
import org.boehn.kmlframework.kml.KmlException;

public class AtomAuthor {

	private String name;
	private String uri;
	private String email;
	
	public AtomAuthor() {}
	
	public AtomAuthor(String name, String uri, String email) {
		this.name = name;
		this.uri = uri;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AtomAuthor(String name) {
		this.name = name;
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<atom:author>", 1);
		if (name != null) {
			kml.println("<atom:name>" + name + "</atom:name>");
		}
		if (uri != null) {
			kml.println("<atom:uri>" + uri + "</atom:uri>");
		}
		if (email != null) {
			kml.println("<atom:email>" + email + "</atom:email>");
		}
		kml.println(-1, "</atom:author>");
		kml.setAtomElementsIncluded(true);
	}
}
