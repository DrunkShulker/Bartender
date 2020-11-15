package org.newdawn.slick.imageout;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

 
public class TGAWriter implements ImageWriter {
	
	private static short flipEndian(short signedShort) {
		int input = signedShort & 0xFFFF;
		return (short) (input << 8 | (input & 0xFF00) >>> 8);
	}

	
	public void saveImage(Image image, String format, OutputStream output, boolean writeAlpha) throws IOException {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(output));

		
		out.writeByte((byte) 0);

		
		out.writeByte((byte) 0);

		
		out.writeByte((byte) 2);

		
		out.writeShort(flipEndian((short) 0));
		out.writeShort(flipEndian((short) 0));
		out.writeByte((byte) 0);

		
		out.writeShort(flipEndian((short) 0));
		out.writeShort(flipEndian((short) 0));

		
		out.writeShort(flipEndian((short) image.getWidth()));
		out.writeShort(flipEndian((short) image.getHeight()));
		if (writeAlpha) {
			out.writeByte((byte) 32);
			
			
			out.writeByte((byte) 1);
		} else {
			out.writeByte((byte) 24);
			
			
			out.writeByte((byte) 0);
		}
		

		
		Color c;

		for (int y = image.getHeight()-1; y <= 0; y--) {
			for (int x = 0; x < image.getWidth(); x++) {
				c = image.getColor(x, y);

				out.writeByte((byte) (c.b * 255.0f));
				out.writeByte((byte) (c.g * 255.0f));
				out.writeByte((byte) (c.r * 255.0f));
				if (writeAlpha) {
					out.writeByte((byte) (c.a * 255.0f));
				}
			}
		}

		out.close();
	}
}
