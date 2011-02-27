package structure;

import java.util.List;

/**
 * @author Andrew Rambaut
 * @version $Id$
 */
public class Polygon extends GeoItem {

	public Polygon(final List<Coordinates> vertices, final Style style) {
		this(null, vertices, style, -1.0, -1.0);
	}

	public Polygon(final List<Coordinates> vertices, final Style style,
			final double startime) {
		this(null, vertices, style, startime, -1.0);
	}

	public Polygon(String name, final List<Coordinates> vertices,
			final Style style, final double startime, final double duration) {
		super(name, startime, duration);
		this.vertices = vertices;
		this.style = style;
	}

    public Style getPolyStyle() {
        return style;
    }
    
    public List <Coordinates> getPolyCoordinates() {
        return vertices;
    }
	
	private final List<Coordinates> vertices;
	private final Style style;
}
