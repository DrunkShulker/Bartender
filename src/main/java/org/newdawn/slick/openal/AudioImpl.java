package org.newdawn.slick.openal;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;


public class AudioImpl implements Audio {
	
	private SoundStore store;
	
	private int buffer;
	
	private int index = -1;
	
	
	private float length;
	
	
	AudioImpl(SoundStore store, int buffer) {
		this.store = store;
		this.buffer = buffer;
		
		int bytes = AL10.alGetBufferi(buffer, AL10.AL_SIZE);
		int bits = AL10.alGetBufferi(buffer, AL10.AL_BITS);
		int channels = AL10.alGetBufferi(buffer, AL10.AL_CHANNELS);
		int freq = AL10.alGetBufferi(buffer, AL10.AL_FREQUENCY);
		
		int samples = bytes / (bits / 8);
		length = (samples / (float) freq) / channels;
	}
	
	
	public int getBufferID() {
		return buffer;
	}
	
	
	protected AudioImpl() {
		
	}
	
	
	public void stop() {
		if (index != -1) {
			store.stopSource(index);
			index = -1;
		}
	}
	
	
	public boolean isPlaying() {
		if (index != -1) {
			return store.isPlaying(index);
		}
		
		return false;
	}
	
	
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		index = store.playAsSound(buffer, pitch, gain, loop);
		return store.getSource(index);
	}


	
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		index = store.playAsSoundAt(buffer, pitch, gain, loop, x, y, z);
		return store.getSource(index);
	}
	
	
	public int playAsMusic(float pitch, float gain, boolean loop) {
		store.playAsMusic(buffer, pitch, gain, loop);
		index = 0;
		return store.getSource(0);
	}
	
	
	public static void pauseMusic() {
		SoundStore.get().pauseLoop();
	}

	
	public static void restartMusic() {
		SoundStore.get().restartLoop();
	}
	
	
	public boolean setPosition(float position) {
		position = position % length;
		
		AL10.alSourcef(store.getSource(index), AL11.AL_SEC_OFFSET, position);
		if (AL10.alGetError() != 0) {
			return false;
		}
		return true;
	}

	
	public float getPosition() {
		return AL10.alGetSourcef(store.getSource(index), AL11.AL_SEC_OFFSET);
	}
}
