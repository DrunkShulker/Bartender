package org.newdawn.slick.util;


public class FastTrig {
   
	private static double reduceSinAngle(double radians) {
		double orig = radians;
		radians %= Math.PI * 2.0; 
		if (Math.abs(radians) > Math.PI) { 
			radians = radians - (Math.PI * 2.0);
		}
		if (Math.abs(radians) > Math.PI / 2) {
			radians = Math.PI - radians;
		}

		return radians;
	}

	
	public static double sin(double radians) {
		radians = reduceSinAngle(radians); 
		if (Math.abs(radians) <= Math.PI / 4) {
			return Math.sin(radians);
		} else {
			return Math.cos(Math.PI / 2 - radians);
		}
	}

	
	public static double cos(double radians) {
		return sin(radians + Math.PI / 2);
	}

}
