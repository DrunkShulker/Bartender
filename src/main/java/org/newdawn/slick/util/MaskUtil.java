package org.newdawn.slick.util;

import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;


public class MaskUtil {
	
	protected static SGL GL = Renderer.get();

	
	public static void defineMask() {
		GL.glDepthMask(true);
		GL.glClearDepth(1);
		GL.glClear(SGL.GL_DEPTH_BUFFER_BIT);
		GL.glDepthFunc(SGL.GL_ALWAYS);
		GL.glEnable(SGL.GL_DEPTH_TEST);
		GL.glDepthMask(true);
		GL.glColorMask(false, false, false, false);
	}
	
	
	public static void finishDefineMask() {
		GL.glDepthMask(false);
		GL.glColorMask(true, true, true, true);
	}
	
	
	public static void drawOnMask() {
		GL.glDepthFunc(SGL.GL_EQUAL);
	}

	
	public static void drawOffMask() {
		GL.glDepthFunc(SGL.GL_NOTEQUAL);
	}
	
	
	public static void resetMask() {
		GL.glDepthMask(true);
		GL.glClearDepth(0);
		GL.glClear(SGL.GL_DEPTH_BUFFER_BIT);
		GL.glDepthMask(false);
		
		GL.glDisable(SGL.GL_DEPTH_TEST);
	}
}
