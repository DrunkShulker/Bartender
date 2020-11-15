package org.newdawn.slick.openal;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;


public class OpenALStreamPlayer {
	
	public static final int BUFFER_COUNT = 3;
	
	private static final int sectionSize = 4096 * 20;
	
	
	private byte[] buffer = new byte[sectionSize];
	
	private IntBuffer bufferNames;
	
	private ByteBuffer bufferData = BufferUtils.createByteBuffer(sectionSize);
	
	private IntBuffer unqueued = BufferUtils.createIntBuffer(1);
	
    private int source;
	
    private int remainingBufferCount;
	
	private boolean loop;
	
	private boolean done = true;
	
	private AudioInputStream audio;
	
	private String ref;
	
	private URL url;
	
	private float pitch;
	
	private float positionOffset;
	
	
	public OpenALStreamPlayer(int source, String ref) {
		this.source = source;
		this.ref = ref;
		
		bufferNames = BufferUtils.createIntBuffer(BUFFER_COUNT);
		AL10.alGenBuffers(bufferNames);
	}

	
	public OpenALStreamPlayer(int source, URL url) {
		this.source = source;
		this.url = url;

		bufferNames = BufferUtils.createIntBuffer(BUFFER_COUNT);
		AL10.alGenBuffers(bufferNames);
	}
	
	
	private void initStreams() throws IOException {
		if (audio != null) {
			audio.close();
		}
		
		OggInputStream audio;
		
		if (url != null) {
			audio = new OggInputStream(url.openStream());
		} else {
			audio = new OggInputStream(ResourceLoader.getResourceAsStream(ref));
		}
		
		this.audio = audio;
		positionOffset = 0;
	}
	
	
	public String getSource() {
		return (url == null) ? ref : url.toString();
	}
	
	
	private void removeBuffers() {
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		int queued = AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED);
		
		while (queued > 0)
		{
			AL10.alSourceUnqueueBuffers(source, buffer);
			queued--;
		}
	}
	
	
	public void play(boolean loop) throws IOException {
		this.loop = loop;
		initStreams();
		
		done = false;

		AL10.alSourceStop(source);
		removeBuffers();
		
		startPlayback();
	}
	
	
	public void setup(float pitch) {
		this.pitch = pitch;
	}
	
	
	public boolean done() {
		return done;
	}
	
	
	public void update() {
		if (done) {
			return;
		}

		float sampleRate = audio.getRate();
		float sampleSize;
		if (audio.getChannels() > 1) {
			sampleSize = 4; 
		} else {
			sampleSize = 2; 
		}
		
		int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
		while (processed > 0) {
			unqueued.clear();
			AL10.alSourceUnqueueBuffers(source, unqueued);
			
			int bufferIndex = unqueued.get(0);

			float bufferLength = (AL10.alGetBufferi(bufferIndex, AL10.AL_SIZE) / sampleSize) / sampleRate;
			positionOffset += bufferLength;
			
	        if (stream(bufferIndex)) {			
	        	AL10.alSourceQueueBuffers(source, unqueued);
	        } else {
	        	remainingBufferCount--;
	        	if (remainingBufferCount == 0) {
	        		done = true;
	        	}
	        }
	        processed--;
		}
		
		int state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
	    
	    if (state != AL10.AL_PLAYING) {
	    	AL10.alSourcePlay(source);
	    }
	}
	
	
	public boolean stream(int bufferId) {
		try {
			int count = audio.read(buffer);
			
			if (count != -1) {
				bufferData.clear();
				bufferData.put(buffer,0,count);
				bufferData.flip();

				int format = audio.getChannels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
				try {
					AL10.alBufferData(bufferId, format, bufferData, audio.getRate());
				} catch (OpenALException e) {
					Log.error("Failed to loop buffer: "+bufferId+" "+format+" "+count+" "+audio.getRate(), e);
					return false;
				}
			} else {
				if (loop) {
					initStreams();
					stream(bufferId);
				} else {
					done = true;
					return false;
				}
			}
			
			return true;
		} catch (IOException e) {
			Log.error(e);
			return false;
		}
	}

	
	public boolean setPosition(float position) {
		try {
			if (getPosition() > position) {
				initStreams();
			}

			float sampleRate = audio.getRate();
			float sampleSize;
			if (audio.getChannels() > 1) {
				sampleSize = 4; 
			} else {
				sampleSize = 2; 
			}

			while (positionOffset < position) {
				int count = audio.read(buffer);
				if (count != -1) {
					float bufferLength = (count / sampleSize) / sampleRate;
					positionOffset += bufferLength;
				} else {
					if (loop) {
						initStreams();
					} else {
						done = true;
					}
					return false;
				}
			}

			startPlayback(); 

			return true;
		} catch (IOException e) {
			Log.error(e);
			return false;
		}
	}

	
	private void startPlayback() {
		AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
		AL10.alSourcef(source, AL10.AL_PITCH, pitch);

		remainingBufferCount = BUFFER_COUNT;

		for (int i = 0; i < BUFFER_COUNT; i++) {
			stream(bufferNames.get(i));
		}

		AL10.alSourceQueueBuffers(source, bufferNames);
		AL10.alSourcePlay(source);
	}

	
	public float getPosition() {
		return positionOffset + AL10.alGetSourcef(source, AL11.AL_SEC_OFFSET);
	}
}

