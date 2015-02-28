package ntua.multimedia.code;

/**
 * The Enum Modes.
 */
public enum Modes {

	/** The normal Mode. When there are no more songs on play list the player stops. */
	NORMAL,
	
	/** The repeat one Mode. Repeats the currently playing song. */
	REPEAT_ONE,
	
	/** The repeat all Mode. When there are no more songs on play list the players starts from the beginning. */
	REPEAT_ALL,
	
	/** The random Mode. A random order is followed instead of the order which songs inserted in the play list. */
	RANDOM;
}
