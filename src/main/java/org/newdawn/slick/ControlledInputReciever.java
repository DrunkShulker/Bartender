package org.newdawn.slick;


public interface ControlledInputReciever {

	
	public abstract void setInput(Input input);

	
	public abstract boolean isAcceptingInput();

	
	public abstract void inputEnded();
	
	
	public abstract void inputStarted();

}