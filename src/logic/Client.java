package logic;

import client.ClientView;
import server.messages.MoveMessage;
import server.messages.MoveResponseMessage;
import server.messages.NotificationMessage;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private Board ownBoard;
    private Board opponentBoard;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(ClientView clientView, Board ownBoard, Board opponentBoard) {
        this.ownBoard = ownBoard;
        this.opponentBoard = opponentBoard;

        try {
            Socket socket = new Socket("localhost", 8900);
            out = new ObjectOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    @Override
    public void run() {
        super.run();
        Object input;
        try {
            while ((input = in.readObject()) != null) {
                parseInput(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void parseInput(Object input) {
        if (input instanceof NotificationMessage) {

        } else if (input instanceof MoveResponseMessage) {
            MoveResponseMessage move = (MoveResponseMessage) input;
            if (move.isOwnBoard()) {
                ownBoard.applyMove(move);
            } else {
                opponentBoard.applyMove(move);
            }
        }
    }

    public void sendMove(int x, int y) throws IOException {
        out.writeObject(new MoveMessage(x, y));
        out.flush();
    }
}