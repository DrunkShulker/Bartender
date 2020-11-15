package org.newdawn.slick.opengl.renderer;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTSecondaryColor;
import org.lwjgl.opengl.EXTTextureMirrorClamp;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


public interface SGL {
	
	public static final int GL_TEXTURE_2D = GL11.GL_TEXTURE_2D;

	
	public static final int GL_RGBA = GL11.GL_RGBA;
	
	public static final int GL_RGB = GL11.GL_RGB;
	
	public static final int GL_UNSIGNED_BYTE = GL11.GL_UNSIGNED_BYTE;
	
	public static final int GL_LINEAR = GL11.GL_LINEAR;
	
	public static final int GL_NEAREST = GL11.GL_NEAREST;
	
	public static final int GL_TEXTURE_MIN_FILTER = GL11.GL_TEXTURE_MIN_FILTER;
	
	public static final int GL_TEXTURE_MAG_FILTER = GL11.GL_TEXTURE_MAG_FILTER;
	
	public static final int GL_POINT_SMOOTH = GL11.GL_POINT_SMOOTH;
	
	public static final int GL_POLYGON_SMOOTH = GL11.GL_POLYGON_SMOOTH;
	
	public static final int GL_LINE_SMOOTH = GL11.GL_LINE_SMOOTH;
	
	public static final int GL_SCISSOR_TEST = GL11.GL_SCISSOR_TEST;

	
	public static final int GL_MODULATE = GL11.GL_MODULATE;
	
	public static final int GL_TEXTURE_ENV = GL11.GL_TEXTURE_ENV;
	
	public static final int GL_TEXTURE_ENV_MODE = GL11.GL_TEXTURE_ENV_MODE;

	
	public static final int GL_QUADS = GL11.GL_QUADS;
	
	public static final int GL_POINTS = GL11.GL_POINTS;
	
	public static final int GL_LINES = GL11.GL_LINES;
	
	public static final int GL_LINE_STRIP = GL11.GL_LINE_STRIP;
	
	public static final int GL_TRIANGLES = GL11.GL_TRIANGLES;
	
	public static final int GL_TRIANGLE_FAN = GL11.GL_TRIANGLE_FAN;

	
	public static final int GL_SRC_ALPHA = GL11.GL_SRC_ALPHA;
	
	public static final int GL_ONE = GL11.GL_ONE;
	
	public static final int GL_ONE_MINUS_DST_ALPHA = GL11.GL_ONE_MINUS_DST_ALPHA;
	
	public static final int GL_DST_ALPHA = GL11.GL_DST_ALPHA;
	
	public static final int GL_ONE_MINUS_SRC_ALPHA = GL11.GL_ONE_MINUS_SRC_ALPHA;

	
	public static final int GL_COMPILE = GL11.GL_COMPILE;
	
	public static final int GL_MAX_TEXTURE_SIZE = GL11.GL_MAX_TEXTURE_SIZE;
	
	public static final int GL_COLOR_BUFFER_BIT = GL11.GL_COLOR_BUFFER_BIT;
	
	public static final int GL_DEPTH_BUFFER_BIT = GL11.GL_DEPTH_BUFFER_BIT;
	
	public static final int GL_BLEND = GL11.GL_BLEND;
	
	public static final int GL_COLOR_CLEAR_VALUE = GL11.GL_COLOR_CLEAR_VALUE;
	
	public static final int GL_LINE_WIDTH = GL11.GL_LINE_WIDTH;
	
	public static final int GL_CLIP_PLANE0 = GL11.GL_CLIP_PLANE0;
	
	public static final int GL_CLIP_PLANE1 = GL11.GL_CLIP_PLANE1;
	
	public static final int GL_CLIP_PLANE2 = GL11.GL_CLIP_PLANE2;
	
	public static final int GL_CLIP_PLANE3 = GL11.GL_CLIP_PLANE3;
	
	
	public static final int GL_COMPILE_AND_EXECUTE = GL11.GL_COMPILE_AND_EXECUTE;

	
	public static final int GL_RGBA8 = GL11.GL_RGBA;
	
	public static final int GL_RGBA16 = GL11.GL_RGBA16;
	
	public static final int GL_BGRA = GL12.GL_BGRA;
	
	public static final int GL_MIRROR_CLAMP_TO_EDGE_EXT = EXTTextureMirrorClamp.GL_MIRROR_CLAMP_TO_EDGE_EXT;

	
	public static final int GL_TEXTURE_WRAP_S = GL11.GL_TEXTURE_WRAP_S;
	
	public static final int GL_TEXTURE_WRAP_T = GL11.GL_TEXTURE_WRAP_T;

	
	public static final int GL_CLAMP = GL11.GL_CLAMP;

	
	public static final int GL_COLOR_SUM_EXT = EXTSecondaryColor.GL_COLOR_SUM_EXT;

	
	public static final int GL_ALWAYS = GL11.GL_ALWAYS;

	
	public static final int GL_DEPTH_TEST = GL11.GL_DEPTH_TEST;

	
	public static final int GL_NOTEQUAL = GL11.GL_NOTEQUAL;
	
	public static final int GL_EQUAL = GL11.GL_EQUAL;
	
	public static final int GL_SRC_COLOR = GL11.GL_SRC_COLOR;
	
	public static final int GL_ONE_MINUS_SRC_COLOR = GL11.GL_ONE_MINUS_SRC_COLOR;
	
	public static final int GL_MODELVIEW_MATRIX = GL11.GL_MODELVIEW_MATRIX;
	
	
	public void flush();
	
	
	public void initDisplay(int width, int height);

	
	public void enterOrtho(int xsize, int ysize);

	
	public void glClearColor(float red, float green, float blue, float alpha);

	
	public void glClipPlane(int plane, DoubleBuffer buffer);
	
	
	public void glScissor(int x, int y, int width, int height);
	
	
	public void glLineWidth(float width);
	
	
	public void glClear(int value);
	
	
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha);
	
	
	public void glLoadIdentity();
	
	
	public void glGetInteger(int id, IntBuffer ret);
	
	
	public void glGetFloat(int id, FloatBuffer ret);
	
	
	public void glEnable(int item);
	
	
	public void glDisable(int item);
	
	
	public void glBindTexture(int target, int id);
	
	
	public void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels);
	
	
	public void glDeleteTextures(IntBuffer buffer);

	
	public void glColor4f(float r, float g, float b, float a);
	
	
	public void glTexCoord2f(float u, float v);
	
	
	public void glVertex3f(float x, float y, float z);

	
	public void glVertex2f(float x, float y);
	
	
	public void glRotatef(float angle, float x, float y, float z);
	
	
	public void glTranslatef(float x, float y, float z);
	
	
	public void glBegin(int geomType);
	
	
	public void glEnd();
	
	
	public void glTexEnvi(int target, int mode, int value);
	
	
	public void glPointSize(float size);
	
	
	public void glScalef(float x, float y, float z);
	
	
	public void glPushMatrix();
	
	
	public void glPopMatrix();
	
	
	public void glBlendFunc(int src, int dest);
	
	
	public int glGenLists(int count);
	
	
	public void glNewList(int id, int option);
	
	
	public void glEndList();
	
	
	public void glCallList(int id);
	
	
	public void glCopyTexImage2D(int	target, int level, int internalFormat,
			 							int x, int y, int width, int height, int border);
	
	
	public void glReadPixels(int x, int y, int width, int height, int format, int type,
		     						ByteBuffer pixels);
	
	
	public void glTexParameteri(int target, int param, int value);
	
	
	public float[] getCurrentColor();

	
	public void glDeleteLists(int list, int count);
	
	
	public void glDepthMask(boolean mask);
	
	
	public void glClearDepth(float value);
	
	
	public void glDepthFunc(int func);
	
	
	public void setGlobalAlphaScale(float alphaScale);

	
	public void glLoadMatrix(FloatBuffer buffer);

	
	public void glGenTextures(IntBuffer ids);

	
	public void glGetError();

	
	public void glTexImage2D(int target, int i, int dstPixelFormat,
			int get2Fold, int get2Fold2, int j, int srcPixelFormat,
			int glUnsignedByte, ByteBuffer textureBuffer);

	
	public void glTexSubImage2D(int glTexture2d, int i, int pageX, int pageY,
			int width, int height, int glBgra, int glUnsignedByte,
			ByteBuffer scratchByteBuffer);

	
	public boolean canTextureMirrorClamp();

	public boolean canSecondaryColor();

	public void glSecondaryColor3ubEXT(byte b, byte c, byte d);
}
