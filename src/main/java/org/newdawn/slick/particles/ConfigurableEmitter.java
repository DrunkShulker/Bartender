package org.newdawn.slick.particles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;
import org.newdawn.slick.util.Log;


public class ConfigurableEmitter implements ParticleEmitter {
	
	private static String relativePath = "";

	
	public static void setRelativePath(String path) {
		if (!path.endsWith("/")) {
			path += "/";
		}
		relativePath = path;
	}

	
	public Range spawnInterval = new Range(100, 100);
	
	public Range spawnCount = new Range(5, 5);
	
	public Range initialLife = new Range(1000, 1000);
	
	public Range initialSize = new Range(10, 10);
	
	public Range xOffset = new Range(0, 0);
	
	public Range yOffset = new Range(0, 0);
	
	public RandomValue spread = new RandomValue(360);
	
	public SimpleValue angularOffset = new SimpleValue(0);
	
	public Range initialDistance = new Range(0, 0);
	
	public Range speed = new Range(50, 50);
	
	public SimpleValue growthFactor = new SimpleValue(0);
	
	public SimpleValue gravityFactor = new SimpleValue(0);
	
	public SimpleValue windFactor = new SimpleValue(0);
	
	public Range length = new Range(1000, 1000);
	
	public ArrayList colors = new ArrayList();
	
	public SimpleValue startAlpha = new SimpleValue(255);
	
	public SimpleValue endAlpha = new SimpleValue(0);

	
	public LinearInterpolator alpha;
	
	public LinearInterpolator size;
	
	public LinearInterpolator velocity;
	
	public LinearInterpolator scaleY;

	
	public Range emitCount = new Range(1000, 1000);
	
	public int usePoints = Particle.INHERIT_POINTS;

	
	public boolean useOriented = false;
	
	public boolean useAdditive = false;

	
	public String name;
	
	public String imageName = "";
	
	private Image image;
	
	private boolean updateImage;

	
	private boolean enabled = true;
	
	private float x;
	
	private float y;
	
	private int nextSpawn = 0;

	
	private int timeout;
	
	private int particleCount;
	
	private ParticleSystem engine;
	
	private int leftToEmit;

	
	protected boolean wrapUp = false;
	
	protected boolean completed = false;
	
	protected boolean adjust;
	
	protected float adjustx;
	
	protected float adjusty;
	
	
	public ConfigurableEmitter(String name) {
		this.name = name;
		leftToEmit = (int) emitCount.random();
		timeout = (int) (length.random());

		colors.add(new ColorRecord(0, Color.white));
		colors.add(new ColorRecord(1, Color.red));

		ArrayList curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 0.0f));
		curve.add(new Vector2f(1.0f, 255.0f));
		alpha = new LinearInterpolator(curve, 0, 255);

		curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 0.0f));
		curve.add(new Vector2f(1.0f, 255.0f));
		size = new LinearInterpolator(curve, 0, 255);

		curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 0.0f));
		curve.add(new Vector2f(1.0f, 1.0f));
		velocity = new LinearInterpolator(curve, 0, 1);

		curve = new ArrayList();
		curve.add(new Vector2f(0.0f, 0.0f));
		curve.add(new Vector2f(1.0f, 1.0f));
		scaleY = new LinearInterpolator(curve, 0, 1);
	}

	
	public void setImageName(String imageName) {
		if (imageName.length() == 0) {
			imageName = null;
		}

		this.imageName = imageName;
		if (imageName == null) {
			image = null;
		} else {
			updateImage = true;
		}
	}
	
	
	public String getImageName() {
		return imageName;
	}

	
	public String toString() {
		return "[" + name + "]";
	}

	
	public void setPosition(float x, float y) {
		setPosition(x,y,true);
	}

	
	public void setPosition(float x, float y, boolean moveParticles) {
		if (moveParticles) {
			adjust = true;
			adjustx -= this.x - x;
			adjusty -= this.y - y;
		}
		this.x = x;
		this.y = y;		
	}
	
	
	public float getX() {
		return x;
	}

	
	public float getY() {
		return y;
	}

	
	public boolean isEnabled() {
		return enabled;
	}

	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	public void update(ParticleSystem system, int delta) {
		this.engine = system;

		if (!adjust) {
			adjustx = 0;
			adjusty = 0;
		} else {
			adjust = false;
		}
		
		if (updateImage) {
			updateImage = false;
			try {
				image = new Image(relativePath + imageName);
			} catch (SlickException e) {
				image = null;
				Log.error(e);
			}
		}

		if ((wrapUp) || 
		    ((length.isEnabled()) && (timeout < 0)) ||
		    ((emitCount.isEnabled() && (leftToEmit <= 0)))) {
			if (particleCount == 0) {
				completed = true;
			}
		}
		particleCount = 0;
		
		if (wrapUp) {
			return;
		}
		
		if (length.isEnabled()) {
			if (timeout < 0) {
				return;
			}
			timeout -= delta;
		}
		if (emitCount.isEnabled()) {
			if (leftToEmit <= 0) {
				return;
			}
		}

		nextSpawn -= delta;
		if (nextSpawn < 0) {
			nextSpawn = (int) spawnInterval.random();
			int count = (int) spawnCount.random();

			for (int i = 0; i < count; i++) {
				Particle p = system.getNewParticle(this, initialLife.random());
				p.setSize(initialSize.random());
				p.setPosition(x + xOffset.random(), y + yOffset.random());
				p.setVelocity(0, 0, 0);

				float dist = initialDistance.random();
				float power = speed.random();
				if ((dist != 0) || (power != 0)) {
					float s = spread.getValue(0);
					float ang = (s + angularOffset.getValue(0) - (spread
							.getValue() / 2)) - 90;
					float xa = (float) FastTrig.cos(Math.toRadians(ang)) * dist;
					float ya = (float) FastTrig.sin(Math.toRadians(ang)) * dist;
					p.adjustPosition(xa, ya);

					float xv = (float) FastTrig.cos(Math.toRadians(ang));
					float yv = (float) FastTrig.sin(Math.toRadians(ang));
					p.setVelocity(xv, yv, power * 0.001f);
				}

				if (image != null) {
					p.setImage(image);
				}

				ColorRecord start = (ColorRecord) colors.get(0);
				p.setColor(start.col.r, start.col.g, start.col.b, startAlpha
						.getValue(0) / 255.0f);
				p.setUsePoint(usePoints);
				p.setOriented(useOriented);

				if (emitCount.isEnabled()) {
					leftToEmit--;
					if (leftToEmit <= 0) {
						break;
					}
				}
			}
		}
	}

	
	public void updateParticle(Particle particle, int delta) {
		particleCount++;
		
		
		particle.x += adjustx;
		particle.y += adjusty;

		particle.adjustVelocity(windFactor.getValue(0) * 0.00005f * delta, gravityFactor
				.getValue(0) * 0.00005f * delta);
		
		float offset = particle.getLife() / particle.getOriginalLife();
		float inv = 1 - offset;
		float colOffset = 0;
		float colInv = 1;

		Color startColor = null;
		Color endColor = null;
		for (int i = 0; i < colors.size() - 1; i++) {
			ColorRecord rec1 = (ColorRecord) colors.get(i);
			ColorRecord rec2 = (ColorRecord) colors.get(i + 1);

			if ((inv >= rec1.pos) && (inv <= rec2.pos)) {
				startColor = rec1.col;
				endColor = rec2.col;

				float step = rec2.pos - rec1.pos;
				colOffset = inv - rec1.pos;
				colOffset /= step;
				colOffset = 1 - colOffset;
				colInv = 1 - colOffset;
			}
		}

		if (startColor != null) {
			float r = (startColor.r * colOffset) + (endColor.r * colInv);
			float g = (startColor.g * colOffset) + (endColor.g * colInv);
			float b = (startColor.b * colOffset) + (endColor.b * colInv);

			float a;
			if (alpha.isActive()) {
				a = alpha.getValue(inv) / 255.0f;
			} else {
				a = ((startAlpha.getValue(0) / 255.0f) * offset)
						+ ((endAlpha.getValue(0) / 255.0f) * inv);
			}
			particle.setColor(r, g, b, a);
		}

		if (size.isActive()) {
			float s = size.getValue(inv);
			particle.setSize(s);
		} else {
			particle.adjustSize(delta * growthFactor.getValue(0) * 0.001f);
		}

		if (velocity.isActive()) {
			particle.setSpeed(velocity.getValue(inv));
		}

		if (scaleY.isActive()) {
			particle.setScaleY(scaleY.getValue(inv));
		}
	}

	
	public boolean completed() {
		if (engine == null) {
			return false;
		}

		if (length.isEnabled()) {
			if (timeout > 0) {
				return false;
			}
			return completed;
		}
		if (emitCount.isEnabled()) {
			if (leftToEmit > 0) {
				return false;
			}
			return completed;
		}

		if (wrapUp) {
			return completed;
		}
		
		return false;
	}

	
	public void replay() {
		reset();
		nextSpawn = 0;
		leftToEmit = (int) emitCount.random();
		timeout = (int) (length.random());
	}

	
	public void reset() {
	    completed = false; 
		if (engine != null) {
			engine.releaseAll(this);
		}
	}

	
	public void replayCheck() {
		if (completed()) {
			if (engine != null) {
				if (engine.getParticleCount() == 0) {
					replay();
				}
			}
		}
	}
	
	
	public ConfigurableEmitter duplicate() {
		ConfigurableEmitter theCopy = null;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ParticleIO.saveEmitter(bout, this);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			theCopy = ParticleIO.loadEmitter(bin);
		} catch (IOException e) {
			Log.error("Slick: ConfigurableEmitter.duplicate(): caught exception " + e.toString());
			return null;
		}
		return theCopy;
	}

	
	public interface Value {
		
		public float getValue(float time);
	}

	
	public class SimpleValue implements Value {
		
		private float value;
		
		private float next;

		
		private SimpleValue(float value) {
			this.value = value;
		}

		
		public float getValue(float time) {
			return value;
		}

		
		public void setValue(float value) {
			this.value = value;
		}
	}

	
	public class RandomValue implements Value {
		
		private float value;

		
		private RandomValue(float value) {
			this.value = value;
		}

		
		public float getValue(float time) {
			return (float) (Math.random() * value);
		}

		
		public void setValue(float value) {
			this.value = value;
		}

		
		public float getValue() {
			return value;
		}
	}

	
	public class LinearInterpolator implements Value {
		
		private ArrayList curve;
		
		private boolean active;
		
		private int min;
		
		private int max;

		
		public LinearInterpolator(ArrayList curve, int min, int max) {
			this.curve = curve;
			this.min = min;
			this.max = max;
			this.active = false;
		}

		
		public void setCurve(ArrayList curve) {
			this.curve = curve;
		}

		
		public ArrayList getCurve() {
			return curve;
		}

		
		public float getValue(float t) {
			
			Vector2f p0 = (Vector2f) curve.get(0);
			for (int i = 1; i < curve.size(); i++) {
				Vector2f p1 = (Vector2f) curve.get(i);

				if (t >= p0.getX() && t <= p1.getX()) {
					
					float st = (t - p0.getX())
							/ (p1.getX() - p0.getX());
					float r = p0.getY() + st
							* (p1.getY() - p0.getY());
					
					

					return r;
				}

				p0 = p1;
			}
			return 0;
		}

		
		public boolean isActive() {
			return active;
		}

		
		public void setActive(boolean active) {
			this.active = active;
		}

		
		public int getMax() {
			return max;
		}

		
		public void setMax(int max) {
			this.max = max;
		}

		
		public int getMin() {
			return min;
		}

		
		public void setMin(int min) {
			this.min = min;
		}
	}

	
	public class ColorRecord {
		
		public float pos;
		
		public Color col;

		
		public ColorRecord(float pos, Color col) {
			this.pos = pos;
			this.col = col;
		}
	}

	
	public void addColorPoint(float pos, Color col) {
		colors.add(new ColorRecord(pos, col));
	}

	
	public class Range {
		
		private float max;
		
		private float min;
		
		private boolean enabled = false;

		
		private Range(float min, float max) {
			this.min = min;
			this.max = max;
		}

		
		public float random() {
			return (float) (min + (Math.random() * (max - min)));
		}

		
		public boolean isEnabled() {
			return enabled;
		}

		
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		
		public float getMax() {
			return max;
		}

		
		public void setMax(float max) {
			this.max = max;
		}

		
		public float getMin() {
			return min;
		}

		
		public void setMin(float min) {
			this.min = min;
		}
	}

	public boolean useAdditive() {
		return useAdditive;
	}
	
	public boolean isOriented() {
		return this.useOriented;
	}
	
	public boolean usePoints(ParticleSystem system) {
		return (this.usePoints == Particle.INHERIT_POINTS) && (system.usePoints()) ||
			   (this.usePoints == Particle.USE_POINTS); 
	}

	public Image getImage() {
		return image;
	}

	public void wrapUp() {
		wrapUp = true;
	}

	public void resetState() {
		wrapUp = false;
		replay();
	}
}
