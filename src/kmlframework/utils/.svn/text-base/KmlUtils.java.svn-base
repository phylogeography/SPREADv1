/*
 * KmlUtils.java Created Oct 14, 2010 by Andrew Butler, PSL
 */
package org.boehn.kmlframework.utils;

/** Contains utility methods useful for creating KML documents */
public class KmlUtils
{
	private static final java.text.SimpleDateFormat GOOGLE_TIME_FORMAT;

	static
	{
		GOOGLE_TIME_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		GOOGLE_TIME_FORMAT.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Writes a time to correct KML format
	 * 
	 * @param time The time (in milliseconds from the epoch) to convert
	 * @return The KML-formatted time
	 */
	public static String formatTime(long time)
	{
		return GOOGLE_TIME_FORMAT.format(new java.util.Date(time));
	}

	private static final String HEX = "0123456789abcdef";

	/**
	 * Writes a color to correct KML format
	 * 
	 * @param color The color to convert
	 * @return The KML-formatted color
	 */
	public static String formatColor(java.awt.Color color)
	{
		StringBuilder ret = new StringBuilder();
		int a = color.getAlpha();
		ret.append(HEX.charAt(a / 16));
		ret.append(HEX.charAt(a % 16));
		int b = color.getBlue();
		ret.append(HEX.charAt(b / 16));
		ret.append(HEX.charAt(b % 16));
		int g = color.getGreen();
		ret.append(HEX.charAt(g / 16));
		ret.append(HEX.charAt(g % 16));
		int r = color.getRed();
		ret.append(HEX.charAt(r / 16));
		ret.append(HEX.charAt(r % 16));
		return ret.toString();
	}
}
