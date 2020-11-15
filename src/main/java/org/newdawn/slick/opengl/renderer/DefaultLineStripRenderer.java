package org.newdawn.slick.opengl.renderer;


public class DefaultLineStripRenderer implements LineStripRenderer {
	
	private SGL GL = Renderer.get();
	
	
	public void end() {
		GL.glEnd();
	}

	
	public void setAntiAlias(boolean antialias) {
		if (antialias) {
			GL.glEnable(SGL.GL_LINE_SMOOTH);
		} else {
			GL.glDisable(SGL.GL_LINE_SMOOTH);
		}
	}

	
	public void setWidth(float width) {
		GL.glLineWidth(width);
	}

	
	public void start() {
		GL.glBegin(SGL.GL_LINE_STRIP);
	}

	
	public void vertex(float x, float y) {
		GL.glVertex2f(x,y);
	}

	
	public void color(float r, float g, float b, float a) {
		GL.glColor4f(r, g, b, a);
	}

	
	public void setLineCaps(boolean caps) {
	}

	
	public boolean applyGLLineFixes() {
		return true;
	}

}
