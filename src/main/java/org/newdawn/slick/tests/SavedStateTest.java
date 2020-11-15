package org.newdawn.slick.tests;
	
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SavedState;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;


public class SavedStateTest extends BasicGame implements ComponentListener {
	
	private TextField name;
	
	private TextField age;
	
	private String nameValue = "none";
	
	private int ageValue = 0;
	
	private SavedState state;
	
	private String message = "Enter a name and age to store";
	
	
	public SavedStateTest() {
		super("Saved State Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		state = new SavedState("testdata");
		nameValue = state.getString("name","DefaultName");
		ageValue = (int) state.getNumber("age",64);
		
		name = new TextField(container,container.getDefaultFont(),100,100,300,20,this);
		age = new TextField(container,container.getDefaultFont(),100,150,201,20,this);
	}

	
	public void render(GameContainer container, Graphics g) {
		name.render(container, g);
		age.render(container, g);
		
		container.getDefaultFont().drawString(100, 300, "Stored Name: "+nameValue);
		container.getDefaultFont().drawString(100, 350, "Stored Age: "+ageValue);
		container.getDefaultFont().drawString(200, 500, message);
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
	}
	
	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			System.exit(0);
		}
	}
	
	
	private static AppGameContainer container;
	
	
	public static void main(String[] argv) {
		try {
			container = new AppGameContainer(new SavedStateTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	
	public void componentActivated(AbstractComponent source) {
		if (source == name) {
			nameValue = name.getText();
			state.setString("name", nameValue);
		}
		if (source == age) {
			try {
				ageValue = Integer.parseInt(age.getText());
				state.setNumber("age", ageValue);
			} catch (NumberFormatException e) {
				
			}
		}

		try {
			state.save();
		} catch (Exception e) {
			message = System.currentTimeMillis() + " : Failed to save state";
		}
	}
}
