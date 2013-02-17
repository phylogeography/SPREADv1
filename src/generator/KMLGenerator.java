package generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import kmlframework.kml.AltitudeModeEnum;
import kmlframework.kml.Document;
import kmlframework.kml.Feature;
import kmlframework.kml.Folder;
import kmlframework.kml.Kml;
import kmlframework.kml.KmlException;
import kmlframework.kml.LineString;
import kmlframework.kml.LineStyle;
import kmlframework.kml.LinearRing;
import kmlframework.kml.Placemark;
import kmlframework.kml.Point;
import kmlframework.kml.PolyStyle;
import kmlframework.kml.StyleSelector;
import kmlframework.kml.TimePrimitive;
import kmlframework.kml.TimeSpan;
import structure.Container;
import structure.Coordinates;
import structure.Item;
import structure.Layer;
import structure.Line;
import structure.Place;
import structure.Polygon;
import structure.Style;
import structure.TimeLine;
import utils.GeoIntermediate;
import utils.Utils;

public class KMLGenerator implements Generator {

	private List<StyleSelector> styles = new ArrayList<StyleSelector>();
	private TimeLine timeLine;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",
			Locale.US);
	private Document document = new Document();
	private GeoIntermediate rhumbIntermediate;

	// 01-01-01 in millis before 1970-01-01
	private static final double YearZeroInMillis = -62135773200000.0;

	public KMLGenerator() {
	}

	public void generate(PrintWriter writer, final TimeLine timeLine,
			final Collection<Layer> layers) throws IOException {

		this.timeLine = timeLine;

		// We create a new KML Document
		Kml kml = new Kml();
		kml.setXmlIndent(true);
		kml.setGenerateObjectIds(false);

		// We add a document to the kml
		kml.setFeature(document);

		for (Layer layer : layers) {
			document.addFeature(generateLayer(layer));
		}

		// We generate the kml file
		try {
			kml.createKml(writer);
		} catch (KmlException e) {
			e.printStackTrace();
		}
	}

	private Feature generateLayer(final Layer layer) {
		Folder folder = new Folder();
		folder.setName(layer.getName());
		folder.setDescription(layer.getDescription());

		generateContent(folder, layer);

		return folder;
	}

	private Feature generateContent(final Folder folder,
			final Container container) {
		for (Item item : container.getItems()) {
			folder.addFeature(generateItem(item));
		}
		return null;
	}

	private Feature generateItem(final Item item) {
		if (item instanceof Line) {
			return generateLine((Line) item);
		} else if (item instanceof Polygon) {
			return generatePolygon((Polygon) item);
		} else if (item instanceof Place) {
			return generatePlacemark((Place) item);
		}
		throw new IllegalArgumentException("unknown item type");
	}

	private Feature generatePolygon(final Polygon polygon) {

		Placemark placemark = new Placemark(polygon.getName());

		double startTime = polygon.getStartTime();
		double duration = polygon.getDuration();

		// Style
		Style style = polygon.getPolyStyle();
		PolyStyle polyStyle = new PolyStyle();
		polyStyle.setOutline(false);
		polyStyle.setColor(Utils.getKMLColor(style.getStrokeColor()));
		style.setPolyStyle(polyStyle);
		styles.add(style);
		placemark.setStyleUrl(style.getId());
		document.setStyleSelectors(styles);

		// Time
		// Parse minus if date is BC
		placemark.setTimePrimitive(new TimeSpan(
				startTime < YearZeroInMillis ? "-"
						+ formatter.format(startTime) : formatter
						.format(startTime), duration > 0.0 ? (startTime
						+ duration < YearZeroInMillis ? "-"
						+ formatter.format(startTime + duration) : formatter
						.format(startTime + duration)) : ""));

		// Polygon OuterBoundaryIs LinearRing
		LinearRing linearRing = new LinearRing();

		// linearRing.setCoordinates();
		linearRing.setCoordinates(Utils.convertToPoint(polygon
				.getPolyCoordinates()));

		kmlframework.kml.Polygon localPolygon = new kmlframework.kml.Polygon();
		localPolygon.setTessellate(true);
		localPolygon.setOuterBoundary(linearRing);
		placemark.setGeometry(localPolygon);

		return placemark;

	}

	private Feature generateLine(final Line line) {
		Feature feature;

		if (line.getEndStyle() == null
				|| line.getStartStyle().equals(line.getEndStyle())) {

			double startTime = line.getStartTime();
			double endTime = line.getEndTime();
			double duration = line.getDuration();
			double maxAltitude = line.getMaxAltitude();
			double timeRange = endTime - startTime;

			if (timeLine.isInstantaneous() || timeRange == 0.0) {

				feature = generateLineSegment(line.getStartLocation(), line
						.getEndLocation(), startTime, duration, line
						.getStartStyle());

			} else {

				Folder folder = new Folder();

				// Parse start and end point coordinates
				double startLon = line.getStartLocation().getLongitude();
				double startLat = line.getStartLocation().getLatitude();
				double endLon = line.getEndLocation().getLongitude();
				double endLat = line.getEndLocation().getLatitude();

				int sliceCount = timeLine.getSliceCount();

				rhumbIntermediate = new GeoIntermediate(startLon, startLat,
						endLon, endLat, sliceCount);
				double coords[][] = rhumbIntermediate.getCoords();

//				Utils.print2DArray(coords);
				
				double a = -2 * maxAltitude
						/ (Math.pow(sliceCount, 2) - sliceCount);
				double b = 2 * maxAltitude / (sliceCount - 1);

				for (int i = 0; i < sliceCount; i++) {

					double startAltitude = a * Math.pow((double) i, 2) + b
							* (double) i;
					double endAltitude = a * Math.pow((double) (i + 1), 2) + b
							* (double) (i + 1);

					double segmentStartTime = endTime - (i + 1)
							* ((endTime - startTime) / sliceCount);

					//TODO
//					System.out.println(startTime < YearZeroInMillis ? "-"
//							+ formatter.format(startTime) : formatter
//							.format(startTime)); 
							
					Placemark lineSegment = generateLineSegment(
							new Coordinates(coords[i][0], coords[i][1],
									startAltitude),// startCoordinates
							new Coordinates(coords[i + 1][0], coords[i + 1][1],
									endAltitude),// endCoordinates
							segmentStartTime,// startTime
							duration,// duration
							line.getStartStyle()// Style
					);

					folder.addFeature(lineSegment);

				}

				feature = folder;
			}

		} else {
			Folder folder = new Folder();
			// for (int i = 0; i < divisionCount; i++) {
			// Placemark lineSegment = generateLineSegment(loc1, loc2, time,
			// duration, style);
			// folder.addFeature(lineSegment);
			// }
			feature = folder;
		}

		feature.setName(line.getName());
		return feature;
	}

	private Placemark generateLineSegment(Coordinates startCoordinates,
			Coordinates endCoordinates, double startTime, double duration,
			Style style) {

		LineStyle lineStyle = new LineStyle();
		lineStyle.setColor(Utils.getKMLColor(style.getStrokeColor()));
		lineStyle.setWidth(style.getStrokeWidth());

		style.setLineStyle(lineStyle);

		styles.add(style);
		document.setStyleSelectors(styles);

		LineString lineString = new LineString();
		lineString.setTessellate(true);
		lineString.setAltitudeMode(AltitudeModeEnum.relativeToGround);
		List<Point> points = new ArrayList<Point>();
		points.add(generatePoint(startCoordinates));
		points.add(generatePoint(endCoordinates));

		lineString.setCoordinates(points);
		Placemark placemark = new Placemark();

		if (!Double.isNaN(duration)) {

			// Parse minus if date is BC
			TimePrimitive timePrimitive = new TimeSpan(
					startTime < YearZeroInMillis ? "-"
							+ formatter.format(startTime) : formatter
							.format(startTime), duration > 0.0 ? (startTime
							+ duration < YearZeroInMillis ? "-"
							+ formatter.format(startTime + duration)
							: formatter.format(startTime + duration)) : "");

			// System.out.println(formatter.format(startTime + duration));

			placemark.setTimePrimitive(timePrimitive);
		}

		placemark.setStyleUrl(style.getId());
		placemark.setGeometry(lineString);
		return placemark;

	}

	private Placemark generatePlacemark(final Place place) {
		Placemark placemark = new Placemark();
		placemark.setGeometry(generatePoint(place.getCoordinates()));
		placemark.setName(place.getName());
		return placemark;
	}

	private Point generatePoint(final Coordinates coordinates) {
		Point point = new Point();
		point.setAltitudeMode(AltitudeModeEnum.relativeToGround);
		point.setAltitude(coordinates.getAltitude());
		point.setLongitude(coordinates.getLongitude());
		point.setLatitude(coordinates.getLatitude());
		return point;
	}

	@Override
	public String toString() {
		return "KML";
	}

}
