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
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends Thread {

    private Socket socket;
    private MatchRoom matchRoom;
    private String name = "(new player)";
    private ObjectOutputStream out;
    private Game game;
    private Board board;
    private HashMap<String, Player> requestList;
    private String ownKey;
    private String requestedGameKey;
    private Timer requestTimer;

    public Player(Socket socket, MatchRoom matchRoom) {
        this.socket = socket;
        this.matchRoom = matchRoom;
        this.requestList = new HashMap<>();
    }

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
                            if (game != null) {
                                // TODO: check if game over
                                game.killGame();
                                game = null;
                            }
                            matchRoom.join(this, array);
                            break;
                        case "name":
                            if (length != 2 || array[1].equals("")) {
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
                game.killGame();
                // TODO: Alert other player they win
            } else {
                matchRoom.removeWaitingPlayer(this);
            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getPlayerName() {
        return name;
    }

    /**
     * Writes a String to the view.
     * 
     * @param message
     */
    public void writeMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(Object object) {
        try {
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public Board getBoard() {
        return this.board;
    }

    public synchronized void sendRequest(Player requester) {
        requestList.put(requester.getOwnKey(), requester);
        requester.requestedGameKey = this.ownKey;
        requester.startTimer(this);
        writeNotification(NotificationMessage.NEW_JOIN_GAME_REQUEST,
                requester.getOwnKey(), requester.getPlayerName());
    }

    /**
     * Called when the opponent accepts a request.
     * @param opponent the player who accepted the request
     */
    public synchronized void requestAccepted(Player opponent) {
        cancelTimer();
        opponent.requestList.remove(ownKey);
        requestedGameKey = null;
        writeNotification(NotificationMessage.JOIN_GAME_REQUEST_ACCEPTED);
    }

    /**
     * Called when the opponent rejects a request.
     * @param opponent the player who rejected the request
     */
    public synchronized void requestRejected(Player opponent) {
        cancelTimer();
        opponent.requestList.remove(ownKey);
        requestedGameKey = null;
        writeNotification(NotificationMessage.JOIN_GAME_REQUEST_REJECTED);
    }

    public void setOwnKey(String ownKey) {
        this.ownKey = ownKey;
    }

    public String getOwnKey() {
        return ownKey;
    }

    /**
     * Starts a timer in the context of the player who sent a game request,
     * to automatically assume the request was rejected when the timeout is
     * reached and notify the opponent the request is no longer active.
     * @param opponent the invited player
     */
    public void startTimer(final Player opponent) {
        requestTimer = new Timer();
        requestTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                opponent.writeNotification(
                        NotificationMessage.JOIN_GAME_REQUEST_CANCELLED,
                        ownKey);
                requestRejected(opponent);
            }
        }, 30000);
    }

    private void cancelTimer() {
        if (requestTimer != null) {
            requestTimer.cancel();
            System.out.println("timer cancelled");
            requestTimer = null;
        }
    }

    public void setRequestedGameKey(String key) {
        this.requestedGameKey = key;
    }

    public String getRequestedGameKey() {
        return requestedGameKey;
    }

    public void rejectAll() {
        for (Player p : requestList.values()) {
            requestRejected(p);
        }
    }

}
