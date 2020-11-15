package org.newdawn.slick;



public abstract class BasicGame implements Game, InputListener {
	
	private static final int MAX_CONTROLLERS = 20;
	
	private static final int MAX_CONTROLLER_BUTTONS = 100;
	
	private String title;
	
	protected boolean[] controllerLeft = new boolean[MAX_CONTROLLERS];
	
	protected boolean[] controllerRight = new boolean[MAX_CONTROLLERS];
	
	protected boolean[] controllerUp = new boolean[MAX_CONTROLLERS];
	
	protected boolean[] controllerDown = new boolean[MAX_CONTROLLERS];
	
	protected boolean[][] controllerButton = new boolean[MAX_CONTROLLERS][MAX_CONTROLLER_BUTTONS];
	
	
	public BasicGame(String title) {
		this.title = title;
	}

	
	public void setInput(Input input) {	
	}
	
	
	public boolean closeRequested() {
		return true;
	}

	
	public String getTitle() {
		return title;
	}

	
	public abstract void init(GameContainer container) throws SlickException;

	
	public void keyPressed(int key, char c) {
	}

	
	public void keyReleased(int key, char c) {
	}

	
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
	}

	
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
	}
	
	
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}
	
	
	public void mousePressed(int button, int x, int y) {

	}
	
	
	public void controllerButtonPressed(int controller, int button) {
		controllerButton[controller][button] = true;
	}

	
	public void controllerButtonReleased(int controller, int button) {
		controllerButton[controller][button] = false;
	}

	
	public void controllerDownPressed(int controller) {
		controllerDown[controller] = true;
	}

	
	public void controllerDownReleased(int controller) {
		controllerDown[controller] = false;
	}

	
	public void controllerLeftPressed(int controller) {
		controllerLeft[controller] = true;
	}

	
	public void controllerLeftReleased(int controller) {
		controllerLeft[controller] = false;
	}

	
	public void controllerRightPressed(int controller) {
		controllerRight[controller] = true;
	}

	
	public void controllerRightReleased(int controller) {
		controllerRight[controller] = false;
	}

	
	public void controllerUpPressed(int controller) {
		controllerUp[controller] = true;
	}

	
	public void controllerUpReleased(int controller) {
		controllerUp[controller] = false;
	}
	
	
	public void mouseReleased(int button, int x, int y) {
	}

	
	public abstract void update(GameContainer container, int delta) throws SlickException;

	
	public void mouseWheelMoved(int change) {
	}

	
	public boolean isAcceptingInput() {
		return true;
	}
	
	
	public void inputEnded() {
		
	}
	
	
	public void inputStarted() {
		
	}
}
