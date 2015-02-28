package ntua.multimedia.code;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

/**
 * The Class Track.
 */
public class Track {
	
	/** The name. */
	private String name;

	/** The artist. */
	private String artist;
	
	/** The album name. */
	private String albumName;
	
	/** The cover. */
	private byte[] cover;
	
	/** The lyrics. */
	private String lyrics;
	
	/** The file. */
	private File file;
	
	/** The length. */
	private int length;
	
	/** The frames. */
	private int frames;
	
	/** The framerate. */
	private float framerate;
	
	/**
	 * Instantiates a new track.
	 * Sets the length, the album name, the title, the artist, the number of frames and the frame rate.
	 * Retrieves the lyrics and the album cover.
	 * 
	 * @param file the file of the mp3
	 */
	public Track(File file) {
		super();
		this.file = file;
		
		try {
			AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
			Map<String, Object> properties = baseFileFormat.properties();
			Long duration = (Long) properties.get("duration");
			length = (int) (duration / 1000);
			albumName = (String) properties.get("album");
			name = (String) properties.get("title");
			artist = (String) properties.get("author");
			frames = (int) properties.get("mp3.length.frames");
			framerate = (float) properties.get("mp3.framerate.fps");
		} catch (UnsupportedAudioFileException | IOException e) {
			System.err.println("Error while retrieving tags");
			e.printStackTrace();
		}
		cover = GetAlbumCoverService.getAlbumCover(this,  CoverDimensions.x300);
		lyrics = GetLyricsService.getLyrics(artist, name);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the artist.
	 *
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * Gets the lyrics.
	 *
	 * @return the lyrics
	 */
	public String getLyrics() {
		return lyrics;
	}

	/**
	 * Gets the album name.
	 *
	 * @return the album name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Gets the cover.
	 *
	 * @return the cover
	 */
	public byte[] getCover() {
		return cover;
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Gets the frames.
	 *
	 * @return the frames
	 */
	public int getFrames() {
		return frames;
	}

	/**
	 * Gets the framerate.
	 *
	 * @return the framerate
	 */
	public float getFramerate() {
		return framerate;
	}

	/** 
	 * @return the song duration followed by the file name 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String wholeName =  file.getName();
		if(wholeName.endsWith(".mp3"))
			wholeName = wholeName.replace(".mp3", "");
		
		return Utils.getTime(length) + " " + wholeName;
	}
	
	

}
