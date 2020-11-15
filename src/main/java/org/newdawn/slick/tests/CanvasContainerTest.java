package org.newdawn.slick.tests;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class CanvasContainerTest extends BasicGame {
	
	private Image tga;
	
	private Image scaleMe;
	
	private Image scaled;
	
	private Image gif;
	
	private Image image;
	
	private Image subImage;
	
	private float rot;
	
	
	public CanvasContainerTest() {
		super("Canvas Container Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		image = tga = new Image("testdata/logo.tga");
		scaleMe = new Image("testdata/logo.tga", true, Image.FILTER_NEAREST);
		gif = new Image("testdata/logo.gif");
		scaled = gif.getScaledCopy(120, 120);
		subImage = image.getSubImage(200,0,70,260);
		rot = 0;
	}

	
	public void render(GameContainer container, Graphics g) {
		image.draw(0,0);
		image.draw(500,0,200,100);
		scaleMe.draw(500,100,200,100);
		scaled.draw(400,500);
		Image flipped = scaled.getFlippedCopy(true, false);
		flipped.draw(520,500);
		Image flipped2 = flipped.getFlippedCopy(false, true);
		flipped2.draw(520,380);
		Image flipped3 = flipped2.getFlippedCopy(true, false);
		flipped3.draw(400,380);
		
		for (int i=0;i<3;i++) {
			subImage.draw(200+(i*30),300);
		}
		
		g.translate(500, 200);
		g.rotate(50, 50, rot);
		g.scale(0.3f,0.3f);
		image.draw();
		g.resetTransform();
	}

	
	public void update(GameContainer container, int delta) {
		rot += delta * 0.1f;
		if (rot > 360) {
			rot -= 360;
		}
	}

	
	public static void main(String[] argv) {
		try {
			CanvasGameContainer container = new CanvasGameContainer(new CanvasContainerTest());
			
			Frame frame = new Frame("Test");
			frame.setLayout(new GridLayout(1,2));
			frame.setSize(500,500);
			frame.add(container);
			
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.setVisible(true);
			container.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_SPACE) {
			if (image == gif) {
				image = tga;
			} else {
				image = gif;
			}
		}
	}
}
