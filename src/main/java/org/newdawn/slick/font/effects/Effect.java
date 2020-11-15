
package org.newdawn.slick.font.effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.Glyph;


public interface Effect {
	
	public void draw (BufferedImage image, Graphics2D g, UnicodeFont unicodeFont, Glyph glyph);
}
