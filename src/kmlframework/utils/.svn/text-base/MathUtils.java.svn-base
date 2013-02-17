package org.boehn.kmlframework.utils;

public class MathUtils {

	public static double degreesToDecimal(String input) {
		char direction = input.charAt(0);
		int degrees;
		int minutes;
		int seconds;
		if (direction == 'E' || direction == 'W') {
			degrees = Integer.parseInt(input.substring(1,4));
			minutes = Integer.parseInt(input.substring(4,6));
			seconds = Integer.parseInt(input.substring(6));
		} else {
			degrees = Integer.parseInt(input.substring(1,3));
			minutes = Integer.parseInt(input.substring(3,5));
			seconds = Integer.parseInt(input.substring(5));
		}
		
		double decimal = degrees + (float) minutes / 60 + (float) seconds / 3600;
		
		if (direction == 'W' || direction == 'S') {
			decimal *= -1;
		}
		return decimal;
	}
}
