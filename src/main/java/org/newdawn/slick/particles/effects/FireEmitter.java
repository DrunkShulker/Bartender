package org.newdawn.slick.particles.effects;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.Particle;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;


public class FireEmitter implements ParticleEmitter {
	
	private int x;
	
	private int y;
	
	
	private int interval = 50;
	
	private int timer;
	
	private float size = 40;
	
	
	public FireEmitter() {
	}

	
	public FireEmitter(int x, int y) {
		this.x = x;
		this.y = y;
	}

	
	public FireEmitter(int x, int y, float size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	
	public void update(ParticleSystem system, int delta) {
		timer -= delta;
		if (timer <= 0) {
			timer = interval;
			Particle p = system.getNewParticle(this, 1000);
			p.setColor(1, 1, 1, 0.5f);
			p.setPosition(x, y);
			p.setSize(size);
			float vx = (float) (-0.02f + (Math.random() * 0.04f));
			float vy = (float) (-(Math.random() * 0.15f));
			p.setVelocity(vx,vy,1.1f);
		}
	}

	
	public void updateParticle(Particle particle, int delta) {
		if (particle.getLife() > 600) {
			particle.adjustSize(0.07f * delta);
		} else {
			particle.adjustSize(-0.04f * delta * (size / 40.0f));
		}
		float c = 0.002f * delta;
		particle.adjustColor(0,-c/2,-c*2,-c/4);
	}

	
	public boolean isEnabled() {
		return true;
	}

	
	public void setEnabled(boolean enabled) {
	}

	
	public boolean completed() {
		return false;
	}

	
	public boolean useAdditive() {
		return false;
	}

	
	public Image getImage() {
		return null;
	}

	
	public boolean usePoints(ParticleSystem system) {
		return false;
	}

	
	public boolean isOriented() {
		return false;
	}

	
	public void wrapUp() {
	}

	
	public void resetState() {
	}
}
