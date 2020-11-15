package org.newdawn.slick.openal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;


public class SoundStore {
	
	
	private static SoundStore store = new SoundStore();
	
	
	private boolean sounds;
	
	private boolean music;
	
	private boolean soundWorks;
	
	private int sourceCount;
	
	private HashMap loaded = new HashMap();
	
	private int currentMusic = -1;
	
	private IntBuffer sources;
	
	private int nextSource;
	
	private boolean inited = false;
	
	private MODSound mod;
	
	private OpenALStreamPlayer stream;
	
	
	private float musicVolume = 1.0f;
	
	private float soundVolume = 1.0f;
	
	private float lastCurrentMusicVolume = 1.0f;
	
	
	private boolean paused;
	
	private boolean deferred;
	
	
    private FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
    
    private FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3);
    
    
    private int maxSources = 64;
    
	
	private SoundStore() {
	}
	
	
	public void clear() {
		store = new SoundStore();
	}

	
	public void disable() {
		inited = true;
	}
	
    
    public void setDeferredLoading(boolean deferred) {
    	this.deferred = deferred;
    }
    
    
    public boolean isDeferredLoading() {
    	return deferred;
    }
    
	
	public void setMusicOn(boolean music) {
		if (soundWorks) {
			this.music = music;
			if (music) {
				restartLoop();
				setMusicVolume(musicVolume);
			} else {
				pauseLoop();
			}
		}
	}
	
	
	public boolean isMusicOn() {
		return music;
	}

	
	public void setMusicVolume(float volume) {
		if (volume < 0) {
			volume = 0;
		}
		if (volume > 1) {
			volume = 1;
		}
		
		musicVolume = volume;
		if (soundWorks) {
			AL10.alSourcef(sources.get(0), AL10.AL_GAIN, lastCurrentMusicVolume * musicVolume); 
		}
	}

	
	public float getCurrentMusicVolume() {
		return lastCurrentMusicVolume;
	}
	
	
	public void setCurrentMusicVolume(float volume) {
		if (volume < 0) {
			volume = 0;
		}
		if (volume > 1) {
			volume = 1;
		}
		
		if (soundWorks) {
			lastCurrentMusicVolume = volume;
			AL10.alSourcef(sources.get(0), AL10.AL_GAIN, lastCurrentMusicVolume * musicVolume); 
		}
	}
	
	
	public void setSoundVolume(float volume) {
		if (volume < 0) {
			volume = 0;
		}
		soundVolume = volume;
	}
	
	
	public boolean soundWorks() {
		return soundWorks;
	}
	
	
	public boolean musicOn() {
		return music;
	}

	
	public float getSoundVolume() {
		return soundVolume;
	}
	
	
	public float getMusicVolume() {
		return musicVolume;
	}
	
	
	public int getSource(int index) {
		if (!soundWorks) {
			return -1;
		}
		if (index < 0) {
			return -1;
		}
		return sources.get(index);
	}
	
	
	public void setSoundsOn(boolean sounds) {
		if (soundWorks) {
			this.sounds = sounds;
		}
	}
	
	
	public boolean soundsOn() {
		return sounds;
	}
	
	
	public void setMaxSources(int max) {
		this.maxSources = max;
	}
	
	
	public void init() {
		if (inited) {
			return;
		}
		Log.info("Initialising sounds..");
		inited = true;
		
		AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
				try {
					AL.create();
					soundWorks = true;
					sounds = true;
					music = true;
					Log.info("- Sound works");
				} catch (Exception e) {
					Log.error("Sound initialisation failure.");
					Log.error(e);
					soundWorks = false;
					sounds = false;
					music = false;
				}
				
				return null;
            }});
		
		if (soundWorks) {
			sourceCount = 0;
			sources = BufferUtils.createIntBuffer(maxSources);
			while (AL10.alGetError() == AL10.AL_NO_ERROR) {
				IntBuffer temp = BufferUtils.createIntBuffer(1);
				
				try {
					AL10.alGenSources(temp);
				
					if (AL10.alGetError() == AL10.AL_NO_ERROR) {
						sourceCount++;
						sources.put(temp.get(0));
						if (sourceCount > maxSources-1) {
							break;
						}
					} 
				} catch (OpenALException e) {
					
					break;
				}
			}
			Log.info("- "+sourceCount+" OpenAL source available");
		
			if (AL10.alGetError() != AL10.AL_NO_ERROR) {
				sounds = false;
				music = false;
				soundWorks = false;
				Log.error("- AL init failed");
			} else {
				FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(
						new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f });
				FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(
						new float[] { 0.0f, 0.0f, 0.0f });
				FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(
						new float[] { 0.0f, 0.0f, 0.0f });
				listenerPos.flip();
				listenerVel.flip();
				listenerOri.flip();
				AL10.alListener(AL10.AL_POSITION, listenerPos);
				AL10.alListener(AL10.AL_VELOCITY, listenerVel);
				AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
   			 
				Log.info("- Sounds source generated");
			}
		}
	}

	
	void stopSource(int index) {
		AL10.alSourceStop(sources.get(index));
	}
	
	
	int playAsSound(int buffer,float pitch,float gain,boolean loop) {
		return playAsSoundAt(buffer, pitch, gain, loop, 0, 0, 0);
	}
	
	
	int playAsSoundAt(int buffer,float pitch,float gain,boolean loop,float x, float y, float z) {
		gain *= soundVolume;
		if (gain == 0) {
			gain = 0.001f;
		}
		if (soundWorks) {
			if (sounds) {
				int nextSource = findFreeSource();
				if (nextSource == -1) {
					return -1;
				}
				
				AL10.alSourceStop(sources.get(nextSource));
				
				AL10.alSourcei(sources.get(nextSource), AL10.AL_BUFFER, buffer);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_PITCH, pitch);
				AL10.alSourcef(sources.get(nextSource), AL10.AL_GAIN, gain); 
			    AL10.alSourcei(sources.get(nextSource), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			    
			    sourcePos.clear();
			    sourceVel.clear();
				sourceVel.put(new float[] { 0, 0, 0 });
				sourcePos.put(new float[] { x, y, z });
			    sourcePos.flip();
			    sourceVel.flip();
			    AL10.alSource(sources.get(nextSource), AL10.AL_POSITION, sourcePos);
    			AL10.alSource(sources.get(nextSource), AL10.AL_VELOCITY, sourceVel);
			    
				AL10.alSourcePlay(sources.get(nextSource)); 
				
				return nextSource;
			}
		}
		
		return -1;
	}
	
	boolean isPlaying(int index) {
		int state = AL10.alGetSourcei(sources.get(index), AL10.AL_SOURCE_STATE);
		
		return (state == AL10.AL_PLAYING);
	}
	
	
	private int findFreeSource() {
		for (int i=1;i<sourceCount-1;i++) {
			int state = AL10.alGetSourcei(sources.get(i), AL10.AL_SOURCE_STATE);
			
			if ((state != AL10.AL_PLAYING) && (state != AL10.AL_PAUSED)) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	void playAsMusic(int buffer,float pitch,float gain, boolean loop) {
		paused = false;
		
		setMOD(null);
		
		if (soundWorks) {
			if (currentMusic != -1) {
				AL10.alSourceStop(sources.get(0));
			}
			
			getMusicSource();
			
			AL10.alSourcei(sources.get(0), AL10.AL_BUFFER, buffer);
			AL10.alSourcef(sources.get(0), AL10.AL_PITCH, pitch);
		    AL10.alSourcei(sources.get(0), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
			
			currentMusic = sources.get(0);
			
			if (!music) {
				pauseLoop();
			} else {
				AL10.alSourcePlay(sources.get(0)); 
			}
		}
	}
	
	
	private int getMusicSource() {
		return sources.get(0);
	}
	
	
	public void setMusicPitch(float pitch) {
		if (soundWorks) {
			AL10.alSourcef(sources.get(0), AL10.AL_PITCH, pitch);
		}
	}
	
	
	public void pauseLoop() {
		if ((soundWorks) && (currentMusic != -1)){
			paused = true;
			AL10.alSourcePause(currentMusic);
		}
	}

	
	public void restartLoop() {
		if ((music) && (soundWorks) && (currentMusic != -1)){
			paused = false;
			AL10.alSourcePlay(currentMusic);
		}
	}
	
	
	boolean isPlaying(OpenALStreamPlayer player) {
		return stream == player;
	}
	
	
	public Audio getMOD(String ref) throws IOException {
		return getMOD(ref, ResourceLoader.getResourceAsStream(ref));
	}

	
	public Audio getMOD(InputStream in) throws IOException {
		return getMOD(in.toString(), in);
	}
	
	
	public Audio getMOD(String ref, InputStream in) throws IOException {
		if (!soundWorks) {
			return new NullAudio();
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, in, DeferredSound.MOD);
		}
		
		return new MODSound(this, in);
	}

	
	public Audio getAIF(String ref) throws IOException {
		return getAIF(ref, ResourceLoader.getResourceAsStream(ref));
	}
	

	
	public Audio getAIF(InputStream in) throws IOException {
		return getAIF(in.toString(), in);
	}
	
	
	public Audio getAIF(String ref, InputStream in) throws IOException {
		in = new BufferedInputStream(in);

		if (!soundWorks) {
			return new NullAudio();
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, in, DeferredSound.AIF);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				AiffData data = AiffData.create(in);
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), data.format, data.data, data.samplerate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				IOException x = new IOException("Failed to load: "+ref);
				x.initCause(e);
				
				throw x;
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new AudioImpl(this, buffer);
	}
	

	
	
	public Audio getWAV(String ref) throws IOException {
		return getWAV(ref, ResourceLoader.getResourceAsStream(ref));
	}
	
	
	public Audio getWAV(InputStream in) throws IOException {
		return getWAV(in.toString(), in);
	}
	
	
	public Audio getWAV(String ref, InputStream in) throws IOException {
		if (!soundWorks) {
			return new NullAudio();
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, in, DeferredSound.WAV);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				WaveData data = WaveData.create(in);
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), data.format, data.data, data.samplerate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				IOException x = new IOException("Failed to load: "+ref);
				x.initCause(e);
				
				throw x;
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new AudioImpl(this, buffer);
	}

	
	public Audio getOggStream(String ref) throws IOException {
		if (!soundWorks) {
			return new NullAudio();
		}
		
		setMOD(null);
		setStream(null);
		
		if (currentMusic != -1) {
			AL10.alSourceStop(sources.get(0));
		}
		
		getMusicSource();
		currentMusic = sources.get(0);
		
		return new StreamSound(new OpenALStreamPlayer(currentMusic, ref));
	}

	
	public Audio getOggStream(URL ref) throws IOException {
		if (!soundWorks) {
			return new NullAudio();
		}
		
		setMOD(null);
		setStream(null);
		
		if (currentMusic != -1) {
			AL10.alSourceStop(sources.get(0));
		}
		
		getMusicSource();
		currentMusic = sources.get(0);
		
		return new StreamSound(new OpenALStreamPlayer(currentMusic, ref));
	}
	
	
	public Audio getOgg(String ref) throws IOException {
		return getOgg(ref, ResourceLoader.getResourceAsStream(ref));
	}
	
	
	public Audio getOgg(InputStream in) throws IOException {
		return getOgg(in.toString(), in);
	}
	
	
	public Audio getOgg(String ref, InputStream in) throws IOException {
		if (!soundWorks) {
			return new NullAudio();
		}
		if (!inited) {
			throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
		}
		if (deferred) {
			return new DeferredSound(ref, in, DeferredSound.OGG);
		}
		
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				OggDecoder decoder = new OggDecoder();
				OggData ogg = decoder.getData(in);
				
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				                     
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				Sys.alert("Error","Failed to load: "+ref+" - "+e.getMessage());
				throw new IOException("Unable to load: "+ref);
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return new AudioImpl(this, buffer);
	}
	
	
	void setMOD(MODSound sound) {
		if (!soundWorks) {
			return;
		}

		currentMusic = sources.get(0);
		stopSource(0);
		
		this.mod = sound;
		if (sound != null) {
			this.stream = null;
		}
		paused = false;
	}

	
	void setStream(OpenALStreamPlayer stream) {
		if (!soundWorks) {
			return;
		}

		currentMusic = sources.get(0);
		this.stream = stream;
		if (stream != null) {
			this.mod = null;
		}
		paused = false;
	}
	
	
	public void poll(int delta) {
		if (!soundWorks) {
			return;
		}
		if (paused) {
			return;
		}

		if (music) {
			if (mod != null) {
				try {
					mod.poll();
				} catch (OpenALException e) {
					Log.error("Error with OpenGL MOD Player on this this platform");
					Log.error(e);
					mod = null;
				}
			}
			if (stream != null) {
				try {
					stream.update();
				} catch (OpenALException e) {
					Log.error("Error with OpenGL Streaming Player on this this platform");
					Log.error(e);
					mod = null;
				}
			}
		}
	}
	
	
	public boolean isMusicPlaying() 
	{
		if (!soundWorks) {
			return false;
		}
		
		int state = AL10.alGetSourcei(sources.get(0), AL10.AL_SOURCE_STATE);
		return ((state == AL10.AL_PLAYING) || (state == AL10.AL_PAUSED));
	}
	
	
	public static SoundStore get() {
		return store;
	}
	
	
	public void stopSoundEffect(int id) {
		AL10.alSourceStop(id);
	}
	
	
	public int getSourceCount() {
		return sourceCount;
	}
}
