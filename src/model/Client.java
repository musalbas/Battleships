package model;

import server.Game;
import server.messages.ChatMessage;
import server.messages.MoveMessage;
import server.messages.MoveResponseMessage;
import server.messages.NotificationMessage;
import view.ClientView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread {

    private Board ownBoard;
    private Board opponentBoard;
    private ClientView view;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String opponentName = "Player";

    public Client(ClientView clientView, Board ownBoard, Board opponentBoard,
            ObjectOutputStream out, ObjectInputStream in) {
        this.ownBoard = ownBoard;
        this.opponentBoard = opponentBoard;
        this.view = clientView;

        // TODO: MAKE THIS SHIT BETTER
        ownBoard.setClient(this);
        opponentBoard.setClient(this);

        this.out = out;
        this.in = in;

        // start();
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

    public void parseInput(Object input) {
        if (input instanceof NotificationMessage) {
            NotificationMessage n = (NotificationMessage) input;
            switch (n.getCode()) {
            case NotificationMessage.OPPONENTS_NAME:
                // TODO: handle receiving opponents name
                view.addChatMessage("received opponents_name");
                if (n.getText().length == 1) {
                    opponentName = n.getText()[0];
                }
                break;
            case NotificationMessage.BOARD_ACCEPTED:
                // TODO: board is good, can start game
                view.setMessage("Board accepted. Waiting for opponent.");
                view.stopTimer();
                ownBoard.setBoatPositionLocked(true);
                break;
            case NotificationMessage.GAME_TOKEN:
                // TODO: handle receiving game token to share with friend
                view.addChatMessage("received game_Token");
                break;
            case NotificationMessage.GAME_NOT_FOUND:
                // TODO: handle joining a game that doesn't exist
                view.addChatMessage("game not found");
                break;
            case NotificationMessage.PLACE_SHIPS:
                // TODO: allow player to start positioning ships
                view.addChatMessage("can place ships now");
                ownBoard.setBoatPositionLocked(false);
                break;
            case NotificationMessage.YOUR_TURN:
                // TODO: inform player it's their turn and to make a move
                view.stopTimer();
                view.setTimer(Game.TURN_TIMEOUT / 1000);
                view.addChatMessage("YOUR TURN");
                break;
            case NotificationMessage.OPPONENTS_TURN:
                // TODO: informs player it is their opponent's turn
                view.stopTimer();
                view.setTimer(Game.TURN_TIMEOUT / 1000);
                view.addChatMessage("OPPONENTS_TURN");
                view.setMessage("Opponent's turn.");
                break;
            case NotificationMessage.GAME_WIN:
                // TODO: inform player they have won the game
                view.addChatMessage("GAME_WIN");
                view.gameOverAction();
                view.setMessage("Your turn.");
                break;
            case NotificationMessage.GAME_LOSE:
                // TODO: inform player they have lost the game
                view.addChatMessage("GAME_LOSE");
                view.gameOverAction();
                break;
            case NotificationMessage.TIMEOUT_WIN:
                // TODO: inform of win due to opponent taking too long
                view.addChatMessage("TIMEOUT_WIN");
                view.gameOverAction();
                break;
            case NotificationMessage.TIMEOUT_LOSE:
                // TODO: inform of loss due to taking too long
                view.addChatMessage("TIMEOUT_LOSE");
                view.gameOverAction();
                break;
            case NotificationMessage.TIMEOUT_DRAW:
                // TODO: inform that both took too long to place ships
                view.addChatMessage("TIMEOUT_DRAW");
                view.gameOverAction();
                break;
            case NotificationMessage.NOT_YOUR_TURN:
                view.addChatMessage("NOT_YOUR_TURN");
                break;
            case NotificationMessage.INVALID_BOARD:
                view.addChatMessage("INVALID_BOARD");
                break;
            case NotificationMessage.NOT_IN_GAME:
                view.addChatMessage("NOT_IN_GAME");
                break;
            case NotificationMessage.INVALID_MOVE:
                view.addChatMessage("INVALID_MOVE");
                break;
            case NotificationMessage.REPEATED_MOVE:
                view.addChatMessage("REPEATED_MOVE");
                break;
            case NotificationMessage.OPPONENT_DISCONNECTED:
                view.addChatMessage("OPPONENT_DISCONNECTED");
            }
        } else if (input instanceof MoveResponseMessage) {
            MoveResponseMessage move = (MoveResponseMessage) input;
            if (move.isOwnBoard()) {
                ownBoard.applyMove(move);
            } else {
                opponentBoard.applyMove(move);
            }
        } else if (input instanceof ChatMessage) {
            view.addChatMessage(((ChatMessage) input).getMessage());
        }
    }

    public void sendBoard(Board board) throws IOException {
        out.reset();
        out.writeObject(board);
        out.flush();
    }

    public ClientView getView() {
        return view;
    }

    public void sendChatMessage(String message) throws IOException {
        System.out.println(message);
        out.writeObject(new ChatMessage(message));
        out.flush();
    }

    public void sendMove(int x, int y) throws IOException {
        out.writeObject(new MoveMessage(x, y));
        out.flush();
    }

    public String getOpponentName() {
        return opponentName;
    }

}