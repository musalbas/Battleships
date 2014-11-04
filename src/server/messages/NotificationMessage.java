package server.messages;

import java.io.Serializable;

public class NotificationMessage extends ServerMessage implements Serializable {

    public final static int PLACE_SHIPS = 101;
    public final static int YOUR_TURN = 102;
    public final static int OPPONENTS_TURN = 103;

    public final static int GAME_WIN = 201;
    public final static int GAME_LOSE = 202;
    public final static int TIMEOUT_WIN = 203;
    public final static int TIMEOUT_LOSE = 204;
    public final static int TIMEOUT_DRAW = 205;

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

    private int code;
    private String[] text;

    public NotificationMessage(int code) {
        this.code = code;
    }

    public NotificationMessage(int code, String... text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String[] getText() {
        return text;
    }
}
