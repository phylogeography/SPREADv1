package kmlframework.kml;

public class ListStyle extends KmlObject {

	private ListItemTypeEnum listItemType;
	private String bgColor;
	private String itemIconState;
	private String href;
	
	public ListStyle() {}
	
	public ListStyle(ListItemTypeEnum listItemType, String bgColor, String itemIconState, String href) {
		this.listItemType = listItemType;
		this.bgColor = bgColor;
		this.itemIconState = itemIconState;
		this.href = href;
	}
	
	public ListItemTypeEnum getListItemType() {
		return listItemType;
	}

	public void setListItemType(ListItemTypeEnum listItemType) {
		this.listItemType = listItemType;
	}

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getItemIconState() {
		return itemIconState;
	}

	public void setItemIconState(String itemIconState) {
		this.itemIconState = itemIconState;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void write(Kml kml) throws KmlException {
		kml.println("<ListStyle" + getIdAndTargetIdFormatted(kml) + ">", 1);
		if (listItemType != null) {
			kml.println("<listItemType>" + listItemType + "</listItemType>");
		}
		if (bgColor != null) {
			kml.println("<bgColor>" + bgColor + "</bgColor>");
		}
		if (itemIconState != null || href != null) {
			kml.println("<ItemIcon>", 1);
			if (itemIconState != null) {
				kml.println("<state>" + itemIconState + "</state>");
			}
			if (href != null) {
				kml.println("<href>" + href + "</href>");
			}			
			kml.println(-1, "</ItemIcon>");
		}
		kml.println(-1, "</ListStyle>");
	}
}