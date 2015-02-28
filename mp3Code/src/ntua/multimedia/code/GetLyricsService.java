package ntua.multimedia.code;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * The Class GetLyricsService.
 */
public class GetLyricsService {
	
	/**
	 * Gets the lyrics from wikia for the provided artist and song name.
	 *
	 * @param artist the artist
	 * @param song the song
	 * @return the lyrics
	 */
	protected static String getLyrics(String artist, String song) {
		if (artist == null || song == null) 
			return null;
		String lyrics = null;
		artist = artist.replaceAll(" ", "_");
		song = song.replaceAll(" ", "_");
		try {
			if(isInstrumental(artist, song))
				return "Instrumental";
			lyrics = retrieveAndDecode(artist, song);
		} catch (Exception e) {
			System.err.println("Error on retrieving lyrics");
		}
		return lyrics;
	}
	
	/**
	 * Checks if is instrumental.
	 *
	 * @param artist the artist
	 * @param song the song
	 * @return true, if is instrumental
	 * @throws Exception the exception
	 */
	private static boolean isInstrumental(String artist, String song) throws Exception{
		String url = "http://lyrics.wikia.com/api.php?func=getSong&artist="+artist+"&song="+song+"&fmt=xml";
		String htmlResponse = Utils.doHttpRequest(url);
		if(htmlResponse.contains("<lyrics>Instrumental</lyrics>"))
			return true;
		else 
			return false;

	}

	/**
	 * Retrieve and decode.
	 *
	 * @param artist the artist
	 * @param song the song
	 * @return the string
	 * @throws Exception the exception
	 */
	private static String retrieveAndDecode(String artist, String song) throws Exception {
		String lyrics = null;
		String url = "http://lyrics.wikia.com/" + artist + ":" + song;
		String htmlResponse = Utils.doHttpRequest(url);
		
		String[] splits = htmlResponse.split("insertBefore.r,s.};}}...;</script>");

		splits = splits[1].split("<!-- <p>");
		htmlResponse = splits[0].replaceAll("<br />", "\n");
		lyrics = StringEscapeUtils.unescapeHtml4(htmlResponse);
		
		return lyrics;
	}
	
}
