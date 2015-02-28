package ntua.multimedia.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Class Utils.
 */
public class Utils {
	
	/** The Constant MP3_MEDIA_TYPE. */
	private static final String MP3_MEDIA_TYPE = "audio/mpeg";
	
	/** The Constant EMPTY_NAME. */
	private static final String EMPTY_NAME = "Unknown";

	/**
	 * Gets the time. 
	 * Converts the song length from milliseconds to MM:SS format
	 *
	 * @param milliseconds the milliseconds
	 * @return the time
	 */
	protected static String getTime(int milliseconds){
		int seconds = milliseconds / 1000;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		if (seconds < 10)
			return minutes + ":0" + seconds;
		else
			return minutes + ":" + seconds;
	}
	
	/**
	 * Gets the time from frames.
	 *
	 * @param currentFrames the current frames
	 * @param frameRate the frame rate
	 * @return the time from frames
	 */
	public static String getTimeFromFrames(int currentFrames, float frameRate){
		int miliseconds = (int) (currentFrames*1000/frameRate);
		return getTime(miliseconds);
	}
	
	/**
	 * Parses a value.
	 *
	 * @param value the value
	 * @return the string value if is not null else returns the string Unknown
	 */
	public static String parseValue(String value){
		return value == null ? EMPTY_NAME : value;
	}
	
	/**
	 * Checks if is mp3.
	 *
	 * @param file the file to be checked
	 * @return true, if the file is a valid mp3
	 */
	public static boolean isMP3(File file){
		try {
			Path filepath = Paths.get(file.getAbsolutePath());
			String contentType = Files.probeContentType(filepath);
			if(contentType.equals(MP3_MEDIA_TYPE))
				return true;
		} catch (IOException e) {
			System.err.println("Error while detecting media type");
		}
		return false;
	}
	
	/**
	 * Do http request.
	 *
	 * @param url the target url
	 * @return the html response as string
	 * @throws Exception 
	 */
	protected static String doHttpRequest(String url) throws Exception{
		URL obj = new URL(url);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(obj.openStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response.toString();
	}
}
