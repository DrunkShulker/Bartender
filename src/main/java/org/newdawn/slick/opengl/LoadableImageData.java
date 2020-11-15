package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public interface LoadableImageData extends ImageData {
	
	public void configureEdging(boolean edging);
	
	
	public ByteBuffer loadImage(InputStream fis) throws IOException;

	
	public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent)
			throws IOException;
	
	
	public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent)
			throws IOException;
}
