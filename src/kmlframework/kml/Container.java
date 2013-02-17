package kmlframework.kml;

import java.util.ArrayList;
import java.util.List;

import kmlframework.atom.AtomAuthor;
import kmlframework.atom.AtomLink;


public abstract class Container extends Feature {
	List<Feature> features;
	
	public Container() {
	}
	
	public Container(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor, AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber, String snippet, Integer snippetMaxLines,String description, AbstractView abstractView, TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors, Region region, ExtendedData extendedData, List<Feature> feauters) {
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails, phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive, styleUrl, styleSelectors, region, extendedData);
		this.features = feauters;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	
	public void addFeature(Feature feature) {
		if (features == null) {
			features = new ArrayList<Feature>();
		}
		features.add(feature);
	}
	
	public void writeInner(Kml kml) throws KmlException {
		super.writeInner(kml);
		if (features != null) {
			for (Feature feature: features) {
				feature.write(kml);
			}
		}
	}
}
