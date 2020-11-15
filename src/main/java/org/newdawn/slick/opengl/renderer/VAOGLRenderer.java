package org.newdawn.slick.opengl.renderer;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;


public class VAOGLRenderer extends ImmediateModeOGLRenderer {
	
	private static final int TOLERANCE = 20;
	
	public static final int NONE = -1;
	
	public static final int MAX_VERTS = 5000;
	
	
	private int currentType = NONE;
	
	private float[] color = new float[] {1f,1f,1f,1f};
	
	private float[] tex = new float[] {0f,0f};
	
	private int vertIndex;
	
	
	private float[] verts = new float[MAX_VERTS*3];
	
	private float[] cols = new float[MAX_VERTS*4];
	
	private float[] texs = new float[MAX_VERTS*3];
	
	
	private FloatBuffer vertices = BufferUtils.createFloatBuffer(MAX_VERTS * 3);
	
	private FloatBuffer colors = BufferUtils.createFloatBuffer(MAX_VERTS * 4);
	
	private FloatBuffer textures = BufferUtils.createFloatBuffer(MAX_VERTS * 2);
	
	
	private int listMode = 0;
	
	
	public void initDisplay(int width, int height) {
		super.initDisplay(width, height);
		
		startBuffer();
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
	}

	
	private void startBuffer() {
		vertIndex = 0;
	}
	
	
	private void flushBuffer() {	
		if (vertIndex == 0) {
			return;
		}
		if (currentType == NONE) {
			return;
		}
		
		if (vertIndex < TOLERANCE) {
			GL11.glBegin(currentType);
			for (int i=0;i<vertIndex;i++) {
				GL11.glColor4f(cols[(i*4)+0], cols[(i*4)+1], cols[(i*4)+2], cols[(i*4)+3]);
				GL11.glTexCoord2f(texs[(i*2)+0], texs[(i*2)+1]);
				GL11.glVertex3f(verts[(i*3)+0], verts[(i*3)+1], verts[(i*3)+2]);
			}
			GL11.glEnd();
			currentType = NONE;
			return;
		}
		vertices.clear();
		colors.clear();
		textures.clear();
		
		vertices.put(verts,0,vertIndex*3);
		colors.put(cols,0,vertIndex*4);
		textures.put(texs,0,vertIndex*2);
		
		vertices.flip(); 
		colors.flip(); 
		textures.flip(); 
		
		GL11.glVertexPointer(3,0,vertices);     
		GL11.glColorPointer(4,0,colors);     
		GL11.glTexCoordPointer(2,0,textures);     
		
		GL11.glDrawArrays(currentType, 0, vertIndex);
		currentType = NONE;
	}
	
	
	private void applyBuffer() {
		if (listMode > 0) {
			return;
		}
		
		if (vertIndex != 0) {
			flushBuffer();
			startBuffer();
		}
		
		super.glColor4f(color[0], color[1], color[2], color[3]);
	}
	
	
	public void flush() {
		super.flush();
		
		applyBuffer();
	}
	
	
	public void glBegin(int geomType) {
		if (listMode > 0) {
			super.glBegin(geomType);
			return;
		}
		
		if (currentType != geomType) {
			applyBuffer();
			currentType = geomType;
		}
	}

	
	public void glColor4f(float r, float g, float b, float a) {
		a *= alphaScale;
		
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
		
		if (listMode > 0) {
			super.glColor4f(r,g,b,a);
			return;
		}
	}

	
	public void glEnd() {
		if (listMode > 0) {
			super.glEnd();
			return;
		}
	}

	
	public void glTexCoord2f(float u, float v) {
		if (listMode > 0) {
			super.glTexCoord2f(u,v);
			return;
		}
		
		tex[0] = u;
		tex[1] = v;
	}

	
	public void glVertex2f(float x, float y) {
		if (listMode > 0) {
			super.glVertex2f(x,y);
			return;
		}
		
		glVertex3f(x,y,0);
	}

	
	public void glVertex3f(float x, float y, float z) {
		if (listMode > 0) {
			super.glVertex3f(x,y,z);
			return;
		}
		
		verts[(vertIndex*3)+0] = x;
		verts[(vertIndex*3)+1] = y;
		verts[(vertIndex*3)+2] = z;
		cols[(vertIndex*4)+0] = color[0];
		cols[(vertIndex*4)+1] = color[1];
		cols[(vertIndex*4)+2] = color[2];
		cols[(vertIndex*4)+3] = color[3];
		texs[(vertIndex*2)+0] = tex[0];
		texs[(vertIndex*2)+1] = tex[1];
		vertIndex++;
		
		if (vertIndex > MAX_VERTS - 50) {
			if (isSplittable(vertIndex, currentType)) {
				int type = currentType;
				applyBuffer();
				currentType = type;
			}
		}
	}

	
	private boolean isSplittable(int count, int type) {
		switch (type) {
		case GL11.GL_QUADS:
			return count % 4 == 0;
		case GL11.GL_TRIANGLES:
			return count % 3 == 0;
		case GL11.GL_LINE:
			return count % 2 == 0;
		}
		
		return false;
	}
	
	
	public void glBindTexture(int target, int id) {
		applyBuffer();
		super.glBindTexture(target, id);
	}

	
	public void glBlendFunc(int src, int dest) {
		applyBuffer();
		super.glBlendFunc(src, dest);
	}

	
	public void glCallList(int id) {
		applyBuffer();
		super.glCallList(id);
	}

	
	public void glClear(int value) {
		applyBuffer();
		super.glClear(value);
	}

	
	public void glClipPlane(int plane, DoubleBuffer buffer) {
		applyBuffer();
		super.glClipPlane(plane, buffer);
	}

	
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		applyBuffer();
		super.glColorMask(red, green, blue, alpha);
	}

	
	public void glDisable(int item) {
		applyBuffer();
		super.glDisable(item);
	}

	
	public void glEnable(int item) {
		applyBuffer();
		super.glEnable(item);
	}

	
	public void glLineWidth(float width) {
		applyBuffer();
		super.glLineWidth(width);
	}

	
	public void glPointSize(float size) {
		applyBuffer();
		super.glPointSize(size);
	}

	
	public void glPopMatrix() {
		applyBuffer();
		super.glPopMatrix();
	}

	
	public void glPushMatrix() {
		applyBuffer();
		super.glPushMatrix();
	}

	
	public void glRotatef(float angle, float x, float y, float z) {
		applyBuffer();
		super.glRotatef(angle, x, y, z);
	}

	
	public void glScalef(float x, float y, float z) {
		applyBuffer();
		super.glScalef(x, y, z);
	}

	
	public void glScissor(int x, int y, int width, int height) {
		applyBuffer();
		super.glScissor(x, y, width, height);
	}

	
	public void glTexEnvi(int target, int mode, int value) {
		applyBuffer();
		super.glTexEnvi(target, mode, value);
	}

	
	public void glTranslatef(float x, float y, float z) {
		applyBuffer();
		super.glTranslatef(x, y, z);
	}

	
	public void glEndList() {
		listMode--;
		super.glEndList();
	}

	
	public void glNewList(int id, int option) {
		listMode++;
		super.glNewList(id, option);
	}

	
	public float[] getCurrentColor() {
		return color;
	}
	
	
	public void glLoadMatrix(FloatBuffer buffer) {
		flushBuffer();
		super.glLoadMatrix(buffer);
	}
}
