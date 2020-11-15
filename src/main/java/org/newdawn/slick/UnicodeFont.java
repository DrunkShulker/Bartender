
package org.newdawn.slick;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.newdawn.slick.font.Glyph;
import org.newdawn.slick.font.GlyphPage;
import org.newdawn.slick.font.HieroSettings;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.ResourceLoader;


public class UnicodeFont implements org.newdawn.slick.Font {
	
	private static final int DISPLAY_LIST_CACHE_SIZE = 200;
	
	static private final int MAX_GLYPH_CODE = 0x10FFFF;
	
	private static final int PAGE_SIZE = 512;
	
	private static final int PAGES = MAX_GLYPH_CODE / PAGE_SIZE;
	
	private static final SGL GL = Renderer.get();
	
	private static final DisplayList EMPTY_DISPLAY_LIST = new DisplayList();

	
	private static Font createFont (String ttfFileRef) throws SlickException {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(ttfFileRef));
		} catch (FontFormatException ex) {
			throw new SlickException("Invalid font: " + ttfFileRef, ex);
		} catch (IOException ex) {
			throw new SlickException("Error reading font: " + ttfFileRef, ex);
		}
	}

	
	private static final Comparator heightComparator = new Comparator() {
		public int compare (Object o1, Object o2) {
			return ((Glyph)o1).getHeight() - ((Glyph)o2).getHeight();
		}
	};
	
	
	private Font font;
	
	private String ttfFileRef;
	
	private int ascent;
	
	private int descent;
	
	private int leading;
	
	private int spaceWidth;
	
	private final Glyph[][] glyphs = new Glyph[PAGES][];
	
	private final List glyphPages = new ArrayList();
	
	private final List queuedGlyphs = new ArrayList(256);
	
	private final List effects = new ArrayList();
	
	
	private int paddingTop;
	
	private int paddingLeft;
	
	private int paddingBottom;
	
	private int paddingRight;
	
	private int paddingAdvanceX;
	
	private int paddingAdvanceY;
	
	private Glyph missingGlyph;

	
	private int glyphPageWidth = 512;
	
	private int glyphPageHeight = 512;
	
	
	private boolean displayListCaching = true;
	
	private int baseDisplayListID = -1;
	
	private int eldestDisplayListID;
	
	private DisplayList eldestDisplayList;
	
	
	private final LinkedHashMap displayLists = new LinkedHashMap(DISPLAY_LIST_CACHE_SIZE, 1, true) {
		protected boolean removeEldestEntry (Entry eldest) {
			DisplayList displayList = (DisplayList)eldest.getValue();
			if (displayList != null) eldestDisplayListID = displayList.id;
			return size() > DISPLAY_LIST_CACHE_SIZE;
		}
	};

	
	public UnicodeFont (String ttfFileRef, String hieroFileRef) throws SlickException {
		this(ttfFileRef, new HieroSettings(hieroFileRef));
	}

	
	public UnicodeFont (String ttfFileRef, HieroSettings settings) throws SlickException {
		this.ttfFileRef = ttfFileRef;
		Font font = createFont(ttfFileRef);
		initializeFont(font, settings.getFontSize(), settings.isBold(), settings.isItalic());
		loadSettings(settings);
	}

	
	public UnicodeFont (String ttfFileRef, int size, boolean bold, boolean italic) throws SlickException {
		this.ttfFileRef = ttfFileRef;
		initializeFont(createFont(ttfFileRef), size, bold, italic);
	}

	
	public UnicodeFont (Font font, String hieroFileRef) throws SlickException {
		this(font, new HieroSettings(hieroFileRef));
	}

	
	public UnicodeFont (Font font, HieroSettings settings) {
		initializeFont(font, settings.getFontSize(), settings.isBold(), settings.isItalic());
		loadSettings(settings);
	}

	
	public UnicodeFont (Font font) {
		initializeFont(font, font.getSize(), font.isBold(), font.isItalic());
	}

	
	public UnicodeFont (Font font, int size, boolean bold, boolean italic) {
		initializeFont(font, size, bold, italic);
	}

	
	private void initializeFont(Font baseFont, int size, boolean bold, boolean italic) {
		Map attributes = baseFont.getAttributes();
		attributes.put(TextAttribute.SIZE, new Float(size));
		attributes.put(TextAttribute.WEIGHT, bold ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
		attributes.put(TextAttribute.POSTURE, italic ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
		try {
			attributes.put(TextAttribute.class.getDeclaredField("KERNING").get(null), TextAttribute.class.getDeclaredField(
				"KERNING_ON").get(null));
		} catch (Exception ignored) {
		}
		font = baseFont.deriveFont(attributes);

		FontMetrics metrics = GlyphPage.getScratchGraphics().getFontMetrics(font);
		ascent = metrics.getAscent();
		descent = metrics.getDescent();
		leading = metrics.getLeading();
		
		
		char[] chars = " ".toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
		spaceWidth = vector.getGlyphLogicalBounds(0).getBounds().width;
	}

	
	private void loadSettings(HieroSettings settings) {
		paddingTop = settings.getPaddingTop();
		paddingLeft = settings.getPaddingLeft();
		paddingBottom = settings.getPaddingBottom();
		paddingRight = settings.getPaddingRight();
		paddingAdvanceX = settings.getPaddingAdvanceX();
		paddingAdvanceY = settings.getPaddingAdvanceY();
		glyphPageWidth = settings.getGlyphPageWidth();
		glyphPageHeight = settings.getGlyphPageHeight();
		effects.addAll(settings.getEffects());
	}

	
	public void addGlyphs(int startCodePoint, int endCodePoint) {
		for (int codePoint = startCodePoint; codePoint <= endCodePoint; codePoint++)
			addGlyphs(new String(Character.toChars(codePoint)));
	}

	
	public void addGlyphs(String text) {
		if (text == null) throw new IllegalArgumentException("text cannot be null.");

		char[] chars = text.toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
		for (int i = 0, n = vector.getNumGlyphs(); i < n; i++) {
			int codePoint = text.codePointAt(vector.getGlyphCharIndex(i));
			Rectangle bounds = getGlyphBounds(vector, i, codePoint);
			getGlyph(vector.getGlyphCode(i), codePoint, bounds, vector, i);
		}
	}

	
	public void addAsciiGlyphs () {
		addGlyphs(32, 255);
	}

	
	public void addNeheGlyphs () {
		addGlyphs(32, 32 + 96);
	}

	
	public boolean loadGlyphs () throws SlickException {
		return loadGlyphs(-1);
	}

	
	public boolean loadGlyphs (int maxGlyphsToLoad) throws SlickException {
		if (queuedGlyphs.isEmpty()) return false;

		if (effects.isEmpty())
			throw new IllegalStateException("The UnicodeFont must have at least one effect before any glyphs can be loaded.");

		for (Iterator iter = queuedGlyphs.iterator(); iter.hasNext();) {
			Glyph glyph = (Glyph)iter.next();
			int codePoint = glyph.getCodePoint();

			
			if (glyph.getWidth() == 0 || codePoint == ' ') {
				iter.remove();
				continue;
			}

			
			if (glyph.isMissing()) {
				if (missingGlyph != null) {
					if (glyph != missingGlyph) iter.remove();
					continue;
				}
				missingGlyph = glyph;
			}
		}

		Collections.sort(queuedGlyphs, heightComparator);

		
		for (Iterator iter = glyphPages.iterator(); iter.hasNext();) {
			GlyphPage glyphPage = (GlyphPage)iter.next();
			maxGlyphsToLoad -= glyphPage.loadGlyphs(queuedGlyphs, maxGlyphsToLoad);
			if (maxGlyphsToLoad == 0 || queuedGlyphs.isEmpty())
				return true;
		}

		
		while (!queuedGlyphs.isEmpty()) {
			GlyphPage glyphPage = new GlyphPage(this, glyphPageWidth, glyphPageHeight);
			glyphPages.add(glyphPage);
			maxGlyphsToLoad -= glyphPage.loadGlyphs(queuedGlyphs, maxGlyphsToLoad);
			if (maxGlyphsToLoad == 0) return true;
		}

		return true;
	}

	
	public void clearGlyphs () {
		for (int i = 0; i < PAGES; i++)
			glyphs[i] = null;

		for (Iterator iter = glyphPages.iterator(); iter.hasNext();) {
			GlyphPage page = (GlyphPage)iter.next();
			try {
				page.getImage().destroy();
			} catch (SlickException ignored) {
			}
		}
		glyphPages.clear();

		if (baseDisplayListID != -1) {
			GL.glDeleteLists(baseDisplayListID, displayLists.size());
			baseDisplayListID = -1;
		}

		queuedGlyphs.clear();
		missingGlyph = null;
	}

	
	public void destroy () {
		
		clearGlyphs();
	}

	
	public DisplayList drawDisplayList (float x, float y, String text, Color color, int startIndex, int endIndex) {
		if (text == null) throw new IllegalArgumentException("text cannot be null.");
		if (text.length() == 0) return EMPTY_DISPLAY_LIST;
		if (color == null) throw new IllegalArgumentException("color cannot be null.");

		x -= paddingLeft;
		y -= paddingTop;

		String displayListKey = text.substring(startIndex, endIndex);

		color.bind();
		TextureImpl.bindNone();

		DisplayList displayList = null;
		if (displayListCaching && queuedGlyphs.isEmpty()) {
			if (baseDisplayListID == -1) {
				baseDisplayListID = GL.glGenLists(DISPLAY_LIST_CACHE_SIZE);
				if (baseDisplayListID == 0) {
					baseDisplayListID = -1;
					displayListCaching = false;
					return new DisplayList();
				}
			}
			
			displayList = (DisplayList)displayLists.get(displayListKey);
			if (displayList != null) {
				if (displayList.invalid)
					displayList.invalid = false;
				else {
					GL.glTranslatef(x, y, 0);
					GL.glCallList(displayList.id);
					GL.glTranslatef(-x, -y, 0);
					return displayList;
				}
			} else if (displayList == null) {
				
				displayList = new DisplayList();
				int displayListCount = displayLists.size();
				displayLists.put(displayListKey, displayList);
				if (displayListCount < DISPLAY_LIST_CACHE_SIZE)
					displayList.id = baseDisplayListID + displayListCount;
				else
					displayList.id = eldestDisplayListID;
			}
			displayLists.put(displayListKey, displayList);
		}

		GL.glTranslatef(x, y, 0);

		if (displayList != null) GL.glNewList(displayList.id, SGL.GL_COMPILE_AND_EXECUTE);

		char[] chars = text.substring(0, endIndex).toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);

		int maxWidth = 0, totalHeight = 0, lines = 0;
		int extraX = 0, extraY = ascent;
		boolean startNewLine = false;
		Texture lastBind = null;
		for (int glyphIndex = 0, n = vector.getNumGlyphs(); glyphIndex < n; glyphIndex++) {
			int charIndex = vector.getGlyphCharIndex(glyphIndex);
			if (charIndex < startIndex) continue;
			if (charIndex > endIndex) break;

			int codePoint = text.codePointAt(charIndex);

			Rectangle bounds = getGlyphBounds(vector, glyphIndex, codePoint);
			Glyph glyph = getGlyph(vector.getGlyphCode(glyphIndex), codePoint, bounds, vector, glyphIndex);

			if (startNewLine && codePoint != '\n') {
				extraX = -bounds.x;
				startNewLine = false;
			}

			Image image = glyph.getImage();
			if (image == null && missingGlyph != null && glyph.isMissing()) image = missingGlyph.getImage();
			if (image != null) {
				
				Texture texture = image.getTexture();
				if (lastBind != null && lastBind != texture) {
					GL.glEnd();
					lastBind = null;
				}
				if (lastBind == null) {
					texture.bind();
					GL.glBegin(SGL.GL_QUADS);
					lastBind = texture;
				}
				image.drawEmbedded(bounds.x + extraX, bounds.y + extraY, image.getWidth(), image.getHeight());
			}

			if (glyphIndex >= 0) extraX += paddingRight + paddingLeft + paddingAdvanceX;
			maxWidth = Math.max(maxWidth, bounds.x + extraX + bounds.width);
			totalHeight = Math.max(totalHeight, ascent + bounds.y + bounds.height);

			if (codePoint == '\n') {
				startNewLine = true; 
				extraY += getLineHeight();
				lines++;
				totalHeight = 0;
			}
		}
		if (lastBind != null) GL.glEnd();

		if (displayList != null) {
			GL.glEndList();
			
			if (!queuedGlyphs.isEmpty()) displayList.invalid = true;
		}

		GL.glTranslatef(-x, -y, 0);

		if (displayList == null) displayList = new DisplayList();
		displayList.width = (short)maxWidth;
		displayList.height = (short)(lines * getLineHeight() + totalHeight);
		return displayList;
	}

	public void drawString (float x, float y, String text, Color color, int startIndex, int endIndex) {
		drawDisplayList(x, y, text, color, startIndex, endIndex);
	}

	public void drawString (float x, float y, String text) {
		drawString(x, y, text, Color.white);
	}

	public void drawString (float x, float y, String text, Color col) {
		drawString(x, y, text, col, 0, text.length());
	}

	
	private Glyph getGlyph (int glyphCode, int codePoint, Rectangle bounds, GlyphVector vector, int index) {
		if (glyphCode < 0 || glyphCode >= MAX_GLYPH_CODE) {
			
			return new Glyph(codePoint, bounds, vector, index, this) {
				public boolean isMissing () {
					return true;
				}
			};
		}
		int pageIndex = glyphCode / PAGE_SIZE;
		int glyphIndex = glyphCode & (PAGE_SIZE - 1);
		Glyph glyph = null;
		Glyph[] page = glyphs[pageIndex];
		if (page != null) {
			glyph = page[glyphIndex];
			if (glyph != null) return glyph;
		} else
			page = glyphs[pageIndex] = new Glyph[PAGE_SIZE];
		
		glyph = page[glyphIndex] = new Glyph(codePoint, bounds, vector, index, this);
		queuedGlyphs.add(glyph);
		return glyph;
	}

	
	private Rectangle getGlyphBounds (GlyphVector vector, int index, int codePoint) {
		Rectangle bounds = vector.getGlyphPixelBounds(index, GlyphPage.renderContext, 0, 0);
		if (codePoint == ' ') bounds.width = spaceWidth;
		return bounds;
	}

	
	public int getSpaceWidth () {
		return spaceWidth;
	}

	
	public int getWidth (String text) {
		if (text == null) return 0;
		if (text.length() == 0) return 0;

		if (displayListCaching) {
			DisplayList displayList = (DisplayList)displayLists.get(text);
			if (displayList != null) return displayList.width;
		}

		char[] chars = text.toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);

		int width = 0;
		int extraX = 0;
		boolean startNewLine = false;
		for (int glyphIndex = 0, n = vector.getNumGlyphs(); glyphIndex < n; glyphIndex++) {
			int charIndex = vector.getGlyphCharIndex(glyphIndex);
			int codePoint = text.codePointAt(charIndex);
			Rectangle bounds = getGlyphBounds(vector, glyphIndex, codePoint);

			if (startNewLine && codePoint != '\n') extraX = -bounds.x;

			if (glyphIndex > 0) extraX += paddingLeft + paddingRight + paddingAdvanceX;
			width = Math.max(width, bounds.x + extraX + bounds.width);

			if (codePoint == '\n') startNewLine = true;
		}

		return width;
	}

	
	public int getHeight (String text) {
		if (text == null) throw new IllegalArgumentException("text cannot be null.");
		if (text.length() == 0) return 0;

		if (displayListCaching) {
			DisplayList displayList = (DisplayList)displayLists.get(text);
			if (displayList != null) return displayList.height;
		}

		char[] chars = text.toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);

		int lines = 0, height = 0;
		for (int i = 0, n = vector.getNumGlyphs(); i < n; i++) {
			int charIndex = vector.getGlyphCharIndex(i);
			int codePoint = text.codePointAt(charIndex);
			if (codePoint == ' ') continue;
			Rectangle bounds = getGlyphBounds(vector, i, codePoint);

			height = Math.max(height, ascent + bounds.y + bounds.height);

			if (codePoint == '\n') {
				lines++;
				height = 0;
			}
		}
		return lines * getLineHeight() + height;
	}

	
	public int getYOffset (String text) {
		if (text == null) throw new IllegalArgumentException("text cannot be null.");

		DisplayList displayList = null;
		if (displayListCaching) {
			displayList = (DisplayList)displayLists.get(text);
			if (displayList != null && displayList.yOffset != null) return displayList.yOffset.intValue();
		}

		int index = text.indexOf('\n');
		if (index != -1) text = text.substring(0, index);
		char[] chars = text.toCharArray();
		GlyphVector vector = font.layoutGlyphVector(GlyphPage.renderContext, chars, 0, chars.length, Font.LAYOUT_LEFT_TO_RIGHT);
		int yOffset = ascent + vector.getPixelBounds(null, 0, 0).y;

		if (displayList != null) displayList.yOffset = new Short((short)yOffset);

		return yOffset;
	}

	
	public Font getFont() {
		return font;
	}

	
	public int getPaddingTop() {
		return paddingTop;
	}

	
	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	
	public int getPaddingLeft() {
		return paddingLeft;
	}

	
	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	
	public int getPaddingBottom() {
		return paddingBottom;
	}

	
	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	
	public int getPaddingRight () {
		return paddingRight;
	}

	
	public void setPaddingRight (int paddingRight) {
		this.paddingRight = paddingRight;
	}

	
	public int getPaddingAdvanceX() {
		return paddingAdvanceX;
	}

	
	public void setPaddingAdvanceX (int paddingAdvanceX) {
		this.paddingAdvanceX = paddingAdvanceX;
	}

	
	public int getPaddingAdvanceY () {
		return paddingAdvanceY;
	}

	
	public void setPaddingAdvanceY (int paddingAdvanceY) {
		this.paddingAdvanceY = paddingAdvanceY;
	}

	
	public int getLineHeight() {
		return descent + ascent + leading + paddingTop + paddingBottom + paddingAdvanceY;
	}

	
	public int getAscent() {
		return ascent;
	}

	
	public int getDescent () {
		return descent;
	}

	
	public int getLeading () {
		return leading;
	}

	
	public int getGlyphPageWidth () {
		return glyphPageWidth;
	}

	
	public void setGlyphPageWidth(int glyphPageWidth) {
		this.glyphPageWidth = glyphPageWidth;
	}

	
	public int getGlyphPageHeight() {
		return glyphPageHeight;
	}

	
	public void setGlyphPageHeight(int glyphPageHeight) {
		this.glyphPageHeight = glyphPageHeight;
	}

	
	public List getGlyphPages () {
		return glyphPages;
	}

	
	public List getEffects () {
		return effects;
	}

	
	public boolean isCaching () {
		return displayListCaching;
	}

	
	public void setDisplayListCaching (boolean displayListCaching) {
		this.displayListCaching = displayListCaching;
	}

	
	public String getFontFile () {
		if (ttfFileRef == null) {
			
			try {
				Object font2D = Class.forName("sun.font.FontManager").getDeclaredMethod("getFont2D", new Class[] {Font.class})
					.invoke(null, new Object[] {font});
				Field platNameField = Class.forName("sun.font.PhysicalFont").getDeclaredField("platName");
				platNameField.setAccessible(true);
				ttfFileRef = (String)platNameField.get(font2D);
			} catch (Throwable ignored) {
			}
			if (ttfFileRef == null) ttfFileRef = "";
		}
		if (ttfFileRef.length() == 0) return null;
		return ttfFileRef;
	}

	
	public static class DisplayList {
		
		boolean invalid;
		
		int id;
		
		Short yOffset;

		
		public short width;
		
		public short height;
		
		public Object userData;

		DisplayList () {
		}
	}
}
