package org.newdawn.slick.util;

import java.awt.Graphics2D;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;


public class FontUtils {
	
	public class Alignment {
		
		public static final int LEFT = 1;
		
		public static final int CENTER = 2;
		
		public static final int RIGHT = 3;
		
		public static final int JUSTIFY = 4;
	}

	
	public static void drawLeft(Font font, String s, int x, int y) {
		drawString(font, s, Alignment.LEFT, x, y, 0, Color.white);
	}

	
	public static void drawCenter(Font font, String s, int x, int y, int width) {
		drawString(font, s, Alignment.CENTER, x, y, width, Color.white);
	}

	
	public static void drawCenter(Font font, String s, int x, int y, int width,
			Color color) {
		drawString(font, s, Alignment.CENTER, x, y, width, color);
	}

	
	public static void drawRight(Font font, String s, int x, int y, int width) {
		drawString(font, s, Alignment.RIGHT, x, y, width, Color.white);
	}

	
	public static void drawRight(Font font, String s, int x, int y, int width,
			Color color) {
		drawString(font, s, Alignment.RIGHT, x, y, width, color);
	}

	
	public static final int drawString(Font font, final String s,
			final int alignment, final int x, final int y, final int width,
			Color color) {
		int resultingXCoordinate = 0;
		if (alignment == Alignment.LEFT) {
			font.drawString(x, y, s, color);
		} else if (alignment == Alignment.CENTER) {
			font.drawString(x + (width / 2) - (font.getWidth(s) / 2), y, s,
					color);
		} else if (alignment == Alignment.RIGHT) {
			font.drawString(x + width - font.getWidth(s), y, s, color);
		} else if (alignment == Alignment.JUSTIFY) {
			
			int leftWidth = width - font.getWidth(s);
			if (leftWidth <= 0) {
				
				font.drawString(x, y, s, color);
			}

			return FontUtils.drawJustifiedSpaceSeparatedSubstrings(font, s, x,
					y, FontUtils.calculateWidthOfJustifiedSpaceInPixels(font,
							s, leftWidth));
		}

		return resultingXCoordinate;
	}

	
	private static int calculateWidthOfJustifiedSpaceInPixels(final Font font,
			final String s, final int leftWidth) {
		int space = 0; 
		int curpos = 0; 

		
		while (curpos < s.length()) {
			if (s.charAt(curpos++) == ' ') {
				space++;
			}
		}

		if (space > 0) {
			
			
			space = (leftWidth + (font.getWidth(" ") * space)) / space;
		}
		return space;
	}

	
	private static int drawJustifiedSpaceSeparatedSubstrings(Font font,
			final String s, final int x, final int y,
			final int justifiedSpaceWidth) {
		int curpos = 0;
		int endpos = 0;
		int resultingXCoordinate = x;
		while (curpos < s.length()) {
			endpos = s.indexOf(' ', curpos); 
			if (endpos == -1) {
				endpos = s.length(); 
			}
			String substring = s.substring(curpos, endpos);

			font.drawString(resultingXCoordinate, y, substring);

			resultingXCoordinate += font.getWidth(substring)
					+ justifiedSpaceWidth; 
			
			curpos = endpos + 1;
		}

		return resultingXCoordinate;
	}
}