/*
 * Deletable.java Created Oct 14, 2010 by Andrew Butler, PSL
 */
package kmlframework.kml;

/**
 * Only items that implement this interface can be deleted dynamically from a KML document using
 * {@link Delete}. As of KML 2.2, the only items that may be deleted are {@link Document},
 * {@link Folder}, {@link Placemark}, {@link GroundOverlay}, and {@link ScreenOverlay}.
 */
public interface Deletable
{
	/**
	 * Writes an abbreviated version of the KML element--an empty element with a single attribute,
	 * the targetId of the object to delete
	 * 
	 * @param kml The KML document to write the element to
	 * @throws KmlException If an error occurs writing the element
	 */
	public abstract void writeDelete(Kml kml) throws KmlException;
}
