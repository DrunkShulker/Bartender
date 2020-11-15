package org.newdawn.slick.particles;

import org.newdawn.slick.Image;


public interface ParticleEmitter {
	
	public void update(ParticleSystem system, int delta);

	
	public boolean completed();
	
	
	public void wrapUp();
	
	
	public void updateParticle(Particle particle, int delta);
	
	
	public boolean isEnabled();
	
	
	public void setEnabled(boolean enabled);
	
	
	public boolean useAdditive();
	
	
	public Image getImage();

	
	public boolean isOriented();
	
	
	public boolean usePoints(ParticleSystem system);
	
	
	public void resetState();
}
