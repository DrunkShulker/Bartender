package org.newdawn.slick.openal;

import java.io.IOException;


interface AudioInputStream {
	
	public int getChannels();
	
	
	public int getRate();

	
	public int read() throws IOException;

	
	public int read(byte[] data) throws IOException;

	
	public int read(byte[] data, int ofs, int len) throws IOException;

	
	public boolean atEnd();
	
	
	public void close() throws IOException;
}
