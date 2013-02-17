package org.boehn.kmlframework.kml;

import java.util.List;

import org.boehn.kmlframework.atom.AtomAuthor;
import org.boehn.kmlframework.atom.AtomLink;

public abstract class Overlay extends Feature {

	private String color;
	private Integer drawOrder;
	private Icon icon;

	public Overlay() {}
	
	public Overlay(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor, AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber, String snippet, Integer snippetMaxLines,String description, AbstractView abstractView, TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors, Region region, ExtendedData extendedData, String color, Integer drawOrder, Icon icon) {
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails, phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive, styleUrl, styleSelectors, region, extendedData);
		this.color = color;
		this.drawOrder = drawOrder;
		this.icon = icon;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public Integer getDrawOrder() {
		return drawOrder;
	}
	
	public void setDrawOrder(Integer drawOrder) {
		this.drawOrder = drawOrder;
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	public void writeInner(Kml kml) throws KmlException {
		super.writeInner(kml);
		if (color != null) {
			kml.println("<color>" + color + "</color>");
		}
		if (drawOrder != null) {
			kml.println("<drawOrder>" + drawOrder + "</drawOrder>");
		}
		if (icon != null) {
			icon.write(kml);
		}
	}
}