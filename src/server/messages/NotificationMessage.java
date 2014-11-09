package server.messages;

import java.io.Serializable;

/**
 * An Object sent from the server to a client containing a notification, and an
 * optional String array with additional information. The notifications are
 * constants represented by an int.
 */
public class NotificationMessage implements Serializable {

    public final static int PLACE_SHIPS = 101;
    public final static int YOUR_TURN = 102;
    public final static int OPPONENTS_TURN = 103;

    public final static int GAME_WIN = 201;
    public final static int GAME_LOSE = 202;
    public final static int TIMEOUT_WIN = 203;
    public final static int TIMEOUT_LOSE = 204;
    public final static int TIMEOUT_DRAW = 205;
    public final static int OPPONENT_DISCONNECTED = 206;

    public final static int OPPONENTS_NAME = 301;
    public final static int INVALID_NAME = 302;
    public final static int NAME_TAKEN = 303;
    public final static int NAME_ACCEPTED = 304;

    public final static int GAME_TOKEN = 401;
    public final static int GAME_NOT_FOUND = 402;
    public final static int CANNOT_PLAY_YOURSELF = 403;

    public final static int REPEATED_MOVE = 501;
    public final static int NOT_YOUR_TURN = 502;
    public final static int INVALID_MOVE = 503;
    public final static int INVALID_BOARD = 504;
    public final static int NOT_IN_GAME = 505;

    public final static int BOARD_ACCEPTED = 601;

    public final static int NEW_JOIN_GAME_REQUEST = 701;
    public final static int JOIN_GAME_REQUEST_REJECTED = 702;
    public final static int JOIN_GAME_REQUEST_ACCEPTED = 703;
    public final static int JOIN_GAME_REQUEST_CANCELLED = 704;

    private int code;
    private String[] text;

    /**
     * Constructs a NotificationMessage with the int value of a constant
     * representing the notification.
     *
     * @param code int value of constant
     */
    public NotificationMessage(int code) {
        this.code = code;
    }

    /**
     * Constructs a NotificationMessage with the int value of a constant
     * representing the notification, and a String array of any length, to be
     * sent as additional information.
     *
     * @param code int value of constant
     * @param text additional information
     */
    public NotificationMessage(int code, String... text) {
        this.code = code;
        this.text = text;
    }

    /**
     * Returns the int value of the notification the object is storing.
     *
     * @return int value of notification
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the String array of additional information.
     *
     * @return additional information
     */
    public String[] getText() {
        return text;
    }
}
