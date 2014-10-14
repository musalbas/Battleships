package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player extends Thread {

    private Socket socket;
    private MatchRoom matchRoom;
    private String name;
    private ObjectOutputStream out;
    private Game game;

    public Player(Socket socket, MatchRoom matchRoom) {
        this.socket = socket;
        this.matchRoom = matchRoom;
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

                    if (length > 0 ) {
                        String message = array[0];

                        switch (message) {
                            case "join":
                                if (game == null) {
                                    matchRoom.join(this, array);
                                }
                                break;
                            case "name":
                                if (length == 2) {
                                    name = array[1];
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (game != null) {
                game.killGame(this);
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
     * Writes a String to the client.
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

}
