package org.newdawn.slick.muffin;

import java.io.IOException;
import java.util.HashMap;


public interface Muffin {
	
	public abstract void saveFile(HashMap data, String fileName) throws IOException;
	
	
	public abstract HashMap loadFile(String fileName) throws IOException;
}
