package org.newdawn.slick.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;



public class BufferedImageUtil {

	
	public static Texture getTexture(String resourceName,
			BufferedImage resourceImage) throws IOException {
		Texture tex = getTexture(resourceName, resourceImage,
				SGL.GL_TEXTURE_2D, 
				SGL.GL_RGBA8, 
				SGL.GL_LINEAR, 
				SGL.GL_LINEAR);

		return tex;
	}

	
	public static Texture getTexture(String resourceName,
			BufferedImage resourceImage, int filter) throws IOException {
		Texture tex = getTexture(resourceName, resourceImage,
				SGL.GL_TEXTURE_2D, 
				SGL.GL_RGBA8, 
				filter, 
				filter);

		return tex;
	}
	
	
	public static Texture getTexture(String resourceName,
			BufferedImage resourceimage, int target, int dstPixelFormat,
			int minFilter, int magFilter) throws IOException {
		ImageIOImageData data = new ImageIOImageData();int srcPixelFormat = 0;

		
		int textureID = InternalTextureLoader.createTextureID();
		TextureImpl texture = new TextureImpl(resourceName, target, textureID);

		
		Renderer.get().glEnable(SGL.GL_TEXTURE_2D);

		
		Renderer.get().glBindTexture(target, textureID);

		BufferedImage bufferedImage = resourceimage;
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = SGL.GL_RGBA;
		} else {
			srcPixelFormat = SGL.GL_RGB;
		}

		
		ByteBuffer textureBuffer = data.imageToByteBuffer(bufferedImage, false, false, null);
		texture.setTextureHeight(data.getTexHeight());
		texture.setTextureWidth(data.getTexWidth());
		texture.setAlpha(data.getDepth() == 32);
		
		if (target == SGL.GL_TEXTURE_2D) {
			Renderer.get().glTexParameteri(target, SGL.GL_TEXTURE_MIN_FILTER, minFilter);
			Renderer.get().glTexParameteri(target, SGL.GL_TEXTURE_MAG_FILTER, magFilter);
			
	        if (Renderer.get().canTextureMirrorClamp()) {
	        	Renderer.get().glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_S, SGL.GL_MIRROR_CLAMP_TO_EDGE_EXT);
	        	Renderer.get().glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_T, SGL.GL_MIRROR_CLAMP_TO_EDGE_EXT);
	        } else {
	        	Renderer.get().glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_S, SGL.GL_CLAMP);
	        	Renderer.get().glTexParameteri(SGL.GL_TEXTURE_2D, SGL.GL_TEXTURE_WRAP_T, SGL.GL_CLAMP);
	        }
		}

		Renderer.get().glTexImage2D(target, 
                      0, 
                      dstPixelFormat, 
                      texture.getTextureWidth(), 
                      texture.getTextureHeight(), 
                      0, 
                      srcPixelFormat, 
                      SGL.GL_UNSIGNED_BYTE, 
                      textureBuffer); 

		return texture;
	}
	
	
	private static void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.drawImage(image.getSubimage(x, y, width, height),x+dx,y+dy,null);
	}
}
