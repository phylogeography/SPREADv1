package gui;

public class TableRecord {

	protected String location;
	protected String longitude;
	protected String latitude;

	public TableRecord() {
		location = "";
		longitude = "";
		latitude = "";
	}

	public TableRecord(String location, String longitude, String latitude) {
		this.location = location;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
