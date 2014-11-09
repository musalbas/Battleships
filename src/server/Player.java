package server;

import model.Board;
import server.messages.ChatMessage;
import server.messages.MoveMessage;
import server.messages.NotificationMessage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Thread {

    private Socket socket;
    private MatchRoom matchRoom;
    private String name = "";
    private ObjectOutputStream out;
    private Game game;
    private Board board;
    private HashMap<String, Player> requestList;
    private String ownKey;
    private String requestedGameKey;

    /**
     * Constructs a player with a socket to connect through, and a reference
     * to the match room.
     *
     * @param socket the socket connecting to the player
     * @param matchRoom the match room the player will be placed in
     */
    public Player(Socket socket, MatchRoom matchRoom) {
        this.socket = socket;
        this.matchRoom = matchRoom;
        matchRoom.assignKey(this);
        matchRoom.addPlayer(this);
        this.requestList = new HashMap<>();
        System.out.println(socket.getRemoteSocketAddress().toString() +
                " connected");
    }

    /**
     * Listens to input from the client.
     */
    @Override
    public void run() {
        super.run();
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(
                    socket.getOutputStream()));
            out.flush();
            ObjectInputStream in = new ObjectInputStream(
                    socket.getInputStream());

            Object input;

            while ((input = in.readObject()) != null) {
                if (input instanceof String[]) {
                    String[] array = (String[]) input;
                    int length = array.length;

                    if (length > 0) {
                        String message = array[0];

                        switch (message) {
                        case "join":
                            matchRoom.parse(this, array);
                            break;
                        case "name":
                            if (length != 2 || array[1] == null ||
                                    array[1].equals("")) {
                                writeNotification(NotificationMessage.INVALID_NAME);
                            } else if (matchRoom.playerNameExists(array[1])) {
                                writeNotification(NotificationMessage.NAME_TAKEN);
                            } else {
                                name = array[1];
                                writeNotification(NotificationMessage.NAME_ACCEPTED);
                                matchRoom.sendMatchRoomList();
                            }
                            break;
                        }
                    }
                } else if (input instanceof Board) {
                    Board board = (Board) input;
                    if (Board.isValid(board) && game != null) {
                        writeNotification(NotificationMessage.BOARD_ACCEPTED);
                        this.board = board;
                        game.checkBoards();
                    } else if (game == null) {
                        writeNotification(NotificationMessage.NOT_IN_GAME);
                    } else {
                        writeNotification(NotificationMessage.INVALID_BOARD);
                    }
                } else if (input instanceof MoveMessage) {
                    if (game != null) {
                        game.applyMove((MoveMessage) input, this);
                    }
                } else if (input instanceof ChatMessage) {
                    if (game != null) {
                        Player opponent = game.getOpponent(this);
                        if (opponent != null) {
                            opponent.writeObject(input);
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (game != null) {
                leaveGame();
            } else {
                matchRoom.removeWaitingPlayer(this);
            }
            matchRoom.removePlayer(this);
            System.out.println(socket.getRemoteSocketAddress().toString() +
                    " connected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the Game the player is in.
     *
     * @param game the game the player is in
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Gets the name that the player has chosen to assign to themselves.
     *
     * @return the name of the player
     */
    public String getPlayerName() {
        return name;
    }

    /**
     * Writes a String to the player, and flushes it.
     *
     * @param message the message to be sent
     */
    public void writeMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an Object to the player, and flushes it.
     *
     * @param object the Object to be sent
     */
    public void writeObject(Object object) {
        try {
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a notification to the player, with an optional String array, and
     * flushes it.
     *
     * @see server.messages.NotificationMessage
     * @param notificationMessage the notification message constant to send
     * @param text additional information to be sent as a String array
     */
    public void writeNotification(int notificationMessage, String... text) {
        try {
            NotificationMessage nm = new NotificationMessage(
                    notificationMessage, text);
            out.writeObject(nm);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the board that the player has uploaded to the server.
     *
     * @return the player's board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Sends a game request to the player, and updates the request list and the
     * requested game key of the opponent.
     *
     * @param requester the player who sent the request
     */
    public synchronized void sendRequest(Player requester) {
        requestList.put(requester.getOwnKey(), requester);
        requester.requestedGameKey = this.ownKey;
        writeNotification(NotificationMessage.NEW_JOIN_GAME_REQUEST,
                requester.getOwnKey(), requester.getPlayerName());
    }

    /**
     * Called when the opponent accepts a request and informs the player they
     * have done so.
     *
     * @param opponent the player who accepted the request
     */
    public synchronized void requestAccepted(Player opponent) {
        opponent.requestList.remove(ownKey);
        requestedGameKey = null;
        writeNotification(NotificationMessage.JOIN_GAME_REQUEST_ACCEPTED);
    }

    /**
     * Called when the opponent rejects a request and informs the player they
     * have done so.
     * @param opponent the player who rejected the request
     */
    public synchronized void requestRejected(Player opponent) {
        opponent.requestList.remove(ownKey);
        requestedGameKey = null;
        writeNotification(NotificationMessage.JOIN_GAME_REQUEST_REJECTED);
    }

    /**
     * Sets player's own unique key, used to identify them when sending and
     * receiving game requests.
     *
     * @param ownKey the player's unique key
     */
    public void setOwnKey(String ownKey) {
        this.ownKey = ownKey;
    }

    /**
     * Gets the unique key of the player.
     *
     * @return the player's unique key
     */
    public String getOwnKey() {
        return ownKey;
    }

    /**
     * Sets the requested game key to the unique key of the player an invite was
     * sent to.
     *
     * @param key key of invited player
     */
    public void setRequestedGameKey(String key) {
        this.requestedGameKey = key;
    }

    /**
     * Gets the unique key of a player that the player sent a game invite to.
     *
     * @return key of invited player
     */
    public String getRequestedGameKey() {
        return requestedGameKey;
    }

    /**
     * Rejects a game invite from every player who has invited this player.
     */
    public void rejectAll() {
        for (Player p : requestList.values()) {
            p.requestRejected(this);
        }
    }

    /**
     * Ends a game and notifies the opponent the player has left.
     */
    public void leaveGame() {
        if (game != null) {
            Player opponent = game.getOpponent(this);
            opponent.writeNotification(NotificationMessage.OPPONENT_DISCONNECTED);
            game.killGame();
        }
    }

}
