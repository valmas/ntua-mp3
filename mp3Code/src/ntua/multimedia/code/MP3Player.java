package ntua.multimedia.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.DefaultListModel;

import javazoom.jl.decoder.JavaLayerException;

/**
 * The Class MP3Player.
 */
public class MP3Player {

	/** The player. */
	private static Player player = null;
	
	/** The play list. */
	private DefaultListModel<Track> playList;
	
	/** The status. */
	private Status status;
	
	/** The mode. */
	private Modes mode = Modes.NORMAL;
	
	/**
	 * Instantiates a new mp3 player.
	 */
	public MP3Player(){
		playList = new DefaultListModel<Track>();
		status = Status.NULL;
	}
	

	/**
	 * Inits the player with the selected track.
	 *
	 * @param track the song track
	 */
	public void init(Track track){
		try{
			player = new Player(new FileInputStream(track.getFile()));
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + track.getFile().getAbsolutePath());
			e.printStackTrace();
			System.exit(1);
		} catch (JavaLayerException e) {
			System.err.println("Error on creating player");
			e.printStackTrace();
			System.exit(1);
		}
		status = Status.PLAYING;
	}
	
	/**
	 * Plays the selected track.
	 *
	 * @param track the track to be played
	 */
	public void play(Track track) {
		try{
			player.play(track.getFrames());
		} catch (JavaLayerException e) {
			System.err.println("Error while playing");
			e.printStackTrace();
			System.exit(1);
		}
		if(status != Status.STOPPED)
			status = Status.NULL;
	}
	
	/**
	 * Action resume from pause.
	 */
	public void resume(){
		if(status == Status.PAUSED) {
			player.resume();
			status = Status.PLAYING;
		}
	}
	
	/**
	 * Action stop.
	 */
	public void stop(){
		if (status == Status.PLAYING || status == Status.PAUSED){
			player.stop();
			status = Status.STOPPED;
		}
	}
	
	/**
	 * Action pause.
	 */
	public void pause(){
		if (status == Status.PLAYING){
			player.pause();
			status = Status.PAUSED;
		}
		
	}
	
	/**
	 * Action fast forward.
	 */
	public void fastForward() {
		if (status == Status.PLAYING){
			player.setFastForward(true);
		} else if (status == Status.PAUSED){
			try {
				player.fastForward();
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the selected track.
	 * Stop playing the selected track and removes it from play list.
	 *
	 * @param track the track to be closed
	 */
	public void close(Track track) {
		if (status == Status.PLAYING || status == Status.PAUSED){
			player.stop();
		}
		status = Status.NULL;
		removeFromPlayList(track);
	}
	
	/**
	 * Initializes a track and adds it to the play list.
	 *
	 * @param file the file of the mp3
	 */
	public void addToPlayList(File file){
		Track track = new Track(file);
		playList.addElement(track);
	}
	
	/**
	 * Removes the selected track from play list.
	 *
	 * @param track the track to be removed
	 */
	protected void removeFromPlayList(Track track){
		playList.removeElement(track);
	}

	/**
	 * Gets the play list.
	 *
	 * @return the play list
	 */
	public DefaultListModel<Track> getPlayList() {
		return playList;
	}
	
	/**
	 * Gets the position in frames.
	 *
	 * @return the position in frames or -1 if the song ends
	 */
	public int getPosition(){
		if(status == Status.PLAYING || status == Status.PAUSED)
			return player.getPosition();
		else
			return -1;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * Gets the next track to be played.
	 *
	 * @param currentTrack the current track
	 * @return the next track to be played
	 */
	public Track getNext(Track currentTrack){
		if(status == Status.STOPPED)
			return currentTrack;
		int index = playList.indexOf(currentTrack);
		switch (mode){
		case NORMAL:
			if(index + 1 < playList.size())
				return playList.get(index+1);
			else
				return null;
		case RANDOM:
			int random = (int) (Math.random()*(playList.size()) - 1) + 1;
			return playList.get((index + random) % playList.size());
		case REPEAT_ALL:
			return playList.get((index+1) % playList.size());
		case REPEAT_ONE:
			return currentTrack;
		}
		return null;
	}
	

	/**
	 * Gets the index.
	 *
	 * @param currentTrack the current track
	 * @return the index
	 */
	public int getIndex(Track currentTrack){
		return playList.indexOf(currentTrack);
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public Modes getMode() {
		return mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	public void setMode(Modes mode) {
		this.mode = mode;
	}
	
}
