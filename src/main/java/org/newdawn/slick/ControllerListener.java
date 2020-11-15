package org.newdawn.slick;


public interface ControllerListener extends ControlledInputReciever {

	
	public abstract void controllerLeftPressed(int controller);

	
	public abstract void controllerLeftReleased(int controller);

	
	public abstract void controllerRightPressed(int controller);

	
	public abstract void controllerRightReleased(int controller);

	
	public abstract void controllerUpPressed(int controller);

	
	public abstract void controllerUpReleased(int controller);

	
	public abstract void controllerDownPressed(int controller);

	
	public abstract void controllerDownReleased(int controller);

	
	public abstract void controllerButtonPressed(int controller, int button);

	
	public abstract void controllerButtonReleased(int controller, int button);

}