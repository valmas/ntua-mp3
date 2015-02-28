package ntua.multimedia.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

/**
 * The Class Player.
 */
public class Player {
	
	/** The bitstream. */
	private Bitstream bitstream;
	
	/** The running. */
	private boolean running = true;
	
	/** The closed. */
	private boolean closed = false;
	
	/** The fast forward. */
	private boolean fastForward = false;
	
	/** The position. */
	private int position;
	
	/**
	 * The MPEG audio decoder. 
	 */
	private Decoder		decoder; 
	
	/** The gui initialization monitor. */
	private final Object GUI_INITIALIZATION_MONITOR = new Object();
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
	private AudioDevice	audio;
	
	/**
	 * Instantiates a new player.
	 *
	 * @param stream the stream
	 * @throws JavaLayerException the java layer exception
	 */
	protected Player(InputStream stream) throws JavaLayerException{
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
				
		FactoryRegistry r = FactoryRegistry.systemRegistry();
		audio = r.createAudioDevice();
		audio.open(decoder);
	}
	
	/**
	 * Fast forward.
	 *
	 * @throws JavaLayerException the java layer exception
	 */
	protected synchronized  void fastForward() throws JavaLayerException{
		for(int i = 0; i < 200; i++){
			Header h = bitstream.readFrame();
			if (h == null) 
				return;
			bitstream.closeFrame();
			position ++;
		}
		fastForward = false;
	}

	/**
	 * Decode frame.
	 *
	 * @return true, if successful
	 * @throws JavaLayerException the java layer exception
	 */
	private  boolean decodeFrame() throws JavaLayerException
	{		
		try
		{
			AudioDevice out = audio;
			if (out==null)
				return false;
			Header h;
			synchronized (this) {
				if (closed)
					return false;

				if(fastForward)
					fastForward();
				h = bitstream.readFrame();	
			}
			
			if (h==null)
				return false;
			
			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
				
			synchronized (this)
			{
				out = audio;
				if (out!=null)
				{					
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}				
			}														
			bitstream.closeFrame();
		}		
		catch (RuntimeException ex)
		{
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}
	
	/**
	 * Gets the metadata.
	 *
	 * @return the metadata
	 */
	protected String getMetadata(){
		InputStream input = bitstream.getRawID3v2();
		return getStringFromInputStream(input);
	}
	
	/**
	 * Gets the string from input stream.
	 *
	 * @param is the is
	 * @return the string from input stream
	 */
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
 
	}
	
	/**
	 * Play.
	 *
	 * @param frames the frames
	 * @throws JavaLayerException the java layer exception
	 */
	protected void play(int frames) throws JavaLayerException{
		position = 0;
		boolean ret = true; 
		running = true;
		synchronized (GUI_INITIALIZATION_MONITOR) {
			while (position < frames && ret) {
				if(running) {
					ret = decodeFrame();
				} else {
					try {
						while(!running) {
							GUI_INITIALIZATION_MONITOR.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(closed)
						return;
				}
				position++;
			}
		}
		
		AudioDevice out = audio;
		if (out!=null)
		{				
			out.flush();
			synchronized (this)
			{
				stop();
			}				
		}
		position = 0;
	}
	
	/**
	 * Pause.
	 */
	protected void pause(){
		running = false;
	}
	
	/**
	 * Resume.
	 */
	protected synchronized void resume(){
		if(running)
			return;
		synchronized (GUI_INITIALIZATION_MONITOR) {
			running = true;
			GUI_INITIALIZATION_MONITOR.notify();
		}
	}
	
	/**
	 * Stop.
	 */
	protected synchronized void stop()	{
		AudioDevice out = audio;
		if (out != null) {
			out.close();
			out.getPosition();
			try
			{
				bitstream.close();
				closed = true;
			}
			catch (BitstreamException ex)
			{}
			running = true;
		}
	}
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	protected int getPosition(){
		return position;
	}

	/**
	 * Sets the fast forward.
	 *
	 * @param fastForward the new fast forward
	 */
	protected void setFastForward(boolean fastForward) {
		this.fastForward = fastForward;
	}
	
}
