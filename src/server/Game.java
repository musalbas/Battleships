package server;

import logic.Ship;
import logic.Square;
import server.messages.MoveMessage;
import server.messages.MoveResponseMessage;
import server.messages.NotificationMessage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private Player player1;
    private Player player2;
    private Player turn;

    private Timer placementTimer;
    private Timer turnTimer;

    private boolean gameStarted;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.setGame(this);
        player2.setGame(this);
        player1.writeObject(new NotificationMessage(
                NotificationMessage.OPPONENTS_NAME, player2.getPlayerName()));
        player2.writeObject(new NotificationMessage(
                NotificationMessage.OPPONENTS_NAME, player1.getPlayerName()));
        NotificationMessage placeShipsMessage =
                new NotificationMessage(NotificationMessage.PLACE_SHIPS);
        player1.writeObject(placeShipsMessage);
        player2.writeObject(placeShipsMessage);

        placementTimer = new Timer();
        placementTimer.schedule(new PlacementTimerTask(), 60000);
    }

    public Player getOpponent(Player self) {
        if (player1 == self) {
            return player2;
        }
        return player1;
    }

    public void killGame() {
        player1.setGame(null);
        player2.setGame(null);
    }

    public synchronized void setTurn(Player player) {
        turn = player;
        if (turnTimer != null) {
            turnTimer.cancel();
        }
        turnTimer = new Timer();
        turnTimer.schedule(new TurnTimerTask(), 20000);
        turn.writeObject(new NotificationMessage(
                NotificationMessage.YOUR_TURN));
        getOpponent(turn).writeObject(new NotificationMessage(
                NotificationMessage.OPPONENTS_TURN));
    }

    public void checkBoards() {
        if (player1.getBoard() != null && player2.getBoard() != null) {
            placementTimer.cancel();
            startGame();
        }
    }

    private void startGame() {
        System.out.println("Game started");
        gameStarted = true;
        if (new Random().nextInt(1) == 0) {
            setTurn(player1);
        } else {
            setTurn(player2);
        }
    }

    public synchronized void applyMove(MoveMessage move, Player player) {
        int x = move.getX();
        int y = move.getY();
        if (player != turn) {
            // Throw error
            return;
        } else {
            Player opponent = getOpponent(player);
            Square square = opponent.getBoard().getSquare(x, y);
            if (square.isGuessed()) {
                // Throw move already done error
                return;
            }
            boolean hit = square.guess();
            Ship ship = square.getShip();
            MoveResponseMessage response;
            if (ship != null && ship.isSunk()) {
                response = new MoveResponseMessage(x, y, ship, true, false);
                System.out.println(x + ", " + y);
            } else {
                response = new MoveResponseMessage(x, y, null, hit, false);
            }
            player.writeObject(response);
            response.setOwnBoard(true);
            opponent.writeObject(response);
            if (opponent.getBoard().gameOver()) {
                opponent.getBoard().printBoard(false);
                turn.writeObject(new NotificationMessage(
                        NotificationMessage.GAME_WIN));
                opponent.writeObject(new NotificationMessage(
                        NotificationMessage.GAME_LOSE));
                turn = null;
                // player wins
            } else if (hit) {
                setTurn(player); // player gets another go if hit
            } else {
                setTurn(getOpponent(player));
            }
        }
    }

    private class PlacementTimerTask extends TimerTask {

        @Override
        public void run() {
            if (player1.getBoard() == null & player2.getBoard() == null) {
                // Both clients failed to place ships in time
            } else if (player1.getBoard() == null) {
                // Player1 failed to place ships in time
            } else if (player2.getBoard() == null) {
                // Player2 failed to place ships in time
            }
        }
    }

    private class TurnTimerTask extends TimerTask {

        @Override
        public void run() {
            if (turn != null) {

            }
        }
    }

}
