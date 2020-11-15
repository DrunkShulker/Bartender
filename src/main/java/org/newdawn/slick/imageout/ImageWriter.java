package org.newdawn.slick.imageout;

import java.io.IOException;
import java.io.OutputStream;

import org.newdawn.slick.Image;


public interface ImageWriter {
	
	void saveImage(Image image, String format, OutputStream out, boolean writeAlpha) throws IOException; 
}
