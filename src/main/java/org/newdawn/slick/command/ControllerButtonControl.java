package org.newdawn.slick.command;


public class ControllerButtonControl extends ControllerControl {

	
	public ControllerButtonControl(int controllerIndex, int button) {
		super(controllerIndex, BUTTON_EVENT, button);
	}
}
