package org.newdawn.slick;

import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.pbuffer.GraphicsFactory;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;


public class Image implements Renderable {
	
	public static final int TOP_LEFT = 0;
	
	public static final int TOP_RIGHT = 1;
	
	public static final int BOTTOM_RIGHT = 2;
	
	public static final int BOTTOM_LEFT = 3;
	
	
	protected static SGL GL = Renderer.get();
	
	
	protected static Image inUse;
	
	public static final int FILTER_LINEAR = 1;
	
	public static final int FILTER_NEAREST = 2;
	
	
	protected Texture texture;
	
	protected int width;
	
	protected int height;
	
	protected float textureWidth;
	
	protected float textureHeight;
	
	protected float textureOffsetX;
	
	protected float textureOffsetY;
    
	protected float angle;
	
	protected float alpha = 1.0f;
	
	protected String ref;
	
	protected boolean inited = false;
	
	protected byte[] pixelData;
	
	protected boolean destroyed;

	
    protected float centerX; 
    
    protected float centerY; 
    
    
    protected String name;
    
    
    protected Color[] corners;
    
    private int filter = SGL.GL_LINEAR;
    
    
    private boolean flipped;
    
    private Color transparent;
    
	
	protected Image(Image other) {
		this.width = other.getWidth();
		this.height = other.getHeight();
		this.texture = other.texture;
		this.textureWidth = other.textureWidth;
		this.textureHeight = other.textureHeight;
		this.ref = other.ref;
		this.textureOffsetX = other.textureOffsetX;
		this.textureOffsetY = other.textureOffsetY;
	
		centerX = width / 2;
		centerY = height / 2;
		inited = true;
	}
	
	
	protected Image() {
	}
	
    
	public Image(Texture texture) {
		this.texture = texture;
		ref = texture.toString();
		clampTexture();
	}
	    
	
	public Image(String ref) throws SlickException  {
		this(ref, false);
	}

	
	public Image(String ref, Color trans) throws SlickException  {
		this(ref, false, FILTER_LINEAR, trans);
	}
	
	
	public Image(String ref, boolean flipped) throws SlickException {
		this(ref, flipped, FILTER_LINEAR);
	}

	
	public Image(String ref, boolean flipped, int filter) throws SlickException {
		this(ref, flipped, filter, null);
	}
	
	
	public Image(String ref, boolean flipped, int f, Color transparent) throws SlickException {
		this.filter = f == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST;
		this.transparent = transparent;
		this.flipped = flipped;
		
		try {
			this.ref = ref;
			int[] trans = null;
			if (transparent != null) {
				trans = new int[3];
				trans[0] = (int) (transparent.r * 255);
				trans[1] = (int) (transparent.g * 255);
				trans[2] = (int) (transparent.b * 255);
			}
			texture = InternalTextureLoader.get().getTexture(ref, flipped, filter, trans);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to load image from: "+ref, e);
		}
	}
	
	
	public void setFilter(int f) {
		this.filter = f == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST;

		texture.bind();
		GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_MIN_FILTER, filter); 
        GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_MAG_FILTER, filter); 
	}
	
	
	public Image(int width, int height) throws SlickException {
		this(width, height, FILTER_NEAREST);
	}
	
	
	public Image(int width, int height, int f) throws SlickException {
		ref = super.toString();
		this.filter = f == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST;
		
		try {
			texture = InternalTextureLoader.get().createTexture(width, height, this.filter);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to create empty image "+width+"x"+height);
		}
		
		init();
	}
	
	
	public Image(InputStream in, String ref, boolean flipped) throws SlickException {
		this(in, ref, flipped, FILTER_LINEAR);
	}

	
	public Image(InputStream in, String ref, boolean flipped,int filter) throws SlickException {
		load(in, ref, flipped, filter, null);
	}
	
	
	Image(ImageBuffer buffer) {
		this(buffer, FILTER_LINEAR);
        TextureImpl.bindNone();
	}
	
	
	Image(ImageBuffer buffer, int filter) {
		this((ImageData) buffer, filter);
        TextureImpl.bindNone();
	}

	
	public Image(ImageData data) {
		this(data, FILTER_LINEAR);
	}
	
	
	public Image(ImageData data, int f) {
		try {
			this.filter = f == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST;
			texture = InternalTextureLoader.get().getTexture(data, this.filter);
			ref = texture.toString();
		} catch (IOException e) {
			Log.error(e);
		}
	}

	
	public int getFilter() {
		return filter;
	}
	
	
	public String getResourceReference() {
		return ref;
	}
	
	
	public void setImageColor(float r, float g, float b, float a) {
		setColor(TOP_LEFT, r, g, b, a);
		setColor(TOP_RIGHT, r, g, b, a);
		setColor(BOTTOM_LEFT, r, g, b, a);
		setColor(BOTTOM_RIGHT, r, g, b, a);
	}
	
	
	public void setImageColor(float r, float g, float b) {
		setColor(TOP_LEFT, r, g, b);
		setColor(TOP_RIGHT, r, g, b);
		setColor(BOTTOM_LEFT, r, g, b);
		setColor(BOTTOM_RIGHT, r, g, b);
	}
	
	
	public void setColor(int corner, float r, float g, float b, float a) {
		if (corners == null) {
			corners = new Color[] {new Color(1,1,1,1f),new Color(1,1,1,1f), new Color(1,1,1,1f), new Color(1,1,1,1f)};
		}
		
		corners[corner].r = r;
		corners[corner].g = g;
		corners[corner].b = b;
		corners[corner].a = a;
	}

	
	public void setColor(int corner, float r, float g, float b) {
		if (corners == null) {
			corners = new Color[] {new Color(1,1,1,1f),new Color(1,1,1,1f), new Color(1,1,1,1f), new Color(1,1,1,1f)};
		}
		
		corners[corner].r = r;
		corners[corner].g = g;
		corners[corner].b = b;
	}
	
	
	public void clampTexture() {
        if (GL.canTextureMirrorClamp()) {
        	GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_S, SGL.GL_MIRROR_CLAMP_TO_EDGE_EXT);
        	GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_T, SGL.GL_MIRROR_CLAMP_TO_EDGE_EXT);
        } else {
        	GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_S, SGL.GL_CLAMP);
        	GL.glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_T, SGL.GL_CLAMP);
        }
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public Graphics getGraphics() throws SlickException {
		return GraphicsFactory.getGraphicsForImage(this);
	}
	
	
	private void load(InputStream in, String ref, boolean flipped, int f, Color transparent) throws SlickException {
		this.filter = f == FILTER_LINEAR ? SGL.GL_LINEAR : SGL.GL_NEAREST;
		
		try {
			this.ref = ref;
			int[] trans = null;
			if (transparent != null) {
				trans = new int[3];
				trans[0] = (int) (transparent.r * 255);
				trans[1] = (int) (transparent.g * 255);
				trans[2] = (int) (transparent.b * 255);
			}
			texture = InternalTextureLoader.get().getTexture(in, ref, flipped, filter, trans);
		} catch (IOException e) {
			Log.error(e);
			throw new SlickException("Failed to load image from: "+ref, e);
		}
	}

	
	public void bind() {
		texture.bind();
	}

	
	protected void reinit() {
		inited = false;
		init();
	}
	
	
	protected final void init() {
		if (inited) {
			return;
		}
		
		inited = true;
		if (texture != null) {
			width = texture.getImageWidth();
			height = texture.getImageHeight();
			textureOffsetX = 0;
			textureOffsetY = 0;
			textureWidth = texture.getWidth();
			textureHeight = texture.getHeight();
		}
		
		initImpl();
	
		centerX = width / 2;
		centerY = height / 2;
	}

	
	protected void initImpl() {
		
	}
	
	
	public void draw() {
		draw(0,0);
	}
	
	
	public void drawCentered(float x, float y) {
		draw(x-(getWidth()/2),y-(getHeight()/2));
	}
	
	
	public void draw(float x, float y) {
		init();
		draw(x,y,width,height);
	}
	
	
	public void draw(float x, float y, Color filter) {
		init();
		draw(x,y,width,height, filter);
	}

	
	public void drawEmbedded(float x,float y,float width,float height) {
		init();
		
		if (corners == null) {
		    GL.glTexCoord2f(textureOffsetX, textureOffsetY);
			GL.glVertex3f(x, y, 0);
			GL.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
			GL.glVertex3f(x, y + height, 0);
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
					+ textureHeight);
			GL.glVertex3f(x + width, y + height, 0);
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
			GL.glVertex3f(x + width, y, 0);
		} else {
			corners[TOP_LEFT].bind();
		    GL.glTexCoord2f(textureOffsetX, textureOffsetY);
			GL.glVertex3f(x, y, 0);
			corners[BOTTOM_LEFT].bind();
			GL.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
			GL.glVertex3f(x, y + height, 0);
			corners[BOTTOM_RIGHT].bind();
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
					+ textureHeight);
			GL.glVertex3f(x + width, y + height, 0);
			corners[TOP_RIGHT].bind();
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
			GL.glVertex3f(x + width, y, 0);
		}
	}

	
	public float getTextureOffsetX() {
		init();
		
		return textureOffsetX;
	}

	
	public float getTextureOffsetY() {
		init();
		
		return textureOffsetY;
	}

	
	public float getTextureWidth() {
		init();
		
		return textureWidth;
	}

	
	public float getTextureHeight() {
		init();
		
		return textureHeight;
	}
	
	
	public void draw(float x,float y,float scale) {
		init();
		draw(x,y,width*scale,height*scale,Color.white);
	}
	
	
	public void draw(float x,float y,float scale,Color filter) {
		init();
		draw(x,y,width*scale,height*scale,filter);
	}
	
	
	public void draw(float x,float y,float width,float height) {
		init();
		draw(x,y,width,height,Color.white);
	}

	
    public void drawSheared(float x,float y, float hshear, float vshear) { 
    	this.drawSheared(x, y, hshear, vshear, Color.white);
    }
	
    public void drawSheared(float x,float y, float hshear, float vshear, Color filter) { 
    	if (alpha != 1) {
    		if (filter == null) {
    			filter = Color.white;
    		}
    		
    		filter = new Color(filter);
    		filter.a *= alpha;
    	}
        if (filter != null) { 
            filter.bind(); 
        } 
        
        texture.bind(); 
        
        GL.glTranslatef(x, y, 0);
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        
        GL.glBegin(SGL.GL_QUADS); 
        	init();
		
		    GL.glTexCoord2f(textureOffsetX, textureOffsetY);
			GL.glVertex3f(0, 0, 0);
			GL.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
			GL.glVertex3f(hshear, height, 0);
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
					+ textureHeight);
			GL.glVertex3f(width + hshear, height + vshear, 0);
			GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
			GL.glVertex3f(width, vshear, 0);
        GL.glEnd(); 
        
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(-angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        GL.glTranslatef(-x, -y, 0);
    } 
    
	
    public void draw(float x,float y,float width,float height,Color filter) { 
    	if (alpha != 1) {
    		if (filter == null) {
    			filter = Color.white;
    		}
    		
    		filter = new Color(filter);
    		filter.a *= alpha;
    	}
        if (filter != null) { 
            filter.bind(); 
        } 
       
        texture.bind(); 
        
        GL.glTranslatef(x, y, 0);
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        
        GL.glBegin(SGL.GL_QUADS); 
            drawEmbedded(0,0,width,height); 
        GL.glEnd(); 
        
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(-angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        GL.glTranslatef(-x, -y, 0);
    } 

	
	public void drawFlash(float x,float y,float width,float height) {
		drawFlash(x,y,width,height,Color.white);
	}
	
	
	public void setCenterOfRotation(float x, float y) {
		centerX = x;
		centerY = y;
	}

	
	public float getCenterOfRotationX() {
		init();
		
		return centerX;
	}
	
	
	public float getCenterOfRotationY() {
		init();
		
		return centerY;
	}
	
	
	public void drawFlash(float x,float y,float width,float height, Color col) {
		init();
		
		col.bind();
		texture.bind();

		if (GL.canSecondaryColor()) {
			GL.glEnable(SGL.GL_COLOR_SUM_EXT);
			GL.glSecondaryColor3ubEXT((byte)(col.r * 255), 
													 (byte)(col.g * 255), 
													 (byte)(col.b * 255));
		}
		
		GL.glTexEnvi(SGL.GL_TEXTURE_ENV, SGL.GL_TEXTURE_ENV_MODE, SGL.GL_MODULATE);

        GL.glTranslatef(x, y, 0);
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        
		GL.glBegin(SGL.GL_QUADS);
			drawEmbedded(0,0,width,height);
		GL.glEnd();

        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(-angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        GL.glTranslatef(-x, -y, 0);
        
		if (GL.canSecondaryColor()) {
			GL.glDisable(SGL.GL_COLOR_SUM_EXT);
		}
	}

	
	public void drawFlash(float x,float y) {
		drawFlash(x,y,getWidth(),getHeight());
	}
	
    
    public void setRotation(float angle) { 
        this.angle = angle % 360.0f; 
    } 
    
    
    public float getRotation() { 
        return angle; 
    } 
    
    
    public float getAlpha() {
    	return alpha;
    }
    
    
    public void setAlpha(float alpha) {
    	this.alpha = alpha;
    }
    
    
    public void rotate(float angle) { 
        this.angle += angle;
        this.angle = this.angle % 360;
    } 

	
	public Image getSubImage(int x,int y,int width,int height) {
		init();
		
		float newTextureOffsetX = ((x / (float) this.width) * textureWidth) + textureOffsetX;
		float newTextureOffsetY = ((y / (float) this.height) * textureHeight) + textureOffsetY;
		float newTextureWidth = ((width / (float) this.width) * textureWidth);
		float newTextureHeight = ((height / (float) this.height) * textureHeight);
		
		Image sub = new Image();
		sub.inited = true;
		sub.texture = this.texture;
		sub.textureOffsetX = newTextureOffsetX;
		sub.textureOffsetY = newTextureOffsetY;
		sub.textureWidth = newTextureWidth;
		sub.textureHeight = newTextureHeight;
		
		sub.width = width;
		sub.height = height;
		sub.ref = ref;
		sub.centerX = width / 2;
		sub.centerY = height / 2;
		
		return sub;
	}

	
	public void draw(float x, float y, float srcx, float srcy, float srcx2, float srcy2) {
		draw(x,y,x+width,y+height,srcx,srcy,srcx2,srcy2);
	}
	
	
	public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
		draw(x,y,x2,y2,srcx,srcy,srcx2,srcy2,Color.white);
	}
	
	
	public void draw(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
		init();

    	if (alpha != 1) {
    		if (filter == null) {
    			filter = Color.white;
    		}
    		
    		filter = new Color(filter);
    		filter.a *= alpha;
    	}
		filter.bind();
		texture.bind();
		
        GL.glTranslatef(x, y, 0);
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        
        GL.glBegin(SGL.GL_QUADS); 
			drawEmbedded(0,0,x2-x,y2-y,srcx,srcy,srcx2,srcy2);
        GL.glEnd(); 
        
        if (angle != 0) {
	        GL.glTranslatef(centerX, centerY, 0.0f); 
	        GL.glRotatef(-angle, 0.0f, 0.0f, 1.0f); 
	        GL.glTranslatef(-centerX, -centerY, 0.0f); 
        }
        GL.glTranslatef(-x, -y, 0);
        



	}
	
	
	public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2) {
		drawEmbedded(x,y,x2,y2,srcx,srcy,srcx2,srcy2,null);
	}
	
	
	public void drawEmbedded(float x, float y, float x2, float y2, float srcx, float srcy, float srcx2, float srcy2, Color filter) {
		if (filter != null) {
			filter.bind();
		}
		
		float mywidth = x2 - x;
		float myheight = y2 - y;
		float texwidth = srcx2 - srcx;
		float texheight = srcy2 - srcy;

		float newTextureOffsetX = (((srcx) / (width)) * textureWidth)
				+ textureOffsetX;
		float newTextureOffsetY = (((srcy) / (height)) * textureHeight)
				+ textureOffsetY;
		float newTextureWidth = ((texwidth) / (width))
				* textureWidth;
		float newTextureHeight = ((texheight) / (height))
				* textureHeight;

		GL.glTexCoord2f(newTextureOffsetX, newTextureOffsetY);
		GL.glVertex3f(x,y, 0.0f);
		GL.glTexCoord2f(newTextureOffsetX, newTextureOffsetY
				+ newTextureHeight);
		GL.glVertex3f(x,(y + myheight), 0.0f);
		GL.glTexCoord2f(newTextureOffsetX + newTextureWidth,
				newTextureOffsetY + newTextureHeight);
		GL.glVertex3f((x + mywidth),(y + myheight), 0.0f);
		GL.glTexCoord2f(newTextureOffsetX + newTextureWidth,
				newTextureOffsetY);
		GL.glVertex3f((x + mywidth),y, 0.0f);
	}
	
	
	public void drawWarped(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        Color.white.bind();
        texture.bind();

        GL.glTranslatef(x1, y1, 0);
        if (angle != 0) {
            GL.glTranslatef(centerX, centerY, 0.0f);
            GL.glRotatef(angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-centerX, -centerY, 0.0f);
        }

        GL.glBegin(SGL.GL_QUADS);
        init();

        GL.glTexCoord2f(textureOffsetX, textureOffsetY);
        GL.glVertex3f(0, 0, 0);
        GL.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
        GL.glVertex3f(x2 - x1, y2 - y1, 0);
        GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
                + textureHeight);
        GL.glVertex3f(x3 - x1, y3 - y1, 0);
        GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
        GL.glVertex3f(x4 - x1, y4 - y1, 0);
        GL.glEnd();

        if (angle != 0) {
            GL.glTranslatef(centerX, centerY, 0.0f);
            GL.glRotatef(-angle, 0.0f, 0.0f, 1.0f);
            GL.glTranslatef(-centerX, -centerY, 0.0f);
        }
        GL.glTranslatef(-x1, -y1, 0);
    }
	
	
	public int getWidth() {
		init();
		return width;
	}

	
	public int getHeight() {
		init();
		return height;
	}
	
	
	public Image copy() {
		init();
		return getSubImage(0,0,width,height);
	}

	
	public Image getScaledCopy(float scale) {
		init();
		return getScaledCopy((int) (width*scale),(int) (height*scale));
	}
	
	
	public Image getScaledCopy(int width, int height) {
		init();
		Image image = copy();
		image.width = width;
		image.height = height;
		image.centerX = width / 2;
		image.centerY = height / 2;
		return image;
	}
	
	
	public void ensureInverted() {
		if (textureHeight > 0) {
			textureOffsetY = textureOffsetY + textureHeight;
			textureHeight = -textureHeight;
		}
	}
	
	
	public Image getFlippedCopy(boolean flipHorizontal, boolean flipVertical) {
		init();
		Image image = copy();
		
		if (flipHorizontal) {
			image.textureOffsetX = textureOffsetX + textureWidth;
			image.textureWidth = -textureWidth;
		}
		if (flipVertical) {
			image.textureOffsetY = textureOffsetY + textureHeight;
			image.textureHeight = -textureHeight;
		}
		
		return image;
	}

	
	public void endUse() {
		if (inUse != this) {
			throw new RuntimeException("The sprite sheet is not currently in use");
		}
		inUse = null;
		GL.glEnd();
	}
	
	
	public void startUse() {
		if (inUse != null) {
			throw new RuntimeException("Attempt to start use of a sprite sheet before ending use with another - see endUse()");
		}
		inUse = this;
		init();

		Color.white.bind();
		texture.bind();
		GL.glBegin(SGL.GL_QUADS);
	}
	
	
	public String toString() {
		init();
		
		return "[Image "+ref+" "+width+"x"+height+"  "+textureOffsetX+","+textureOffsetY+","+textureWidth+","+textureHeight+"]";
	}
	
	
	public Texture getTexture() {
		return texture;
	}
	
	
	public void setTexture(Texture texture) {
		this.texture = texture;
		reinit();
	}

	
	private int translate(byte b) {
		if (b < 0) {
			return 256 + b;
		}
		
		return b;
	}
	
	
	public Color getColor(int x, int y) {
		if (pixelData == null) {
			pixelData = texture.getTextureData();
		}
		
		int xo = (int) (textureOffsetX * texture.getTextureWidth());
		int yo = (int) (textureOffsetY * texture.getTextureHeight());
		
		if (textureWidth < 0) {
			x = xo - x;
		} else {
			x = xo + x;
		} 
		
		if (textureHeight < 0) {
			y = yo - y;
		} else {
			y = yo + y;
		}
		
		int offset = x + (y * texture.getTextureWidth());
		offset *= texture.hasAlpha() ? 4 : 3;
		
		if (texture.hasAlpha()) {
			return new Color(translate(pixelData[offset]),translate(pixelData[offset+1]),
							 translate(pixelData[offset+2]),translate(pixelData[offset+3]));
		} else {
			return new Color(translate(pixelData[offset]),translate(pixelData[offset+1]),
					 	     translate(pixelData[offset+2]));
		}
	}
	
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	
	public void destroy() throws SlickException {
		if (isDestroyed()) {
			return;
		}
		
		destroyed = true;
		texture.release();
		GraphicsFactory.releaseGraphicsForImage(this);
	}
	
	
	public void flushPixelData() {
		pixelData = null;
	}
}
