package kmlframework.kml;

import java.util.List;

import kmlframework.atom.AtomAuthor;
import kmlframework.atom.AtomLink;


public class PhotoOverlay extends Overlay
{

	private Double rotation;

	private Double leftFov;

	private Double rightFov;

	private Double bottomFov;

	private Double topFov;

	private Double near;

	private Integer tileSize;

	private Integer maxWidth;

	private Integer maxHeight;

	private GridOriginEnum gridOrigin;

	private Point point;

	private ShapeEnum shape;

	public PhotoOverlay()
	{
	}

	public PhotoOverlay(String name, Boolean visibility, Boolean open, AtomAuthor atomAuthor,
		AtomLink atomLink, String address, String xalAddressDetails, String phoneNumber,
		String snippet, Integer snippetMaxLines, String description, AbstractView abstractView,
		TimePrimitive timePrimitive, String styleUrl, List<StyleSelector> styleSelectors,
		Region region, ExtendedData extendedData, String color, Integer drawOrder, Icon icon,
		Double rotation, Double leftFov, Double rightFov, Double bottomFov, Double topFov,
		Double near, Integer tileSize, Integer maxWidth, Integer maxHeight,
		GridOriginEnum gridOrigin, Point point, ShapeEnum shape)
	{
		super(name, visibility, open, atomAuthor, atomLink, address, xalAddressDetails,
			phoneNumber, snippet, snippetMaxLines, description, abstractView, timePrimitive,
			styleUrl, styleSelectors, region, extendedData, color, drawOrder, icon);
		this.rotation = rotation;
		this.leftFov = leftFov;
		this.rightFov = rightFov;
		this.bottomFov = bottomFov;
		this.topFov = topFov;
		this.near = near;
		this.tileSize = tileSize;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.gridOrigin = gridOrigin;
		this.point = point;
		this.shape = shape;
	}

	public Double getRotation()
	{
		return rotation;
	}

	public void setRotation(Double rotation)
	{
		this.rotation = rotation;
	}

	public Double getLeftFov()
	{
		return leftFov;
	}

	public void setLeftFov(Double leftFov)
	{
		this.leftFov = leftFov;
	}

	public Double getRightFov()
	{
		return rightFov;
	}

	public void setRightFov(Double rightFov)
	{
		this.rightFov = rightFov;
	}

	public Double getBottomFov()
	{
		return bottomFov;
	}

	public void setBottomFov(Double bottomFov)
	{
		this.bottomFov = bottomFov;
	}

	public Double getTopFov()
	{
		return topFov;
	}

	public void setTopFov(Double topFov)
	{
		this.topFov = topFov;
	}

	public Double getNear()
	{
		return near;
	}

	public void setNear(Double near)
	{
		this.near = near;
	}

	public Integer getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(Integer tileSize)
	{
		this.tileSize = tileSize;
	}

	public Integer getMaxWidth()
	{
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth)
	{
		this.maxWidth = maxWidth;
	}

	public Integer getMaxHeight()
	{
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight)
	{
		this.maxHeight = maxHeight;
	}

	public GridOriginEnum getGridOrigin()
	{
		return gridOrigin;
	}

	public void setGridOrigin(GridOriginEnum gridOrigin)
	{
		this.gridOrigin = gridOrigin;
	}

	public Point getPoint()
	{
		return point;
	}

	public void setPoint(Point point)
	{
		this.point = point;
	}

	public ShapeEnum getShape()
	{
		return shape;
	}

	public void setShape(ShapeEnum shape)
	{
		this.shape = shape;
	}

	public void write(Kml kml) throws KmlException
	{
		kml.println("<PhotoOverlay" + getIdAndTargetIdFormatted(kml) + ">", 1);
		super.writeInner(kml);
		if(rotation != null)
		{
			kml.println("<rotation>" + rotation + "</rotation>");
		}
		if(leftFov != null || rightFov != null || bottomFov != null || topFov != null
			|| near != null)
		{
			kml.println("<ViewVolume>", 1);
			if(leftFov != null)
			{
				kml.println("<leftFov>" + leftFov + "</leftFov>");
			}
			if(rightFov != null)
			{
				kml.println("<rightFov>" + rightFov + "</rightFov>");
			}
			if(bottomFov != null)
			{
				kml.println("<bottomFov>" + bottomFov + "</bottomFov>");
			}
			if(topFov != null)
			{
				kml.println("<topFov>" + topFov + "</topFov>");
			}
			if(near != null)
			{
				kml.println("<near>" + near + "</near>");
			}
			kml.println(-1, "</ViewVolume>");
		}
		if(tileSize != null || maxWidth != null || maxHeight != null || gridOrigin != null)
		{
			kml.println("<ImagePyramid>", 1);
			if(tileSize != null)
			{
				kml.println("<tileSize>" + tileSize + "</tileSize>");
			}
			if(maxWidth != null)
			{
				kml.println("<maxWidth>" + maxWidth + "</maxWidth>");
			}
			if(maxHeight != null)
			{
				kml.println("<maxHeight>" + maxHeight + "</maxHeight>");
			}
			if(gridOrigin != null)
			{
				kml.println("<gridOrigin>" + gridOrigin + "</gridOrigin>");
			}
			kml.println(-1, "</ImagePyramid>");
			if(point != null)
			{
				point.write(kml);
			}
			if(shape != null)
			{
				kml.println("<shape>" + shape + "</shape>");
			}
		}
		kml.println(-1, "</PhotoOverlay>");
	}
}
