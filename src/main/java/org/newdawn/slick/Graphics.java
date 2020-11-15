package org.newdawn.slick;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.LineStripRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;


public class Graphics {
	
	protected static SGL GL = Renderer.get();
	
	private static LineStripRenderer LSR = Renderer.getLineStripRenderer();

	
	public static int MODE_NORMAL = 1;

	
	public static int MODE_ALPHA_MAP = 2;

	
	public static int MODE_ALPHA_BLEND = 3;

	
	public static int MODE_COLOR_MULTIPLY = 4;
	
	
	public static int MODE_ADD = 5;
	
	
	public static int MODE_SCREEN = 6;
	
	
	private static final int DEFAULT_SEGMENTS = 50;

	
	protected static Graphics currentGraphics = null;
	
	
	protected static Font DEFAULT_FONT;

	
	private float sx = 1;
	
	private float sy = 1;
	
	
	public static void setCurrent(Graphics current) {
		if (currentGraphics != current) {
			if (currentGraphics != null) {
				currentGraphics.disable();
			}
			currentGraphics = current;
			currentGraphics.enable();
		}
	}
	
	
	private Font font;

	
	private Color currentColor = Color.white;

	
	protected int screenWidth;

	
	protected int screenHeight;

	
	private boolean pushed;

	
	private Rectangle clip;

	
	private DoubleBuffer worldClip = BufferUtils.createDoubleBuffer(4);

	
	private ByteBuffer readBuffer = BufferUtils.createByteBuffer(4);

	
	private boolean antialias;

	
	private Rectangle worldClipRecord;

	
	private int currentDrawingMode = MODE_NORMAL;

	
	private float lineWidth = 1;

	
	private ArrayList stack = new ArrayList();
	
	private int stackIndex;

	
	public Graphics() {	
	}
	
	
	public Graphics(int width, int height) {
		if (DEFAULT_FONT == null) {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					try {
						DEFAULT_FONT = new AngelCodeFont(
								"org/newdawn/slick/data/defaultfont.fnt",
								"org/newdawn/slick/data/defaultfont.png");
					} catch (SlickException e) {
						Log.error(e);
					}
					return null; 
				}
			});
		}
		
		this.font = DEFAULT_FONT;
		screenWidth = width;
		screenHeight = height;
	}

	
	void setDimensions(int width, int height) {
		screenWidth = width;
		screenHeight = height;
	}
	
	
	public void setDrawMode(int mode) {
		predraw();
		currentDrawingMode = mode;
		if (currentDrawingMode == MODE_NORMAL) {
			GL.glEnable(SGL.GL_BLEND);
			GL.glColorMask(true, true, true, true);
			GL.glBlendFunc(SGL.GL_SRC_ALPHA, SGL.GL_ONE_MINUS_SRC_ALPHA);
		}
		if (currentDrawingMode == MODE_ALPHA_MAP) {
			GL.glDisable(SGL.GL_BLEND);
			GL.glColorMask(false, false, false, true);
		}
		if (currentDrawingMode == MODE_ALPHA_BLEND) {
			GL.glEnable(SGL.GL_BLEND);
			GL.glColorMask(true, true, true, false);
			GL.glBlendFunc(SGL.GL_DST_ALPHA, SGL.GL_ONE_MINUS_DST_ALPHA);
		}
		if (currentDrawingMode == MODE_COLOR_MULTIPLY) {
			GL.glEnable(SGL.GL_BLEND);
			GL.glColorMask(true, true, true, true);
			GL.glBlendFunc(SGL.GL_ONE_MINUS_SRC_COLOR, SGL.GL_SRC_COLOR);
		}
		if (currentDrawingMode == MODE_ADD) {
			GL.glEnable(SGL.GL_BLEND);
			GL.glColorMask(true, true, true, true);
			GL.glBlendFunc(SGL.GL_ONE, SGL.GL_ONE);
		}
		if (currentDrawingMode == MODE_SCREEN) {
			GL.glEnable(SGL.GL_BLEND);
			GL.glColorMask(true, true, true, true);
			GL.glBlendFunc(SGL.GL_ONE, SGL.GL_ONE_MINUS_SRC_COLOR);
		}
		postdraw();
	}

	
	public void clearAlphaMap() {
		pushTransform();
		GL.glLoadIdentity();
		
		int originalMode = currentDrawingMode;
		setDrawMode(MODE_ALPHA_MAP);
		setColor(new Color(0,0,0,0));
		fillRect(0, 0, screenWidth, screenHeight);
		setColor(currentColor);
		setDrawMode(originalMode);
		
		popTransform();
	}

	
	private void predraw() {
		setCurrent(this);
	}

	
	private void postdraw() {
	}

	
	protected void enable() {
	}

	
	public void flush() {
		if (currentGraphics == this) {
			currentGraphics.disable();
			currentGraphics = null;
		}
	}

	
	protected void disable() {
	}

	
	public Font getFont() {
		return font;
	}

	
	public void setBackground(Color color) {
		predraw();
		GL.glClearColor(color.r, color.g, color.b, color.a);
		postdraw();
	}

	
	public Color getBackground() {
		predraw();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL.glGetFloat(SGL.GL_COLOR_CLEAR_VALUE, buffer);
		postdraw();

		return new Color(buffer);
	}

	
	public void clear() {
		predraw();
		GL.glClear(SGL.GL_COLOR_BUFFER_BIT);
		postdraw();
	}

	
	public void resetTransform() {
		sx = 1;
		sy = 1;
		
		if (pushed) {
			predraw();
			GL.glPopMatrix();
			pushed = false;
			postdraw();
		}
	}

	
	private void checkPush() {
		if (!pushed) {
			predraw();
			GL.glPushMatrix();
			pushed = true;
			postdraw();
		}
	}

	
	public void scale(float sx, float sy) {
		this.sx = this.sx * sx;
		this.sy = this.sy * sy;
		
		checkPush();

		predraw();
		GL.glScalef(sx, sy, 1);
		postdraw();
	}

	
	public void rotate(float rx, float ry, float ang) {
		checkPush();

		predraw();
		translate(rx, ry);
		GL.glRotatef(ang, 0, 0, 1);
		translate(-rx, -ry);
		postdraw();
	}

	
	public void translate(float x, float y) {
		checkPush();

		predraw();
		GL.glTranslatef(x, y, 0);
		postdraw();
	}

	
	public void setFont(Font font) {
		this.font = font;
	}

	
	public void resetFont() {
		font = DEFAULT_FONT;
	}

	
	public void setColor(Color color) {
		if (color == null) {
			return;
		}
		
		currentColor = new Color(color);
		predraw();
		currentColor.bind();
		postdraw();
	}

	
	public Color getColor() {
		return new Color(currentColor);
	}

	
	public void drawLine(float x1, float y1, float x2, float y2) {
		float lineWidth = this.lineWidth - 1;
		
		if (LSR.applyGLLineFixes()) {
			if (x1 == x2) {
				if (y1 > y2) {
					float temp = y2;
					y2 = y1;
					y1 = temp;
				}
				float step = 1 / sy;
				lineWidth = lineWidth / sy;
				fillRect(x1-(lineWidth/2.0f),y1-(lineWidth/2.0f),lineWidth+step,(y2-y1)+lineWidth+step);
				return;
			} else if (y1 == y2) {
				if (x1 > x2) {
					float temp = x2;
					x2 = x1;
					x1 = temp;
				}
				float step = 1 / sx;
				lineWidth = lineWidth / sx;
				fillRect(x1-(lineWidth/2.0f),y1-(lineWidth/2.0f),(x2-x1)+lineWidth+step,lineWidth+step);
				return;
			}
		}
		
		predraw();
		currentColor.bind();
		TextureImpl.bindNone();

		LSR.start();
		LSR.vertex(x1,y1);
		LSR.vertex(x2,y2);
		LSR.end();
		
		postdraw();
	}

	
	public void draw(Shape shape, ShapeFill fill) {
		predraw();
		TextureImpl.bindNone();

		ShapeRenderer.draw(shape, fill);

		currentColor.bind();
		postdraw();
	}

	
	public void fill(Shape shape, ShapeFill fill) {
		predraw();
		TextureImpl.bindNone();

		ShapeRenderer.fill(shape, fill);

		currentColor.bind();
		postdraw();
	}

	
	public void draw(Shape shape) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		ShapeRenderer.draw(shape);

		postdraw();
	}

	
	public void fill(Shape shape) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		ShapeRenderer.fill(shape);

		postdraw();
	}

	
	public void texture(Shape shape, Image image) {
		texture(shape, image, 0.01f, 0.01f, false);
	}

	
	public void texture(Shape shape, Image image, ShapeFill fill) {
		texture(shape, image, 0.01f, 0.01f, fill);
	}

	
	public void texture(Shape shape, Image image, boolean fit) {
		if (fit) {
			texture(shape, image, 1, 1, true);
		} else {
			texture(shape, image, 0.01f, 0.01f, false);
		}
	}

	
	public void texture(Shape shape, Image image, float scaleX, float scaleY) {
		texture(shape, image, scaleX, scaleY, false);
	}

	
	public void texture(Shape shape, Image image, float scaleX, float scaleY,
			boolean fit) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		if (fit) {
			ShapeRenderer.textureFit(shape, image, scaleX, scaleY);
		} else {
			ShapeRenderer.texture(shape, image, scaleX, scaleY);
		}
		
		postdraw();
	}

	
	public void texture(Shape shape, Image image, float scaleX, float scaleY,
			ShapeFill fill) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		ShapeRenderer.texture(shape, image, scaleX, scaleY, fill);

		postdraw();
	}

	
	public void drawRect(float x1, float y1, float width, float height) {
		float lineWidth = getLineWidth();
		
		drawLine(x1,y1,x1+width,y1);
		drawLine(x1+width,y1,x1+width,y1+height);
		drawLine(x1+width,y1+height,x1,y1+height);
		drawLine(x1,y1+height,x1,y1);
	}

	
	public void clearClip() {
		clip = null;
		predraw();
		GL.glDisable(SGL.GL_SCISSOR_TEST);
		postdraw();
	}

	
	public void setWorldClip(float x, float y, float width, float height) {
		predraw();
		worldClipRecord = new Rectangle(x, y, width, height);
		
		GL.glEnable(SGL.GL_CLIP_PLANE0);
		worldClip.put(1).put(0).put(0).put(-x).flip();
		GL.glClipPlane(SGL.GL_CLIP_PLANE0, worldClip);
		GL.glEnable(SGL.GL_CLIP_PLANE1);
		worldClip.put(-1).put(0).put(0).put(x + width).flip();
		GL.glClipPlane(SGL.GL_CLIP_PLANE1, worldClip);

		GL.glEnable(SGL.GL_CLIP_PLANE2);
		worldClip.put(0).put(1).put(0).put(-y).flip();
		GL.glClipPlane(SGL.GL_CLIP_PLANE2, worldClip);
		GL.glEnable(SGL.GL_CLIP_PLANE3);
		worldClip.put(0).put(-1).put(0).put(y + height).flip();
		GL.glClipPlane(SGL.GL_CLIP_PLANE3, worldClip);
		postdraw();
	}

	
	public void clearWorldClip() {
		predraw();
		worldClipRecord = null;
		GL.glDisable(SGL.GL_CLIP_PLANE0);
		GL.glDisable(SGL.GL_CLIP_PLANE1);
		GL.glDisable(SGL.GL_CLIP_PLANE2);
		GL.glDisable(SGL.GL_CLIP_PLANE3);
		postdraw();
	}

	
	public void setWorldClip(Rectangle clip) {
		if (clip == null) {
			clearWorldClip();
		} else {
			setWorldClip(clip.getX(), clip.getY(), clip.getWidth(), clip
					.getHeight());
		}
	}

	
	public Rectangle getWorldClip() {
		return worldClipRecord;
	}

	
	public void setClip(int x, int y, int width, int height) {
		predraw();
		
		if (clip == null) {
			GL.glEnable(SGL.GL_SCISSOR_TEST);
			clip = new Rectangle(x, y, width, height);
		} else {
			clip.setBounds(x,y,width,height);
		}
		
		GL.glScissor(x, screenHeight - y - height, width, height);
		postdraw();
	}

	
	public void setClip(Rectangle rect) {
		if (rect == null) {
			clearClip();
			return;
		}

		setClip((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
	}

	
	public Rectangle getClip() {
		return clip;
	}

	
	public void fillRect(float x, float y, float width, float height,
			Image pattern, float offX, float offY) {
		int cols = ((int) Math.ceil(width / pattern.getWidth())) + 2;
		int rows = ((int) Math.ceil(height / pattern.getHeight())) + 2;

		Rectangle preClip = getWorldClip();
		setWorldClip(x, y, width, height);

		predraw();
		
		for (int c = 0; c < cols; c++) {
			for (int r = 0; r < rows; r++) {
				pattern.draw(c * pattern.getWidth() + x - offX, r
						* pattern.getHeight() + y - offY);
			}
		}
		postdraw();

		setWorldClip(preClip);
	}

	
	public void fillRect(float x1, float y1, float width, float height) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		GL.glBegin(SGL.GL_QUADS);
		GL.glVertex2f(x1, y1);
		GL.glVertex2f(x1 + width, y1);
		GL.glVertex2f(x1 + width, y1 + height);
		GL.glVertex2f(x1, y1 + height);
		GL.glEnd();
		postdraw();
	}

	
	public void drawOval(float x1, float y1, float width, float height) {
		drawOval(x1, y1, width, height, DEFAULT_SEGMENTS);
	}

	
	public void drawOval(float x1, float y1, float width, float height,
			int segments) {
		drawArc(x1, y1, width, height, segments, 0, 360);
	}

	
	public void drawArc(float x1, float y1, float width, float height,
			float start, float end) {
		drawArc(x1, y1, width, height, DEFAULT_SEGMENTS, start, end);
	}

	
	public void drawArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		while (end < start) {
			end += 360;
		}

		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);

		LSR.start();
		int step = 360 / segments;

		for (int a = (int) start; a < (int) (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}
			float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang)) * width / 2.0f));
			float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang)) * height / 2.0f));

			LSR.vertex(x,y);
		}
		LSR.end();
		postdraw();
	}

	
	public void fillOval(float x1, float y1, float width, float height) {
		fillOval(x1, y1, width, height, DEFAULT_SEGMENTS);
	}

	
	public void fillOval(float x1, float y1, float width, float height,
			int segments) {
		fillArc(x1, y1, width, height, segments, 0, 360);
	}

	
	public void fillArc(float x1, float y1, float width, float height,
			float start, float end) {
		fillArc(x1, y1, width, height, DEFAULT_SEGMENTS, start, end);
	}

	
	public void fillArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		predraw();
		TextureImpl.bindNone();
		currentColor.bind();

		while (end < start) {
			end += 360;
		}

		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);

		GL.glBegin(SGL.GL_TRIANGLE_FAN);
		int step = 360 / segments;

		GL.glVertex2f(cx, cy);

		for (int a = (int) start; a < (int) (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}

			float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang)) * width / 2.0f));
			float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang)) * height / 2.0f));

			GL.glVertex2f(x, y);
		}
		GL.glEnd();

		if (antialias) {
			GL.glBegin(SGL.GL_TRIANGLE_FAN);
			GL.glVertex2f(cx, cy);
			if (end != 360) {
				end -= 10;
			}

			for (int a = (int) start; a < (int) (end + step); a += step) {
				float ang = a;
				if (ang > end) {
					ang = end;
				}

				float x = (float) (cx + (FastTrig.cos(Math.toRadians(ang + 10))
						* width / 2.0f));
				float y = (float) (cy + (FastTrig.sin(Math.toRadians(ang + 10))
						* height / 2.0f));

				GL.glVertex2f(x, y);
			}
			GL.glEnd();
		}

		postdraw();
	}

	
	public void drawRoundRect(float x, float y, float width, float height,
			int cornerRadius) {
		drawRoundRect(x, y, width, height, cornerRadius, DEFAULT_SEGMENTS);
	}

	
	public void drawRoundRect(float x, float y, float width, float height,
			int cornerRadius, int segs) {
		if (cornerRadius < 0)
			throw new IllegalArgumentException("corner radius must be > 0");
		if (cornerRadius == 0) {
			drawRect(x, y, width, height);
			return;
		}

		int mr = (int) Math.min(width, height) / 2;
		
		if (cornerRadius > mr) {
			cornerRadius = mr;
		}

		drawLine(x + cornerRadius, y, x + width - cornerRadius, y);
		drawLine(x, y + cornerRadius, x, y + height - cornerRadius);
		drawLine(x + width, y + cornerRadius, x + width, y + height
				- cornerRadius);
		drawLine(x + cornerRadius, y + height, x + width - cornerRadius, y
				+ height);

		float d = cornerRadius * 2;
		
		drawArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		
		drawArc(x, y + height - d, d, d, segs, 90, 180);
		
		drawArc(x + width - d, y, d, d, segs, 270, 360);
		
		drawArc(x, y, d, d, segs, 180, 270);
	}

	
	public void fillRoundRect(float x, float y, float width, float height,
			int cornerRadius) {
		fillRoundRect(x, y, width, height, cornerRadius, DEFAULT_SEGMENTS);
	}

	
	public void fillRoundRect(float x, float y, float width, float height,
			int cornerRadius, int segs) {
		if (cornerRadius < 0)
			throw new IllegalArgumentException("corner radius must be > 0");
		if (cornerRadius == 0) {
			fillRect(x, y, width, height);
			return;
		}

		int mr = (int) Math.min(width, height) / 2;
		
		if (cornerRadius > mr) {
			cornerRadius = mr;
		}

		float d = cornerRadius * 2;

		fillRect(x + cornerRadius, y, width - d, cornerRadius);
		fillRect(x, y + cornerRadius, cornerRadius, height - d);
		fillRect(x + width - cornerRadius, y + cornerRadius, cornerRadius,
				height - d);
		fillRect(x + cornerRadius, y + height - cornerRadius, width - d,
				cornerRadius);
		fillRect(x + cornerRadius, y + cornerRadius, width - d, height - d);

		
		fillArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		
		fillArc(x, y + height - d, d, d, segs, 90, 180);
		
		fillArc(x + width - d, y, d, d, segs, 270, 360);
		
		fillArc(x, y, d, d, segs, 180, 270);
	}

	
	public void setLineWidth(float width) {
		predraw();
		this.lineWidth = width;
		LSR.setWidth(width);
		GL.glPointSize(width);
		postdraw();
	}

	
	public float getLineWidth() {
		return lineWidth;
	}

	
	public void resetLineWidth() {
		predraw();
		
		Renderer.getLineStripRenderer().setWidth(1.0f);
		GL.glLineWidth(1.0f);
		GL.glPointSize(1.0f);
		
		postdraw();
	}

	
	public void setAntiAlias(boolean anti) {
		predraw();
		antialias = anti;
		LSR.setAntiAlias(anti);
		if (anti) {
			GL.glEnable(SGL.GL_POLYGON_SMOOTH);
		} else {
			GL.glDisable(SGL.GL_POLYGON_SMOOTH);
		}
		postdraw();
	}

	
	public boolean isAntiAlias() {
		return antialias;
	}

	
	public void drawString(String str, float x, float y) {
		predraw();
		font.drawString(x, y, str, currentColor);
		postdraw();
	}

	
	public void drawImage(Image image, float x, float y, Color col) {
		predraw();
		image.draw(x, y, col);
		currentColor.bind();
		postdraw();
	}

	
	public void drawAnimation(Animation anim, float x, float y) {
		drawAnimation(anim, x, y, Color.white);
	}

	
	public void drawAnimation(Animation anim, float x, float y, Color col) {
		predraw();
		anim.draw(x, y, col);
		currentColor.bind();
		postdraw();
	}

	
	public void drawImage(Image image, float x, float y) {
		drawImage(image, x, y, Color.white);
	}

	
	public void drawImage(Image image, float x, float y, float x2, float y2,
			float srcx, float srcy, float srcx2, float srcy2) {
		predraw();
		image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2);
		currentColor.bind();
		postdraw();
	}

	
	public void drawImage(Image image, float x, float y, float srcx,
			float srcy, float srcx2, float srcy2) {
		drawImage(image, x, y, x + image.getWidth(), y + image.getHeight(),
				srcx, srcy, srcx2, srcy2);
	}

	
	public void copyArea(Image target, int x, int y) {
		int format = target.getTexture().hasAlpha() ? SGL.GL_RGBA : SGL.GL_RGB;
		target.bind();
		GL.glCopyTexImage2D(SGL.GL_TEXTURE_2D, 0, format, x, screenHeight
				- (y + target.getHeight()), target.getTexture()
				.getTextureWidth(), target.getTexture().getTextureHeight(), 0);
		target.ensureInverted();
	}

	
	private int translate(byte b) {
		if (b < 0) {
			return 256 + b;
		}

		return b;
	}

	
	public Color getPixel(int x, int y) {
		predraw();
		GL.glReadPixels(x, screenHeight - y, 1, 1, SGL.GL_RGBA,
				SGL.GL_UNSIGNED_BYTE, readBuffer);
		postdraw();

		return new Color(translate(readBuffer.get(0)), translate(readBuffer
				.get(1)), translate(readBuffer.get(2)), translate(readBuffer
				.get(3)));
	}

	
	public void getArea(int x, int y, int width, int height, ByteBuffer target)
	{
		if (target.capacity() < width * height * 4) 
		{
			throw new IllegalArgumentException("Byte buffer provided to get area is not big enough");
		}
		
		predraw();	
		GL.glReadPixels(x, screenHeight - y - height, width, height, SGL.GL_RGBA,
				SGL.GL_UNSIGNED_BYTE, target);
		postdraw();
	}
	
	
	public void drawImage(Image image, float x, float y, float x2, float y2,
			float srcx, float srcy, float srcx2, float srcy2, Color col) {
		predraw();
		image.draw(x, y, x2, y2, srcx, srcy, srcx2, srcy2, col);
		currentColor.bind();
		postdraw();
	}

	
	public void drawImage(Image image, float x, float y, float srcx,
			float srcy, float srcx2, float srcy2, Color col) {
		drawImage(image, x, y, x + image.getWidth(), y + image.getHeight(),
				srcx, srcy, srcx2, srcy2, col);
	}

	
	public void drawGradientLine(float x1, float y1, float red1, float green1,
									float blue1, float alpha1, float x2, float y2, float red2,
									float green2, float blue2, float alpha2) {
		predraw();

		TextureImpl.bindNone();

		GL.glBegin(SGL.GL_LINES);

		GL.glColor4f(red1, green1, blue1, alpha1);
		GL.glVertex2f(x1, y1);

		GL.glColor4f(red2, green2, blue2, alpha2);
		GL.glVertex2f(x2, y2);

		GL.glEnd();

		postdraw();
	}

	
	public void drawGradientLine(float x1, float y1, Color Color1, float x2,
								 float y2, Color Color2) {
		predraw();

		TextureImpl.bindNone();

		GL.glBegin(SGL.GL_LINES);

		Color1.bind();
		GL.glVertex2f(x1, y1);

		Color2.bind();
		GL.glVertex2f(x2, y2);

		GL.glEnd();

		postdraw();
	}
	
	
	public void pushTransform() {
		predraw();
		
		FloatBuffer buffer;
		if (stackIndex >= stack.size()) {
			buffer = BufferUtils.createFloatBuffer(18);
			stack.add(buffer);
		} else {
			buffer = (FloatBuffer) stack.get(stackIndex);
		}
		
		GL.glGetFloat(SGL.GL_MODELVIEW_MATRIX, buffer);
		buffer.put(16, sx);
		buffer.put(17, sy);
		stackIndex++;
		
		postdraw();
	}
	
	
	public void popTransform() {
		if (stackIndex == 0) {
			throw new RuntimeException("Attempt to pop a transform that hasn't be pushed");
		}
		
		predraw();
		
		stackIndex--;
		FloatBuffer oldBuffer = (FloatBuffer) stack.get(stackIndex);
		GL.glLoadMatrix(oldBuffer);
		sx = oldBuffer.get(16);
		sy = oldBuffer.get(17);
		
		postdraw();
	}
	
	
	public void destroy() {
		
	}
}
