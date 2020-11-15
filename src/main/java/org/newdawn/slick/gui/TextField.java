package org.newdawn.slick.gui;

import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;


public class TextField extends AbstractComponent {
	
	private static final int INITIAL_KEY_REPEAT_INTERVAL = 400;
	
	private static final int KEY_REPEAT_INTERVAL = 50;
	
	
	private int width;

	
	private int height;
	
	
	protected int x;

	
	protected int y;

	
	private int maxCharacter = 10000;

	
	private String value = "";

	
	private Font font;

	
	private Color border = Color.white;

	
	private Color text = Color.white;

	
	private Color background = new Color(0, 0, 0, 0.5f);

	
	private int cursorPos;

	
	private boolean visibleCursor = true;

	
	private int lastKey = -1;
	
	
	private char lastChar = 0;
	
	
	private long repeatTimer;
	
	
	private String oldText;
	
	
	private int oldCursorPos;
	
	
	private boolean consume = true;
	
	
	public TextField(GUIContext container, Font font, int x, int y, int width,
					 int height, ComponentListener listener) {
		this(container,font,x,y,width,height);
		addListener(listener);
	}
	
	
	public TextField(GUIContext container, Font font, int x, int y, int width,
			int height) {
		super(container);

		this.font = font;

		setLocation(x, y);
		this.width = width;
		this.height = height;
	}

	
	public void setConsumeEvents(boolean consume) {
		this.consume = consume;
	}
	
	
	public void deactivate() {
		setFocus(false);
	}
	
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
	public int getX() {
		return x;
	}

	
	public int getY() {
		return y;
	}
	
	
	public int getWidth() {
		return width;
	}

	
	public int getHeight() {
		return height;
	}

	
	public void setBackgroundColor(Color color) {
		background = color;
	}

	
	public void setBorderColor(Color color) {
		border = color;
	}

	
	public void setTextColor(Color color) {
		text = color;
	}

	
	public void render(GUIContext container, Graphics g) {
		if (lastKey != -1) {
			if (input.isKeyDown(lastKey)) {
				if (repeatTimer < System.currentTimeMillis()) {
					repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
					keyPressed(lastKey, lastChar);
				}
			} else {
				lastKey = -1;
			}
		}
		Rectangle oldClip = g.getClip();
		g.setWorldClip(x,y,width, height);
		
		
		Color clr = g.getColor();

		if (background != null) {
			g.setColor(background.multiply(clr));
			g.fillRect(x, y, width, height);
		}
		g.setColor(text.multiply(clr));
		Font temp = g.getFont();

		int cpos = font.getWidth(value.substring(0, cursorPos));
		int tx = 0;
		if (cpos > width) {
			tx = width - cpos - font.getWidth("_");
		}

		g.translate(tx + 2, 0);
		g.setFont(font);
		g.drawString(value, x + 1, y + 1);

		if (hasFocus() && visibleCursor) {
			g.drawString("_", x + 1 + cpos + 2, y + 1);
		}

		g.translate(-tx - 2, 0);

		if (border != null) {
			g.setColor(border.multiply(clr));
			g.drawRect(x, y, width, height);
		}
		g.setColor(clr);
		g.setFont(temp);
		g.clearWorldClip();
		g.setClip(oldClip);
	}

	
	public String getText() {
		return value;
	}

	
	public void setText(String value) {
		this.value = value;
		if (cursorPos > value.length()) {
			cursorPos = value.length();
		}
	}

	
	public void setCursorPos(int pos) {
		cursorPos = pos;
		if (cursorPos > value.length()) {
			cursorPos = value.length();
		}
	}

	
	public void setCursorVisible(boolean visibleCursor) {
		this.visibleCursor = visibleCursor;
	}

	
	public void setMaxLength(int length) {
		maxCharacter = length;
		if (value.length() > maxCharacter) {
			value = value.substring(0, maxCharacter);
		}
	}

	
	protected void doPaste(String text) {
		recordOldPosition();
		
		for (int i=0;i<text.length();i++) {
			keyPressed(-1, text.charAt(i));
		}
	}
	
	
	protected void recordOldPosition() {
		oldText = getText();
		oldCursorPos = cursorPos;
	}
	
	
	protected void doUndo(int oldCursorPos, String oldText) {
		if (oldText != null) {
			setText(oldText);
			setCursorPos(oldCursorPos);
		}
	}
	
	
	public void keyPressed(int key, char c) {
		if (hasFocus()) {
			if (key != -1)
			{
				if ((key == Input.KEY_V) && 
				   ((input.isKeyDown(Input.KEY_LCONTROL)) || (input.isKeyDown(Input.KEY_RCONTROL)))) {
					String text = Sys.getClipboard();
					if (text != null) {
						doPaste(text);
					}
					return;
				}
				if ((key == Input.KEY_Z) && 
				   ((input.isKeyDown(Input.KEY_LCONTROL)) || (input.isKeyDown(Input.KEY_RCONTROL)))) {
					if (oldText != null) {
						doUndo(oldCursorPos, oldText);
					}
					return;
				}
				
				
				if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL)) {
					return;
				}
				if (input.isKeyDown(Input.KEY_LALT) || input.isKeyDown(Input.KEY_RALT)) {
					return;
				}
			}
			
			if (lastKey != key) {
				lastKey = key;
				repeatTimer = System.currentTimeMillis() + INITIAL_KEY_REPEAT_INTERVAL;
			} else {
				repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
			}
			lastChar = c;
			
			if (key == Input.KEY_LEFT) {
				if (cursorPos > 0) {
					cursorPos--;
				}
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			} else if (key == Input.KEY_RIGHT) {
				if (cursorPos < value.length()) {
					cursorPos++;
				}
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			} else if (key == Input.KEY_BACK) {
				if ((cursorPos > 0) && (value.length() > 0)) {
					if (cursorPos < value.length()) {
						value = value.substring(0, cursorPos - 1)
								+ value.substring(cursorPos);
					} else {
						value = value.substring(0, cursorPos - 1);
					}
					cursorPos--;
				}
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			} else if (key == Input.KEY_DELETE) {
				if (value.length() > cursorPos) {
					value = value.substring(0,cursorPos) + value.substring(cursorPos+1);
				}
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			} else if ((c < 127) && (c > 31) && (value.length() < maxCharacter)) {
				if (cursorPos < value.length()) {
					value = value.substring(0, cursorPos) + c
							+ value.substring(cursorPos);
				} else {
					value = value.substring(0, cursorPos) + c;
				}
				cursorPos++;
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			} else if (key == Input.KEY_RETURN) {
				notifyListeners();
				
				if (consume) {
					container.getInput().consumeEvent();
				}
			}

		}
	}

	
	public void setFocus(boolean focus) {
		lastKey = -1;
		
		super.setFocus(focus);
	}
}
