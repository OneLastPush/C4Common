package networking;

/**
 * Enum used for packet messages.
 * 
 * @author Julien Comtois, Frank Birikundavyi, Marjorie Olano Morales
 * @version 11/2/2015
 */
public enum C4Msg {
	START_GAME,
	PLAY_AGAIN,
	BAD_MOVE,
	CLIENT_MOVE,
	SERVER_MOVE,
	GAME_WON_AI,
	GAME_WON_CLIENT,
	GAME_ENDED_TIE,
	CLOSE_CONNECTION
}
