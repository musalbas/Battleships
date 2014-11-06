package test.server;

import model.Board;
import server.messages.MoveMessage;
import server.messages.MoveResponseMessage;
import server.messages.NotificationMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Client {

    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                System.out.println("Syntax: <name> <test board no.> "
                        + "<join mode> [join token]");
            }
            final Socket socket = new Socket("localhost", 8900);

            final ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            final ObjectInputStream in = new ObjectInputStream(
                    socket.getInputStream());

            final Board board = TestBoards.getTestBoard(Integer
                    .parseInt(args[1]));
            final Board opponentsBoard = new Board(false);
            board.printBoard(true);
            System.out.println("Board valid: " + Board.isValid(board));

            final ArrayList<Integer[]> coordinates = new ArrayList<>();
            for (int y = 0; y < 10; ++y) {
                for (int x = 0; x < 10; ++x) {
                    coordinates.add(new Integer[] { x, y });
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
                                NotificationMessage n = (NotificationMessage) input;

                                switch (n.getCode()) {
                                case NotificationMessage.OPPONENTS_NAME:
                                    System.out.println("Opponent's name: "
                                            + n.getText()[0]);
                                    break;
                                case NotificationMessage.PLACE_SHIPS:
                                    out.writeObject(board);
                                    out.flush();
                                    break;
                                case NotificationMessage.YOUR_TURN:
                                    // Integer[] xy = coordinates.remove(0);
                                    // out.writeObject(new MoveMessage(
                                    // xy[0], xy[1]));
                                    // out.flush();
                                    System.out.println("Your turn: ");
                                    break;
                                case NotificationMessage.GAME_TOKEN:
                                    System.out.println("token: "
                                            + n.getText()[0]);
                                    break;
                                case NotificationMessage.GAME_WIN:
                                case NotificationMessage.GAME_LOSE:
                                    if (n.getCode() == NotificationMessage.GAME_WIN) {
                                        System.out.println("You win!");
                                    } else {
                                        System.out.println("You lose!");
                                    }
                                    System.out.println("Your board's hits:");
                                    board.printBoard(false);
                                    System.out
                                            .println("Hits on opponent's board:");
                                    opponentsBoard.printBoard(false);
                                    System.out.println("Discovered "
                                            + "opponents Board:");
                                    opponentsBoard.printBoard(true);
                                    break;
                                case NotificationMessage.TIMEOUT_WIN:
                                    System.out.println("Opponent took "
                                            + "too long");
                                    break;
                                case NotificationMessage.TIMEOUT_LOSE:
                                    System.out.printf("You took too long");
                                    break;
                                case NotificationMessage.TIMEOUT_DRAW:
                                    System.out.println("Game ends a draw");
                                }
                            } else if (input instanceof MoveResponseMessage) {
                                MoveResponseMessage move = (MoveResponseMessage) input;
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

            out.writeObject(new String[] { "name", args[0] });
            out.flush();
            if (args.length == 3) {
                out.writeObject(new String[] { "join", args[2] });
                out.flush();
            } else if (args.length == 4) {
                out.writeObject(new String[] { "join", args[2], args[3] });
                out.flush();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader stdIn = new BufferedReader(
                            new InputStreamReader(System.in));
                    String input;
                    try {
                        while ((input = stdIn.readLine()) != null) {
                            String[] xy = input.split(" ");
                            out.writeObject(new MoveMessage(Integer
                                    .parseInt(xy[0]), Integer.parseInt(xy[1])));
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
