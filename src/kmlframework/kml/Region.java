package kmlframework.kml;

public class Region extends KmlObject {

	private Double north;
	private Double south;
	private Double east;
	private Double west;
	private Double minAltitude;
	private Double maxAltitude;
	private AltitudeModeEnum altitudeMode;
	private Double minLodPixels;
	private Double maxLodPixels;
	private Double minFadeExtent;
	private Double maxFadeExtent;
	
	public Region() {}
	
	public Region(Double north, Double south, Double east, Double west, Double minAltitude, Double maxAltitude, AltitudeModeEnum altitudeMode, Double minLodPixels, Double maxLodPixels, Double minFadeExtent, Double maxFadeExtent) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.minAltitude = minAltitude;
		this.maxAltitude = maxAltitude;
		this.altitudeMode = altitudeMode;
		this.minLodPixels = minLodPixels;
		this.maxLodPixels = maxLodPixels;
		this.minFadeExtent = minFadeExtent;
		this.maxFadeExtent = maxFadeExtent;
	}
	
	public Double getNorth() {
		return north;
	}

	public void setNorth(Double north) {
		this.north = north;
	}

	public Double getSouth() {
		return south;
	}

	public void setSouth(Double south) {
		this.south = south;
	}

	public Double getEast() {
		return east;
	}

	public void setEast(Double east) {
		this.east = east;
	}

	public Double getWest() {
		return west;
	}

	public void setWest(Double west) {
		this.west = west;
	}

	public Double getMinAltitude() {
		return minAltitude;
	}

	public void setMinAltitude(Double minAltitude) {
		this.minAltitude = minAltitude;
	}

	public Double getMaxAltitude() {
		return maxAltitude;
	}

	public void setMaxAltitude(Double maxAltitude) {
		this.maxAltitude = maxAltitude;
	}

	public AltitudeModeEnum getAltitudeMode() {
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode) {
		this.altitudeMode = altitudeMode;
	}

	public Double getMinLodPixels() {
		return minLodPixels;
	}

	public void setMinLodPixels(Double minLodPixels) {
		this.minLodPixels = minLodPixels;
	}

	public Double getMaxLodPixels() {
		return maxLodPixels;
	}

	public void setMaxLodPixels(Double maxLodPixels) {
		this.maxLodPixels = maxLodPixels;
	}

	public Double getMinFadeExtent() {
		return minFadeExtent;
	}

	public void setMinFadeExtent(Double minFadeExtent) {
		this.minFadeExtent = minFadeExtent;
	}

	public Double getMaxFadeExtent() {
		return maxFadeExtent;
	}

	public void setMaxFadeExtent(Double maxFadeExtent) {
		this.maxFadeExtent = maxFadeExtent;
	}

	public void write(Kml kml) throws KmlException {
		// We validate the data
		if (north == null) {
			throw new KmlException("north is required in Region");
		}
		if (south == null) {
			throw new KmlException("south is required in Region");
		}
		if (east == null) {
			throw new KmlException("east is required in Region");
		}
		if (west == null) {
			throw new KmlException("west is required in Region");
		}
		
		kml.println("<Region" + getIdAndTargetIdFormatted(kml) + ">", 1);
		kml.println("<LatLonAltBox>", 1);
		kml.println("<north>" + north + "</north>");
		kml.println("<south>" + south + "</south>");
		kml.println("<east>" + east + "</east>");
		kml.println("<west>" + west + "</west>");
		kml.println(-1, "<LatLonAltBox>");
		kml.println(-1, "</LatLonAltBox>");
		if (minAltitude != null) {
			kml.println("<minAltitude>" + minAltitude + "</minAltitude>");
		}
		if (maxAltitude != null) {
			kml.println("<maxAltitude>" + maxAltitude + "</maxAltitude>");
		}
		if (altitudeMode!= null) {
			kml.println("<altitudeMode>" + altitudeMode + "</altitudeMode>");
		}
		if (minLodPixels != null || maxLodPixels != null || minFadeExtent != null || maxFadeExtent != null) {
			kml.println("<Lod>", 1);
			if (minLodPixels != null) {
				kml.println("<minLodPixels>" + minLodPixels + "</minLodPixels>");
			}
			if (maxLodPixels != null) {
				kml.println("<maxLodPixels>" + maxLodPixels + "</maxLodPixels>");
			}
			if (minFadeExtent != null) {
				kml.println("<minFadeExtent>" + minFadeExtent + "</minFadeExtent>");
			}
			if (minFadeExtent != null) {
				kml.println("<minFadeExtent>" + minFadeExtent + "</minFadeExtent>");
			}
			if (maxFadeExtent != null) {
				kml.println("<maxFadeExtent>" + maxFadeExtent + "</maxFadeExtent>");
			}
			kml.println(-1, "<Lod>");
			kml.println(-1, "</Lod>");
		}
		kml.println(-1, "</Region>");
	}
}