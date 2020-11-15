package org.newdawn.slick.openal;

import ibxm.Module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;


public class MODSound extends AudioImpl {
	
	
	
	private Module module;
	
	private SoundStore store;
	
	
	public MODSound(SoundStore store, InputStream in) throws IOException {
		this.store = store;
	}
	
	
	public int playAsMusic(float pitch, float gain, boolean loop) {
		cleanUpSource();

		store.setCurrentMusicVolume(gain);
		
		store.setMOD(this);
		
		return store.getSource(0);
	}

	
	private void cleanUpSource() {
		AL10.alSourceStop(store.getSource(0));
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		int queued = AL10.alGetSourcei(store.getSource(0), AL10.AL_BUFFERS_QUEUED);
		
		while (queued > 0)
		{
			AL10.alSourceUnqueueBuffers(store.getSource(0), buffer);
			queued--;
		}
		
		AL10.alSourcei(store.getSource(0), AL10.AL_BUFFER, 0);
	}
	
	
	public void poll() {
	}
	
	
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		return -1;
	}

	
	public void stop() {
		store.setMOD(null);
	}

	
	public float getPosition() {
		throw new RuntimeException("Positioning on modules is not currently supported");
	}

	
	public boolean setPosition(float position) {
		throw new RuntimeException("Positioning on modules is not currently supported");
	}
}
