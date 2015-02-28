package ntua.multimedia.code;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GetAlbumCoverService.
 */
public class GetAlbumCoverService {

	
	/**
	 * Gets the album cover.
	 *
	 * @param track the track for which the cover will be retrieved
	 * @param dimension the dimension of the cover (64x64, 300x300, 640x640)
	 * @return the album cover in bytes
	 */
	protected static byte[] getAlbumCover(Track track, CoverDimensions dimension) {
		byte[] cover = null;
		if(track.getAlbumName() != null) {
			String artWork = null;
			try {
				String song = track.getName().replace(" ", "+");
				String url = "https://api.spotify.com/v1/search?query="+song+"&offset=0&limit=20&type=track" ;
				artWork = Utils.doHttpRequest(url);
			} catch (Exception e) {
				System.err.println("Error on searching for album cover");
				return null;
			}
			List<String> urls = parseArtwork(artWork, track);
			try {
				cover = getCoverInBytes(urls.get(dimension.type));
			} catch (Exception e) {
				System.err.println("Error on downloading cover");
			}
		}
		return cover;
	}
	
	/**
	 * Parses the artwork.
	 *
	 * @param artWork the art work
	 * @param track the track
	 * @return the list
	 */
	private static List<String> parseArtwork(String artWork, Track track){
		String[] albums = artWork.split("\"album\" : "); 
		for(int i = 1; i<albums.length; i++){
			if(albums[i].contains("\"name\" : \""+track.getArtist()) && albums[i].contains("\"name\" : \""+track.getAlbumName())){
				return extractArtWorkURL(albums[i]);
			}
		}
		//May bring an incorrect cover
		for(int i = 1; i<albums.length; i++){
			if(albums[i].contains("\"name\" : \""+track.getArtist())){
				return extractArtWorkURL(albums[i]);
			}
		}
		return null;
	}
	
	/**
	 * Extract art work url.
	 *
	 * @param fragment the fragment
	 * @return the list
	 */
	private static List<String> extractArtWorkURL(String fragment){
		String[] fragments = fragment.split("\"");
		List<String> urls = new ArrayList<String>();
		for(int i=0; i<fragments.length; i++){
			if(fragments[i].equals("url"))
				urls.add(fragments[i+2]);
		}
		return urls;
	}
	
	/**
	 * Gets the cover in bytes.
	 *
	 * @param url the url
	 * @return the cover in bytes
	 * @throws Exception the exception
	 */
	private static byte[] getCoverInBytes(String url) throws Exception{
		URL obj = new URL(url);
 
		InputStream is = obj.openStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}
	
}