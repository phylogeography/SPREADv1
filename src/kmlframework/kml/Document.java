package kmlframework.kml;

import java.util.ArrayList;
import java.util.List;

import kmlframework.atom.AtomAuthor;
import kmlframework.atom.AtomLink;


public class Document extends Container implements Deletable {

	private List<Schema> schemas;
	
	public Document() {}
	
	public Document(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor, AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber, String snippet, Integer snippetMaxLines,String description, AbstractView abstractView, TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors, Region region, ExtendedData extendedData, List<Feature> feauters, List<Schema> schemas) {
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails, phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive, styleUrl, styleSelectors, region, extendedData, feauters);
		this.schemas = schemas;
	}
	
	public List<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}
	
	public void addSchema(Schema schema) {
		if (schemas == null) {
			schemas = new ArrayList<Schema>();
		}
		schemas.add(schema);
	}
	
	public void write(Kml kml) throws KmlException {
		kml.println("<Document" + getIdAndTargetIdFormatted(kml) + ">", 1);
		writeInner(kml);
		if (schemas != null) {
			for (Schema schema: schemas)
			schema.write(kml);
		}
		kml.println(-1, "</Document>");
	}
	
	public void writeDelete(Kml kml) throws KmlException {
		kml.println("<Document" + getIdAndTargetIdFormatted(kml) + "></Document>");
	}
}
