package org.newdawn.slick.util;


public interface LogSystem {

	
	public void error(String message, Throwable e);

	
	public void error(Throwable e);

	
	public void error(String message);
	
	
	public void warn(String message);

	
	public void warn(String message, Throwable e);
	
	
	public void info(String message);

	
	public void debug(String message);
}
