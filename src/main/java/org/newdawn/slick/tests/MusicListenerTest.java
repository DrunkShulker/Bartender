package org.newdawn.slick.tests;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;


public class MusicListenerTest extends BasicGame implements MusicListener {
	
	private boolean musicEnded = false;
	
	private boolean musicSwapped = false;
	
	private Music music;
	
	private Music stream;
	
	
	public MusicListenerTest() {
		super("Music Listener Test");
	}

	
	public void init(GameContainer container) throws SlickException {
		music = new Music("testdata/restart.ogg", false);
		stream = new Music("testdata/restart.ogg", false);
		
		music.addListener(this);
		stream.addListener(this);
	}

	
	public void update(GameContainer container, int delta) throws SlickException {
	}

	
	public void musicEnded(Music music) {
		musicEnded = true;
	}

	
	public void musicSwapped(Music music, Music newMusic) {
		musicSwapped = true;
	}
	
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.drawString("Press M to play music", 100, 100);
		g.drawString("Press S to stream music", 100, 150);
		if (musicEnded) {
			g.drawString("Music Ended", 100, 200);
		}
		if (musicSwapped) {
			g.drawString("Music Swapped", 100, 250);
		}
	}

	
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_M) {
			musicEnded = false;
			musicSwapped = false;
			music.play();
		}
		if (key == Input.KEY_S) {
			musicEnded = false;
			musicSwapped = false;
			stream.play();
		}
	}
	
	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new MusicListenerTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
