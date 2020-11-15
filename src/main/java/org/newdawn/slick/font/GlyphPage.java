
package org.newdawn.slick.font;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.Effect;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;


public class GlyphPage {
	
	private static final SGL GL = Renderer.get();

	
	public static final int MAX_GLYPH_SIZE = 256;

	
    private static ByteBuffer scratchByteBuffer = ByteBuffer.allocateDirect(MAX_GLYPH_SIZE * MAX_GLYPH_SIZE * 4);

    static {
		scratchByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    
    
    private static IntBuffer scratchIntBuffer = scratchByteBuffer.asIntBuffer();
    
    
	
	private static BufferedImage scratchImage = new BufferedImage(MAX_GLYPH_SIZE, MAX_GLYPH_SIZE, BufferedImage.TYPE_INT_ARGB);
	
	private static Graphics2D scratchGraphics = (Graphics2D)scratchImage.getGraphics();
	
	static {
		scratchGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		scratchGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		scratchGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}
	
	
    public static FontRenderContext renderContext = scratchGraphics.getFontRenderContext();
	
	
	public static Graphics2D getScratchGraphics() {
		return scratchGraphics;
	}
	
	
	private final UnicodeFont unicodeFont;
	
	private final int pageWidth;
	
	private final int pageHeight;
	
	private final Image pageImage;
	
	private int pageX;
	
	private int pageY;
	
	private int rowHeight;
	
	private boolean orderAscending;
	
	private final List pageGlyphs = new ArrayList(32);

	
	public GlyphPage(UnicodeFont unicodeFont, int pageWidth, int pageHeight) throws SlickException {
		this.unicodeFont = unicodeFont;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		pageImage = new Image(pageWidth, pageHeight);
	}

	
	public int loadGlyphs (List glyphs, int maxGlyphsToLoad) throws SlickException {
		if (rowHeight != 0 && maxGlyphsToLoad == -1) {
			
			int testX = pageX;
			int testY = pageY;
			int testRowHeight = rowHeight;
			for (Iterator iter = getIterator(glyphs); iter.hasNext();) {
				Glyph glyph = (Glyph)iter.next();
				int width = glyph.getWidth();
				int height = glyph.getHeight();
				if (testX + width >= pageWidth) {
					testX = 0;
					testY += testRowHeight;
					testRowHeight = height;
				} else if (height > testRowHeight) {
					testRowHeight = height;
				}
				if (testY + testRowHeight >= pageWidth) return 0;
				testX += width;
			}
		}

		Color.white.bind();
		pageImage.bind();

		int i = 0;
		for (Iterator iter = getIterator(glyphs); iter.hasNext();) {
			Glyph glyph = (Glyph)iter.next();
			int width = Math.min(MAX_GLYPH_SIZE, glyph.getWidth());
			int height = Math.min(MAX_GLYPH_SIZE, glyph.getHeight());

			if (rowHeight == 0) {
				
				rowHeight = height;
			} else {
				
				if (pageX + width >= pageWidth) {
					if (pageY + rowHeight + height >= pageHeight) break;
					pageX = 0;
					pageY += rowHeight;
					rowHeight = height;
				} else if (height > rowHeight) {
					if (pageY + height >= pageHeight) break;
					rowHeight = height;
				}
			}

			renderGlyph(glyph, width, height);
			pageGlyphs.add(glyph);

			pageX += width;

			iter.remove();
			i++;
			if (i == maxGlyphsToLoad) {
				
				orderAscending = !orderAscending;
				break;
			}
		}

		TextureImpl.bindNone();

		
		orderAscending = !orderAscending;

		return i;
	}

	
	private void renderGlyph(Glyph glyph, int width, int height) throws SlickException {
		
		scratchGraphics.setComposite(AlphaComposite.Clear);
		scratchGraphics.fillRect(0, 0, MAX_GLYPH_SIZE, MAX_GLYPH_SIZE);
		scratchGraphics.setComposite(AlphaComposite.SrcOver);
		scratchGraphics.setColor(java.awt.Color.white);
		for (Iterator iter = unicodeFont.getEffects().iterator(); iter.hasNext();)
			((Effect)iter.next()).draw(scratchImage, scratchGraphics, unicodeFont, glyph);
		glyph.setShape(null); 

		WritableRaster raster = scratchImage.getRaster();
		int[] row = new int[width];
		for (int y = 0; y < height; y++) {
			raster.getDataElements(0, y, width, 1, row);
			scratchIntBuffer.put(row);
		}
		GL.glTexSubImage2D(SGL.GL_TEXTURE_2D, 0, pageX, pageY, width, height, SGL.GL_BGRA, SGL.GL_UNSIGNED_BYTE,
			scratchByteBuffer);
		scratchIntBuffer.clear();

		glyph.setImage(pageImage.getSubImage(pageX, pageY, width, height));
	}

	
	private Iterator getIterator(List glyphs) {
		if (orderAscending) return glyphs.iterator();
		final ListIterator iter = glyphs.listIterator(glyphs.size());
		return new Iterator() {
			public boolean hasNext () {
				return iter.hasPrevious();
			}

			public Object next () {
				return iter.previous();
			}

			public void remove () {
				iter.remove();
			}
		};
	}

	
	public List getGlyphs () {
		return pageGlyphs;
	}

	
	public Image getImage () {
		return pageImage;
	}
}
