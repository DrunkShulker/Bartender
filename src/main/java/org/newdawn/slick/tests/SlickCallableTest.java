package org.newdawn.slick.tests;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.SlickCallable;


public class SlickCallableTest extends BasicGame {
	
	private Image image;
	
	private Image back;
	
	private float rot;
	
	private AngelCodeFont font;
	
	private Animation homer;
	
	
	public SlickCallableTest() {
		super("Slick Callable Test");
	}
	
	
	public void init(GameContainer container) throws SlickException {
		image = new Image("testdata/rocket.png");
		back = new Image("testdata/sky.jpg");
		font = new AngelCodeFont("testdata/hiero.fnt","testdata/hiero.png");
		SpriteSheet sheet = new SpriteSheet("testdata/homeranim.png", 36, 65);
		homer = new Animation(sheet, 0,0,7,0,true,150,true);
	}

	
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.scale(2,2);
		g.fillRect(0, 0, 800, 600, back, 0, 0);
		g.resetTransform();
		
		g.drawImage(image,100,100);
		image.draw(100,200,80,200);
		
		font.drawString(100,200,"Text Drawn before the callable");
		
		SlickCallable callable = new SlickCallable() {
			protected void performGLOperations() throws SlickException {
				renderGL();
			}
		};
		callable.call();
		
		homer.draw(450,250,80,200);
		font.drawString(150,300,"Text Drawn after the callable");
	}

	
	public void renderGL() {		
		FloatBuffer pos = BufferUtils.createFloatBuffer(4);
		pos.put(new float[] { 5.0f, 5.0f, 10.0f, 0.0f}).flip();
		FloatBuffer red = BufferUtils.createFloatBuffer(4);
		red.put(new float[] { 0.8f, 0.1f, 0.0f, 1.0f}).flip();
	
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, pos);
		GL11.glEnable(GL11.GL_LIGHT0);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		float h = (float) 600 / (float) 800;
		GL11.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();	
		GL11.glTranslatef(0.0f, 0.0f, -40.0f);	
		GL11.glRotatef(rot,0,1,1);		
		
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, red);
		gear(0.5f, 2.0f, 2.0f, 10, 0.7f);
	}
	
	
	private void gear(float inner_radius, float outer_radius, float width, int teeth, float tooth_depth) {
		int i;
		float r0, r1, r2;
		float angle, da;
		float u, v, len;

		r0 = inner_radius;
		r1 = outer_radius - tooth_depth / 2.0f;
		r2 = outer_radius + tooth_depth / 2.0f;

		da = 2.0f * (float) Math.PI / teeth / 4.0f;

		GL11.glShadeModel(GL11.GL_FLAT);

		GL11.glNormal3f(0.0f, 0.0f, 1.0f);

		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), width * 0.5f);
			if (i < teeth) {
				GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), width * 0.5f);
				GL11.glVertex3f(r1 * (float) Math.cos(angle + 3.0f * da), r1 * (float) Math.sin(angle + 3.0f * da),
												width * 0.5f);
			}
		}
		GL11.glEnd();

		
		GL11.glBegin(GL11.GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + da), r2 * (float) Math.sin(angle + da), width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + 2.0f * da), r2 * (float) Math.sin(angle + 2.0f * da), width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle + 3.0f * da), r1 * (float) Math.sin(angle + 3.0f * da), width * 0.5f);
		}
		GL11.glEnd();

		
		GL11.glNormal3f(0.0f, 0.0f, -1.0f);
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), -width * 0.5f);
			GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), -width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1 * (float) Math.sin(angle + 3 * da), -width * 0.5f);
			GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), -width * 0.5f);
		}
		GL11.glEnd();

		
		GL11.glBegin(GL11.GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1 * (float) Math.sin(angle + 3 * da), -width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2 * (float) Math.sin(angle + 2 * da), -width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + da), r2 * (float) Math.sin(angle + da), -width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), -width * 0.5f);
		}
		GL11.glEnd();
		GL11.glNormal3f(0.0f, 0.0f, 1.0f);

		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle), r1 * (float) Math.sin(angle), -width * 0.5f);
			u = r2 * (float) Math.cos(angle + da) - r1 * (float) Math.cos(angle);
			v = r2 * (float) Math.sin(angle + da) - r1 * (float) Math.sin(angle);
			len = (float) Math.sqrt(u * u + v * v);
			u /= len;
			v /= len;
			GL11.glNormal3f(v, -u, 0.0f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + da), r2 * (float) Math.sin(angle + da), width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + da), r2 * (float) Math.sin(angle + da), -width * 0.5f);
			GL11.glNormal3f((float) Math.cos(angle), (float) Math.sin(angle), 0.0f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2 * (float) Math.sin(angle + 2 * da), width * 0.5f);
			GL11.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2 * (float) Math.sin(angle + 2 * da), -width * 0.5f);
			u = r1 * (float) Math.cos(angle + 3 * da) - r2 * (float) Math.cos(angle + 2 * da);
			v = r1 * (float) Math.sin(angle + 3 * da) - r2 * (float) Math.sin(angle + 2 * da);
			GL11.glNormal3f(v, -u, 0.0f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1 * (float) Math.sin(angle + 3 * da), width * 0.5f);
			GL11.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1 * (float) Math.sin(angle + 3 * da), -width * 0.5f);
			GL11.glNormal3f((float) Math.cos(angle), (float) Math.sin(angle), 0.0f);
		}
		GL11.glVertex3f(r1 * (float) Math.cos(0), r1 * (float) Math.sin(0), width * 0.5f);
		GL11.glVertex3f(r1 * (float) Math.cos(0), r1 * (float) Math.sin(0), -width * 0.5f);
		GL11.glEnd();

		GL11.glShadeModel(GL11.GL_SMOOTH);

		
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			GL11.glNormal3f(-(float) Math.cos(angle), -(float) Math.sin(angle), 0.0f);
			GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), -width * 0.5f);
			GL11.glVertex3f(r0 * (float) Math.cos(angle), r0 * (float) Math.sin(angle), width * 0.5f);
		}
		GL11.glEnd();
	}

	
	public void update(GameContainer container, int delta) {
		rot += delta * 0.1f;
	}

	
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new SlickCallableTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
