package org.newdawn.slick;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.util.Log;


public class Input {
	
	public static final int ANY_CONTROLLER = -1;
	
	
	private static final int MAX_BUTTONS = 100;
	
	
	public static final int KEY_ESCAPE          = 0x01;
	
	public static final int KEY_1               = 0x02;
	
	public static final int KEY_2               = 0x03;
	
	public static final int KEY_3               = 0x04;
	
	public static final int KEY_4               = 0x05;
	
	public static final int KEY_5               = 0x06;
	
	public static final int KEY_6               = 0x07;
	
	public static final int KEY_7               = 0x08;
	
	public static final int KEY_8               = 0x09;
	
	public static final int KEY_9               = 0x0A;
	
	public static final int KEY_0               = 0x0B;
	
	public static final int KEY_MINUS           = 0x0C; 
	
	public static final int KEY_EQUALS          = 0x0D;
	
	public static final int KEY_BACK            = 0x0E; 
	
	public static final int KEY_TAB             = 0x0F;
	
	public static final int KEY_Q               = 0x10;
	
	public static final int KEY_W               = 0x11;
	
	public static final int KEY_E               = 0x12;
	
	public static final int KEY_R               = 0x13;
	
	public static final int KEY_T               = 0x14;
	
	public static final int KEY_Y               = 0x15;
	
	public static final int KEY_U               = 0x16;
	
	public static final int KEY_I               = 0x17;
	
	public static final int KEY_O               = 0x18;
	
	public static final int KEY_P               = 0x19;
	
	public static final int KEY_LBRACKET        = 0x1A;
	
	public static final int KEY_RBRACKET        = 0x1B;
	
	public static final int KEY_RETURN          = 0x1C; 
	
	public static final int KEY_ENTER           = 0x1C; 
	
	public static final int KEY_LCONTROL        = 0x1D;
	
	public static final int KEY_A               = 0x1E;
	
	public static final int KEY_S               = 0x1F;
	
	public static final int KEY_D               = 0x20;
	
	public static final int KEY_F               = 0x21;
	
	public static final int KEY_G               = 0x22;
	
	public static final int KEY_H               = 0x23;
	
	public static final int KEY_J               = 0x24;
	
	public static final int KEY_K               = 0x25;
	
	public static final int KEY_L               = 0x26;
	
	public static final int KEY_SEMICOLON       = 0x27;
	
	public static final int KEY_APOSTROPHE      = 0x28;
	
	public static final int KEY_GRAVE           = 0x29; 
	
	public static final int KEY_LSHIFT          = 0x2A;
	
	public static final int KEY_BACKSLASH       = 0x2B;
	
	public static final int KEY_Z               = 0x2C;
	
	public static final int KEY_X               = 0x2D;
	
	public static final int KEY_C               = 0x2E;
	
	public static final int KEY_V               = 0x2F;
	
	public static final int KEY_B               = 0x30;
	
	public static final int KEY_N               = 0x31;
	
	public static final int KEY_M               = 0x32;
	
	public static final int KEY_COMMA           = 0x33;
	
	public static final int KEY_PERIOD          = 0x34; 
	
	public static final int KEY_SLASH           = 0x35; 
	
	public static final int KEY_RSHIFT          = 0x36;
	
	public static final int KEY_MULTIPLY        = 0x37; 
	
	public static final int KEY_LMENU           = 0x38; 
	
	public static final int KEY_SPACE           = 0x39;
	
	public static final int KEY_CAPITAL         = 0x3A;
	
	public static final int KEY_F1              = 0x3B;
	
	public static final int KEY_F2              = 0x3C;
	
	public static final int KEY_F3              = 0x3D;
	
	public static final int KEY_F4              = 0x3E;
	
	public static final int KEY_F5              = 0x3F;
	
	public static final int KEY_F6              = 0x40;
	
	public static final int KEY_F7              = 0x41;
	
	public static final int KEY_F8              = 0x42;
	
	public static final int KEY_F9              = 0x43;
	
	public static final int KEY_F10             = 0x44;
	
	public static final int KEY_NUMLOCK         = 0x45;
	
	public static final int KEY_SCROLL          = 0x46; 
	
	public static final int KEY_NUMPAD7         = 0x47;
	
	public static final int KEY_NUMPAD8         = 0x48;
	
	public static final int KEY_NUMPAD9         = 0x49;
	
	public static final int KEY_SUBTRACT        = 0x4A; 
	
	public static final int KEY_NUMPAD4         = 0x4B;
	
	public static final int KEY_NUMPAD5         = 0x4C;
	
	public static final int KEY_NUMPAD6         = 0x4D;
	
	public static final int KEY_ADD             = 0x4E; 
	
	public static final int KEY_NUMPAD1         = 0x4F;
	
	public static final int KEY_NUMPAD2         = 0x50;
	
	public static final int KEY_NUMPAD3         = 0x51;
	
	public static final int KEY_NUMPAD0         = 0x52;
	
	public static final int KEY_DECIMAL         = 0x53; 
	
	public static final int KEY_F11             = 0x57;
	
	public static final int KEY_F12             = 0x58;
	
	public static final int KEY_F13             = 0x64; 
	
	public static final int KEY_F14             = 0x65; 
	
	public static final int KEY_F15             = 0x66; 
	
	public static final int KEY_KANA            = 0x70; 
	
	public static final int KEY_CONVERT         = 0x79; 
	
	public static final int KEY_NOCONVERT       = 0x7B; 
	
	public static final int KEY_YEN             = 0x7D; 
	
	public static final int KEY_NUMPADEQUALS    = 0x8D; 
	
	public static final int KEY_CIRCUMFLEX      = 0x90; 
	
	public static final int KEY_AT              = 0x91; 
	
	public static final int KEY_COLON           = 0x92; 
	
	public static final int KEY_UNDERLINE       = 0x93; 
	
	public static final int KEY_KANJI           = 0x94; 
	
	public static final int KEY_STOP            = 0x95; 
	
	public static final int KEY_AX              = 0x96; 
	
	public static final int KEY_UNLABELED       = 0x97; 
	
	public static final int KEY_NUMPADENTER     = 0x9C; 
	
	public static final int KEY_RCONTROL        = 0x9D;
	
	public static final int KEY_NUMPADCOMMA     = 0xB3; 
	
	public static final int KEY_DIVIDE          = 0xB5; 
	
	public static final int KEY_SYSRQ           = 0xB7;
	
	public static final int KEY_RMENU           = 0xB8; 
	
	public static final int KEY_PAUSE           = 0xC5; 
	
	public static final int KEY_HOME            = 0xC7; 
	
	public static final int KEY_UP              = 0xC8; 
	
	public static final int KEY_PRIOR           = 0xC9; 
	
	public static final int KEY_LEFT            = 0xCB; 
	
	public static final int KEY_RIGHT           = 0xCD; 
	
	public static final int KEY_END             = 0xCF; 
	
	public static final int KEY_DOWN            = 0xD0; 
	
	public static final int KEY_NEXT            = 0xD1; 
	
	public static final int KEY_INSERT          = 0xD2; 
	
	public static final int KEY_DELETE          = 0xD3; 
	
	public static final int KEY_LWIN            = 0xDB; 
	
	public static final int KEY_RWIN            = 0xDC; 
	
	public static final int KEY_APPS            = 0xDD; 
	
	public static final int KEY_POWER           = 0xDE;
	
	public static final int KEY_SLEEP           = 0xDF;
	
	
	public static final int KEY_LALT = KEY_LMENU;
	
	public static final int KEY_RALT = KEY_RMENU;
	
	
	private static final int LEFT = 0;
	
	private static final int RIGHT = 1;
	
	private static final int UP = 2;
	
	private static final int DOWN = 3;
	
	private static final int BUTTON1 = 4;
	
	private static final int BUTTON2 = 5;
	
	private static final int BUTTON3 = 6;
	
	private static final int BUTTON4 = 7;
	
	private static final int BUTTON5 = 8;
	
	private static final int BUTTON6 = 9;
	
	private static final int BUTTON7 = 10;
	
	private static final int BUTTON8 = 11;
	
	private static final int BUTTON9 = 12;
	
	private static final int BUTTON10 = 13;
	
	
	public static final int MOUSE_LEFT_BUTTON = 0;
	
	public static final int MOUSE_RIGHT_BUTTON = 1;
	
	public static final int MOUSE_MIDDLE_BUTTON = 2;
	
	
	private static boolean controllersInited = false;
	
	private static ArrayList controllers = new ArrayList();

	
	private int lastMouseX;
	
	private int lastMouseY;
	
	protected boolean[] mousePressed = new boolean[10];
	
	private boolean[][] controllerPressed = new boolean[100][MAX_BUTTONS];
	
	
	protected char[] keys = new char[1024];
	
	protected boolean[] pressed = new boolean[1024];
	
	protected long[] nextRepeat = new long[1024];
	
	
	private boolean[][] controls = new boolean[10][MAX_BUTTONS+10];
	
	protected boolean consumed = false;
	
	protected HashSet allListeners = new HashSet();
	
	protected ArrayList keyListeners = new ArrayList();
	
	protected ArrayList keyListenersToAdd = new ArrayList();
	
	protected ArrayList mouseListeners = new ArrayList();
	
	protected ArrayList mouseListenersToAdd = new ArrayList();
	
	protected ArrayList controllerListeners = new ArrayList();
	
	private int wheel;
	
	private int height;
	
	
	private boolean displayActive = true;
	
	
	private boolean keyRepeat;
	
	private int keyRepeatInitial;
	
	private int keyRepeatInterval;
	
	
	private boolean paused;
	
	private float scaleX = 1;
	
	private float scaleY = 1;
	
	private float xoffset = 0;
	
	private float yoffset = 0;
	
	
	private int doubleClickDelay = 250;
	
	private long doubleClickTimeout = 0;
	
	
	private int clickX;
	
	private int clickY;
	
	private int clickButton;

	
	private int pressedX = -1;
	
	
	private int pressedY = -1;
	
	
	private int mouseClickTolerance = 5;

	
	public static void disableControllers() {
	   controllersInited = true;
	}
	
	
	public Input(int height) {
		init(height);
	}
	
	
	public void setDoubleClickInterval(int delay) {
		doubleClickDelay = delay;
	}

	
	public void setMouseClickTolerance (int mouseClickTolerance) {
		this.mouseClickTolerance = mouseClickTolerance;
	}

	
	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	
	public void setOffset(float xoffset, float yoffset) {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
	}
	
	
	public void resetInputTransform() {
	    setOffset(0, 0);
	    setScale(1, 1);
	}
	
	
	public void addListener(InputListener listener) {
		addKeyListener(listener);
		addMouseListener(listener);
		addControllerListener(listener);
	}

	
	public void addKeyListener(KeyListener listener) {
		keyListenersToAdd.add(listener);
	}
	
	
	private void addKeyListenerImpl(KeyListener listener) {
		if (keyListeners.contains(listener)) {
			return;
		}
		keyListeners.add(listener);
		allListeners.add(listener);
	}

	
	public void addMouseListener(MouseListener listener) {
		mouseListenersToAdd.add(listener);
	}
	
	
	private void addMouseListenerImpl(MouseListener listener) {
		if (mouseListeners.contains(listener)) {
			return;
		}
		mouseListeners.add(listener);
		allListeners.add(listener);
	}
	
	
	public void addControllerListener(ControllerListener listener) {
		if (controllerListeners.contains(listener)) {
			return;
		}
		controllerListeners.add(listener);
		allListeners.add(listener);
	}
	
	
	public void removeAllListeners() {
		removeAllKeyListeners();
		removeAllMouseListeners();
		removeAllControllerListeners();
	}

	
	public void removeAllKeyListeners() {
		allListeners.removeAll(keyListeners);
		keyListeners.clear();
	}

	
	public void removeAllMouseListeners() {
		allListeners.removeAll(mouseListeners);
		mouseListeners.clear();
	}

	
	public void removeAllControllerListeners() {
		allListeners.removeAll(controllerListeners);
		controllerListeners.clear();
	}
	
	
	public void addPrimaryListener(InputListener listener) {
		removeListener(listener);
		
		keyListeners.add(0, listener);
		mouseListeners.add(0, listener);
		controllerListeners.add(0, listener);
		
		allListeners.add(listener);
	}
	
	
	public void removeListener(InputListener listener) {
		removeKeyListener(listener);
		removeMouseListener(listener);
		removeControllerListener(listener);
	}

	
	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
		
		if (!mouseListeners.contains(listener) && !controllerListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	
	public void removeControllerListener(ControllerListener listener) {
		controllerListeners.remove(listener);
		
		if (!mouseListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}

	
	public void removeMouseListener(MouseListener listener) {
		mouseListeners.remove(listener);
		
		if (!controllerListeners.contains(listener) && !keyListeners.contains(listener)) {
			allListeners.remove(listener);
		}
	}
	
	
	void init(int height) {
		this.height = height;
		lastMouseX = getMouseX();
		lastMouseY = getMouseY();
	}
	
	
	public static String getKeyName(int code) {
		return Keyboard.getKeyName(code);
	}
	
	
	public boolean isKeyPressed(int code) {
		if (pressed[code]) {
			pressed[code] = false;
			return true;
		}
		
		return false;
	}
	
	
	public boolean isMousePressed(int button) {
		if (mousePressed[button]) {
			mousePressed[button] = false;
			return true;
		}
		
		return false;
	}
	
	
	public boolean isControlPressed(int button) {
		return isControlPressed(button, 0);
	}

	
	public boolean isControlPressed(int button, int controller) {
		if (controllerPressed[controller][button]) {
			controllerPressed[controller][button] = false;
			return true;
		}
		
		return false;
	}
	
	
	public void clearControlPressedRecord() {
		for (int i=0;i<controllers.size();i++) {
			Arrays.fill(controllerPressed[i], false);
		}
	}
	
	
	public void clearKeyPressedRecord() {
		Arrays.fill(pressed, false);
	}

	
	public void clearMousePressedRecord() {
		Arrays.fill(mousePressed, false);
	}
	
	
	public boolean isKeyDown(int code) {
		return Keyboard.isKeyDown(code);
	}

	
	public int getAbsoluteMouseX() {
		return Mouse.getX();
	}

	
	public int getAbsoluteMouseY() {
		return height - Mouse.getY();
	}
	   
	
	public int getMouseX() {
		return (int) ((Mouse.getX() * scaleX)+xoffset);
	}
	
	
	public int getMouseY() {
		return (int) (((height-Mouse.getY()) * scaleY)+yoffset);
	}
	
	
	public boolean isMouseButtonDown(int button) {
		return Mouse.isButtonDown(button);
	}
	
	
	private boolean anyMouseDown() {
		for (int i=0;i<3;i++) {
			if (Mouse.isButtonDown(i)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public int getControllerCount() {
		try {
			initControllers();
		} catch (SlickException e) {
			throw new RuntimeException("Failed to initialise controllers");
		}
		
		return controllers.size();
	}
	
	
	public int getAxisCount(int controller) {
		return ((Controller) controllers.get(controller)).getAxisCount();
	}
	
	 
	public float getAxisValue(int controller, int axis) {
		return ((Controller) controllers.get(controller)).getAxisValue(axis);
	}

	 
	public String getAxisName(int controller, int axis) {
		return ((Controller) controllers.get(controller)).getAxisName(axis);
	}
	
	
	public boolean isControllerLeft(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}
		
		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerLeft(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() < -0.5f
				|| ((Controller) controllers.get(controller)).getPovX() < -0.5f;
	}

	
	public boolean isControllerRight(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerRight(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getXAxisValue() > 0.5f
   				|| ((Controller) controllers.get(controller)).getPovX() > 0.5f;
	}

	
	public boolean isControllerUp(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerUp(i)) {
					return true;
				}
			}
			
			return false;
		}
		return ((Controller) controllers.get(controller)).getYAxisValue() < -0.5f
		   		|| ((Controller) controllers.get(controller)).getPovY() < -0.5f;
	}

	
	public boolean isControllerDown(int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isControllerDown(i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).getYAxisValue() > 0.5f
			   || ((Controller) controllers.get(controller)).getPovY() > 0.5f;
	       
	}

	
	public boolean isButtonPressed(int index, int controller) {
		if (controller >= getControllerCount()) {
			return false;
		}

		if (controller == ANY_CONTROLLER) {
			for (int i=0;i<controllers.size();i++) {
				if (isButtonPressed(index, i)) {
					return true;
				}
			}
			
			return false;
		}
		
		return ((Controller) controllers.get(controller)).isButtonPressed(index);
	}
	
	
	public boolean isButton1Pressed(int controller) {
		return isButtonPressed(0, controller);
	}

	
	public boolean isButton2Pressed(int controller) {
		return isButtonPressed(1, controller);
	}

	
	public boolean isButton3Pressed(int controller) {
		return isButtonPressed(2, controller);
	}
	
	
	public void initControllers() throws SlickException {
		if (controllersInited) {
			return;
		}
		
		controllersInited = true;
		try {
			Controllers.create();
			int count = Controllers.getControllerCount();
			
			for (int i = 0; i < count; i++) {
				Controller controller = Controllers.getController(i);

				if ((controller.getButtonCount() >= 3) && (controller.getButtonCount() < MAX_BUTTONS)) {
					controllers.add(controller);
				}
			}
			
			Log.info("Found "+controllers.size()+" controllers");
			for (int i=0;i<controllers.size();i++) {
				Log.info(i+" : "+((Controller) controllers.get(i)).getName());
			}
		} catch (LWJGLException e) {
			if (e.getCause() instanceof ClassNotFoundException) {
				throw new SlickException("Unable to create controller - no jinput found - add jinput.jar to your classpath");
			}
			throw new SlickException("Unable to create controllers");
		} catch (NoClassDefFoundError e) {
			
		}
	}
	
	
	public void consumeEvent() {
		consumed = true;
	}
	
	
	private class NullOutputStream extends OutputStream {
		
		public void write(int b) throws IOException {
			
		}
		
	}
	
	
	private int resolveEventKey(int key, char c) {
		
		
		if ((c == 61) || (key == 0)) {
			return KEY_EQUALS;
		}
		
		return key;
	}
	
	
	public void considerDoubleClick(int button, int x, int y) {
		if (doubleClickTimeout == 0) {
			clickX = x;
			clickY = y;
			clickButton = button;
			doubleClickTimeout = System.currentTimeMillis() + doubleClickDelay;
			fireMouseClicked(button, x, y, 1);
		} else {
			if (clickButton == button) {
				if ((System.currentTimeMillis() < doubleClickTimeout)) {
					fireMouseClicked(button, x, y, 2);
					doubleClickTimeout = 0;
				}
			}
		}
	}
	
	
	public void poll(int width, int height) {
		if (paused) {
			clearControlPressedRecord();
			clearKeyPressedRecord();
			clearMousePressedRecord();
			
			while (Keyboard.next()) {}
			while (Mouse.next()) {}
			return;
		}

		if (!Display.isActive()) {
			clearControlPressedRecord();
			clearKeyPressedRecord();
			clearMousePressedRecord();
		}
		
		
		for (int i=0;i<keyListenersToAdd.size();i++) {
			addKeyListenerImpl((KeyListener) keyListenersToAdd.get(i));
		}
		keyListenersToAdd.clear();
		for (int i=0;i<mouseListenersToAdd.size();i++) {
			addMouseListenerImpl((MouseListener) mouseListenersToAdd.get(i));
		}
		mouseListenersToAdd.clear();
		
		if (doubleClickTimeout != 0) {
			if (System.currentTimeMillis() > doubleClickTimeout) {
				doubleClickTimeout = 0;
			}
		}
		
		this.height = height;

		Iterator allStarts = allListeners.iterator();
		while (allStarts.hasNext()) {
			ControlledInputReciever listener = (ControlledInputReciever) allStarts.next();
			listener.inputStarted();
		}
		
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				
				keys[eventKey] = Keyboard.getEventCharacter();
				pressed[eventKey] = true;
				nextRepeat[eventKey] = System.currentTimeMillis() + keyRepeatInitial;
				
				consumed = false;
				for (int i=0;i<keyListeners.size();i++) {
					KeyListener listener = (KeyListener) keyListeners.get(i);
					
					if (listener.isAcceptingInput()) {
						listener.keyPressed(eventKey, Keyboard.getEventCharacter());
						if (consumed) {
							break;
						}
					}
				}
			} else {
				int eventKey = resolveEventKey(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				nextRepeat[eventKey] = 0;
				
				consumed = false;
				for (int i=0;i<keyListeners.size();i++) {
					KeyListener listener = (KeyListener) keyListeners.get(i);
					if (listener.isAcceptingInput()) {
						listener.keyReleased(eventKey, keys[eventKey]);
						if (consumed) {
							break;
						}
					}
				}
			}
		}
		
		while (Mouse.next()) {
			if (Mouse.getEventButton() >= 0) {
				if (Mouse.getEventButtonState()) {
					consumed = false;
					mousePressed[Mouse.getEventButton()] = true;

					pressedX = (int) (xoffset + (Mouse.getEventX() * scaleX));
					pressedY =  (int) (yoffset + ((height-Mouse.getEventY()) * scaleY));

					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mousePressed(Mouse.getEventButton(), pressedX, pressedY);
							if (consumed) {
								break;
							}
						}
					}
				} else {
					consumed = false;
					mousePressed[Mouse.getEventButton()] = false;
					
					int releasedX = (int) (xoffset + (Mouse.getEventX() * scaleX));
					int releasedY = (int) (yoffset + ((height-Mouse.getEventY()) * scaleY));
					if ((pressedX != -1) && 
					    (pressedY != -1) &&
						(Math.abs(pressedX - releasedX) < mouseClickTolerance) && 
						(Math.abs(pressedY - releasedY) < mouseClickTolerance)) {
						considerDoubleClick(Mouse.getEventButton(), releasedX, releasedY);
						pressedX = pressedY = -1;
					}

					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseReleased(Mouse.getEventButton(), releasedX, releasedY);
							if (consumed) {
								break;
							}
						}
					}
				}
			} else {
				if (Mouse.isGrabbed() && displayActive) {
					if ((Mouse.getEventDX() != 0) || (Mouse.getEventDY() != 0)) {
						consumed = false;
						for (int i=0;i<mouseListeners.size();i++) {
							MouseListener listener = (MouseListener) mouseListeners.get(i);
							if (listener.isAcceptingInput()) {
								if (anyMouseDown()) {
									listener.mouseDragged(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());	
								} else {
									listener.mouseMoved(0, 0, Mouse.getEventDX(), -Mouse.getEventDY());
								}
								
								if (consumed) {
									break;
								}
							}
						}
					}
				}
				
				int dwheel = Mouse.getEventDWheel();
				wheel += dwheel;
				if (dwheel != 0) {
					consumed = false;
					for (int i=0;i<mouseListeners.size();i++) {
						MouseListener listener = (MouseListener) mouseListeners.get(i);
						if (listener.isAcceptingInput()) {
							listener.mouseWheelMoved(dwheel);
							if (consumed) {
								break;
							}
						}
					}
				}
			}
		}
		
		if (!displayActive || Mouse.isGrabbed()) {
			lastMouseX = getMouseX();
			lastMouseY = getMouseY();
		} else {
			if ((lastMouseX != getMouseX()) || (lastMouseY != getMouseY())) {
				consumed = false;
				for (int i=0;i<mouseListeners.size();i++) {
					MouseListener listener = (MouseListener) mouseListeners.get(i);
					if (listener.isAcceptingInput()) {
						if (anyMouseDown()) {
							listener.mouseDragged(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());
						} else {
							listener.mouseMoved(lastMouseX ,  lastMouseY, getMouseX(), getMouseY());	
						}
						if (consumed) {
							break;
						}
					}
				}
				lastMouseX = getMouseX();
				lastMouseY = getMouseY();
			}
		}
		
		if (controllersInited) {
			for (int i=0;i<getControllerCount();i++) {
				int count = ((Controller) controllers.get(i)).getButtonCount()+3;
				count = Math.min(count, 24);
				for (int c=0;c<=count;c++) {
					if (controls[i][c] && !isControlDwn(c, i)) {
						controls[i][c] = false;
						fireControlRelease(c, i);
					} else if (!controls[i][c] && isControlDwn(c, i)) {
						controllerPressed[i][c] = true;
						controls[i][c] = true;
						fireControlPress(c, i);
					}
				}
			}
		}
		
		if (keyRepeat) {
			for (int i=0;i<1024;i++) {
				if (pressed[i] && (nextRepeat[i] != 0)) {
					if (System.currentTimeMillis() > nextRepeat[i]) {
						nextRepeat[i] = System.currentTimeMillis() + keyRepeatInterval;
						consumed = false;
						for (int j=0;j<keyListeners.size();j++) {
							KeyListener listener = (KeyListener) keyListeners.get(j);

							if (listener.isAcceptingInput()) {
								listener.keyPressed(i, keys[i]);
								if (consumed) {
									break;
								}
							}
						}
					}
				}
			}
		}

		
		Iterator all = allListeners.iterator();
		while (all.hasNext()) {
			ControlledInputReciever listener = (ControlledInputReciever) all.next();
			listener.inputEnded();
		}
		
		if (Display.isCreated()) {
			displayActive = Display.isActive();
		}
	}
	
	
	public void enableKeyRepeat(int initial, int interval) {
		Keyboard.enableRepeatEvents(true);
	}

	
	public void enableKeyRepeat() {
		Keyboard.enableRepeatEvents(true);
	}
	
	
	public void disableKeyRepeat() {
		Keyboard.enableRepeatEvents(false);
	}
	
	
	public boolean isKeyRepeatEnabled() {
		return Keyboard.areRepeatEventsEnabled();
	}
	
	
	private void fireControlPress(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<controllerListeners.size();i++) {
			ControllerListener listener = (ControllerListener) controllerListeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftPressed(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightPressed(controllerIndex);
					break;
				case UP:
					listener.controllerUpPressed(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownPressed(controllerIndex);
					break;
				default:
					
					listener.controllerButtonPressed(controllerIndex, (index - BUTTON1) + 1);
					break;
				}
				if (consumed) {
					break;
				}
			}
		}
	}

	
	private void fireControlRelease(int index, int controllerIndex) {
		consumed = false;
		for (int i=0;i<controllerListeners.size();i++) {
			ControllerListener listener = (ControllerListener) controllerListeners.get(i);
			if (listener.isAcceptingInput()) {
				switch (index) {
				case LEFT:
					listener.controllerLeftReleased(controllerIndex);
					break;
				case RIGHT:
					listener.controllerRightReleased(controllerIndex);
					break;
				case UP:
					listener.controllerUpReleased(controllerIndex);
					break;
				case DOWN:
					listener.controllerDownReleased(controllerIndex);
					break;
				default:
					
					listener.controllerButtonReleased(controllerIndex, (index - BUTTON1) + 1);
					break;
				}
				if (consumed) {
					break;
				}
			}
		}
	}
	
	
	private boolean isControlDwn(int index, int controllerIndex) {
		switch (index) {
		case LEFT:
			return isControllerLeft(controllerIndex);
		case RIGHT:
			return isControllerRight(controllerIndex);
		case UP:
			return isControllerUp(controllerIndex);
		case DOWN:
			return isControllerDown(controllerIndex);
		}
		
		if (index >= BUTTON1) {
			return isButtonPressed((index-BUTTON1), controllerIndex);
		}

		throw new RuntimeException("Unknown control index");
	}
	

	
	public void pause() {
		paused = true;

		
		clearKeyPressedRecord();
		clearMousePressedRecord();
		clearControlPressedRecord();
	}

	
	public void resume() {
		paused = false;
	}
	
	
	private void fireMouseClicked(int button, int x, int y, int clickCount) {
		consumed = false;
		for (int i=0;i<mouseListeners.size();i++) {
			MouseListener listener = (MouseListener) mouseListeners.get(i);
			if (listener.isAcceptingInput()) {
				listener.mouseClicked(button, x, y, clickCount);
				if (consumed) {
					break;
				}
			}
		}
	}
}
