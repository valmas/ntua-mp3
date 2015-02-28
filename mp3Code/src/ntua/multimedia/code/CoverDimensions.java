package ntua.multimedia.code;

/**
 * The Enum CoverDimensions.
 */
public enum CoverDimensions {

	/** The 640x640 dimension. */
	x640(0),
	
	/** The 300x300 dimension (currently used). */
	x300(1),
	
	/** The 64x64 dimension. */
	x64(2);
	
	/** The type. */
	public int type;
	
	/**
	 * Instantiates a new cover dimensions.
	 *
	 * @param type the type
	 */
	private CoverDimensions(int type){
		this.type = type;
	}
}
