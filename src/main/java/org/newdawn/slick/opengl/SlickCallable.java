package org.newdawn.slick.opengl;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;


public abstract class SlickCallable {
	
	private static Texture lastUsed;
	
	private static boolean inSafe = false;
	
	
	
	public static void enterSafeBlock() 
	{
		if (inSafe) {
			return;
		}
		
		Renderer.get().flush();
		lastUsed = TextureImpl.getLastBind();
		TextureImpl.bindNone();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		inSafe = true;
	}

	
	public static void leaveSafeBlock() 
	{
		if (!inSafe) {
			return;
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glPopClientAttrib();
		GL11.glPopAttrib();
		
		if (lastUsed != null) {
			lastUsed.bind();
		} else {
			TextureImpl.bindNone();
		}
		
		inSafe = false;
	}
	
	
	public final void call() throws SlickException {
		enterSafeBlock();
		
		performGLOperations();
		
		leaveSafeBlock();
	}
	
	
	protected abstract void performGLOperations() throws SlickException;
}
