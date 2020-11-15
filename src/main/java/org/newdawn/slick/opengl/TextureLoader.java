package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.GL11;


public class TextureLoader {
	
	public static Texture getTexture(String format, InputStream in) throws IOException {
		return getTexture(format, in, false, GL11.GL_LINEAR);
	}

	
	public static Texture getTexture(String format, InputStream in, boolean flipped)  throws IOException {
		return getTexture(format, in, flipped, GL11.GL_LINEAR);
	}

	
	public static Texture getTexture(String format, InputStream in, int filter) throws IOException {
		return getTexture(format, in, false, filter);
	}

	
	public static Texture getTexture(String format, InputStream in, boolean flipped, int filter) throws IOException {
		return InternalTextureLoader.get().getTexture(in, in.toString()+"."+format, flipped, filter);
	}
}
