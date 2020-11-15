package org.newdawn.slick.geom;

import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.LineStripRenderer;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;


public final class ShapeRenderer {
	
	private static SGL GL = Renderer.get();
	
	private static LineStripRenderer LSR = Renderer.getLineStripRenderer();
	
    
    public static final void draw(Shape shape) {
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
        float points[] = shape.getPoints();
        
        LSR.start();
        for(int i=0;i<points.length;i+=2) {
        	LSR.vertex(points[i], points[i + 1]);
        }
        
        if (shape.closed()) {
        	LSR.vertex(points[0], points[1]);
        }
        
        LSR.end();
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }

    
    public static final void draw(Shape shape, ShapeFill fill) {
        float points[] = shape.getPoints();
        
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();

        float center[] = shape.getCenter();
        GL.glBegin(SGL.GL_LINE_STRIP);
        for(int i=0;i<points.length;i+=2) {
            fill.colorAt(shape, points[i], points[i + 1]).bind();
            Vector2f offset = fill.getOffsetAt(shape, points[i], points[i + 1]);
            GL.glVertex2f(points[i] + offset.x, points[i + 1] + offset.y);
        }
        
        if (shape.closed()) {
	        fill.colorAt(shape, points[0], points[1]).bind();
	        Vector2f offset = fill.getOffsetAt(shape, points[0], points[1]);
	        GL.glVertex2f(points[0] + offset.x, points[1] + offset.y);
        }
        GL.glEnd();
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    
    
    public static boolean validFill(Shape shape) {
    	if (shape.getTriangles() == null) {
    		return false;
    	}
        return shape.getTriangles().getTriangleCount() != 0;
    }

    
    public static final void fill(Shape shape) {
    	if (!validFill(shape)) {
    		return;
    	}
    	
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        
    	fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
				
				return null;
			}
    	});
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    
    
    private static final void fill(Shape shape, PointCallback callback) {
    	Triangulator tris = shape.getTriangles();

        GL.glBegin(SGL.GL_TRIANGLES);
        for (int i=0;i<tris.getTriangleCount();i++) {
        	for (int p=0;p<3;p++) {
        		float[] pt = tris.getTrianglePoint(i, p);
        		float[] np = callback.preRenderPoint(shape, pt[0],pt[1]);
        		
        		if (np == null) {
        			GL.glVertex2f(pt[0],pt[1]);
        		} else {
        			GL.glVertex2f(np[0],np[1]);
        		}
        	}
        }
        GL.glEnd();
    }

    
    public static final void texture(Shape shape, Image image) {
    	texture(shape, image, 0.01f, 0.01f);
    }

    
    public static final void textureFit(Shape shape, Image image) {
    	textureFit(shape, image,1f,1f);
    }
    
    
    public static final void texture(Shape shape, final Image image, final float scaleX, final float scaleY) {
    	if (!validFill(shape)) {
    		return;
    	}
    	
    	final Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
				float tx = x * scaleX;
				float ty = y * scaleY;
				
				tx = image.getTextureOffsetX() + (image.getTextureWidth() * tx);
				ty = image.getTextureOffsetY() + (image.getTextureHeight() * ty);
				
				GL.glTexCoord2f(tx, ty);
	            return null;
			}
    	});
    	
        float points[] = shape.getPoints();
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    
    
    public static final void textureFit(Shape shape, final Image image, final float scaleX, final float scaleY) {
    	if (!validFill(shape)) {
    		return;
    	}
    	
        float points[] = shape.getPoints();
        
        Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        final float minX = shape.getX();
        final float minY = shape.getY();
        final float maxX = shape.getMaxX() - minX;
        final float maxY = shape.getMaxY() - minY;

        fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
				x -= shape.getMinX();
				y -= shape.getMinY();
				
				x /= (shape.getMaxX() - shape.getMinX());
				y /= (shape.getMaxY() - shape.getMinY());
				
				float tx = x * scaleX;
				float ty = y * scaleY;
				
				tx = image.getTextureOffsetX() + (image.getTextureWidth() * tx);
				ty = image.getTextureOffsetY() + (image.getTextureHeight() * ty);
				
				GL.glTexCoord2f(tx, ty);
	            return null;
			}
    	});
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }

    
    public static final void fill(final Shape shape, final ShapeFill fill) {
        if (!validFill(shape)) {
    		return;
    	}
        
        Texture t = TextureImpl.getLastBind();
        TextureImpl.bindNone();

        final float center[] = shape.getCenter();
        fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
	            fill.colorAt(shape, x, y).bind();
	            Vector2f offset = fill.getOffsetAt(shape, x, y);
	            
	            return new float[] {offset.x + x,offset.y + y};
			}
    	});
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    

    
    public static final void texture(final Shape shape, final Image image, final float scaleX, final float scaleY, final ShapeFill fill) {
    	if (!validFill(shape)) {
    		return;
    	}
        
        Texture t = TextureImpl.getLastBind();
        image.getTexture().bind();
        
        final float center[] = shape.getCenter();
        fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
	            fill.colorAt(shape, x - center[0], y - center[1]).bind();
	            Vector2f offset = fill.getOffsetAt(shape, x, y);
	            
	            x += offset.x;
	            y += offset.y;
	            
				float tx = x * scaleX;
				float ty = y * scaleY;
				
				tx = image.getTextureOffsetX() + (image.getTextureWidth() * tx);
				ty = image.getTextureOffsetY() + (image.getTextureHeight() * ty);
				
	            GL.glTexCoord2f(tx, ty);

	            return new float[] {offset.x + x,offset.y + y};
			}
    	});
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    
    public static final void texture(final Shape shape, Image image, final TexCoordGenerator gen) {
        Texture t = TextureImpl.getLastBind();

        image.getTexture().bind();

        final float center[] = shape.getCenter();
        fill(shape, new PointCallback() {
			public float[] preRenderPoint(Shape shape, float x, float y) {
				Vector2f tex = gen.getCoordFor(x, y);
	            GL.glTexCoord2f(tex.x, tex.y);

	            return new float[] {x,y};
			}
    	});
        
        if (t == null) {
        	TextureImpl.bindNone();
        } else {
        	t.bind();
        }
    }
    
    
    private static interface PointCallback {
    	
    	float[] preRenderPoint(Shape shape, float x, float y);
    }
}
