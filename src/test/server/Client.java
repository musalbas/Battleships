package test.server;

import logic.Board;
import logic.Ship;
import logic.Square;
import server.messages.MoveMessage;
import server.messages.MoveResponseMessage;
import server.messages.NotificationMessage;

import javax.sound.midi.Soundbank;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Client {

    public static void main(String[] args) {
        try {
            final Socket socket = new Socket("localhost", 8900);

            final ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            final ObjectInputStream in = new ObjectInputStream(
                    socket.getInputStream());

            final Board board = new Board(true);
            final Board opponentsBoard = new Board(false);
            ArrayList<Ship> ships = board.getShips();
            ships.get(0).setVertical(false);
            board.placeShip(ships.get(0), 0, 0);
            board.placeShip(ships.get(1), 0, 1);
            board.placeShip(ships.get(2), 1, 1);
            board.placeShip(ships.get(3), 3, 1);
            board.placeShip(ships.get(4), 4, 2);
            board.printBoard(true);
            board.placeShip(ships.get(4), 4, 4);

            board.printBoard(true);

            System.out.println(Board.isValid(board));

            final ArrayList<Integer[]> coordinates =
                    new ArrayList<>();
            for (int y = 0; y < 10; ++y) {
                for (int x = 0; x < 10; ++x) {
                    coordinates.add(new Integer[]{x, y});
                }
            }
            Collections.shuffle(coordinates);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object input;
                        while ((input = in.readObject()) != null) {
                            if (input instanceof NotificationMessage) {
                                NotificationMessage n =
                                        (NotificationMessage) input;

                                switch (n.getCode()) {
                                    case NotificationMessage.OPPONENTS_NAME:
                                        System.out.println("Opponent's name: " +
                                                n.getText()[0]);
                                        break;
                                    case NotificationMessage.PLACE_SHIPS:
                                        out.writeObject(board);
                                        out.flush();
                                        break;
                                    case NotificationMessage.YOUR_TURN:
                                        Integer[] xy = coordinates.remove(0);
                                        out.writeObject(new MoveMessage(
                                                xy[0], xy[1]));
                                        out.flush();
                                        break;
                                    case NotificationMessage.GAME_TOKEN:
                                        System.out.println("token: " +
                                                n.getText()[0]);
                                        break;
                                    case NotificationMessage.GAME_WIN:
                                        System.out.println("You win!");
                                        opponentsBoard.printBoard(true);
                                        break;
                                    case NotificationMessage.GAME_LOSE:
                                        System.out.println("You lost!");
                                        board.printBoard(true);
                                }
                            } else if (input instanceof MoveResponseMessage) {
                                MoveResponseMessage move =
                                        (MoveResponseMessage) input;
                                if (move.isOwnBoard()) {
                                    board.applyMove(move);
                                } else {
                                    opponentsBoard.applyMove(move);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            out.writeObject(new String[]{"name", args[0]});
            out.flush();
            if (args.length == 2) {
                out.writeObject(new String[]{"join", args[1]});
                out.flush();
            } else if (args.length == 3) {
                out.writeObject(new String[]{"join", args[1], args[2]});
                out.flush();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
