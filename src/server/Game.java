package server;

public class Game {

    private Player player1;
    private Player player2;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1.setGame(this);
        player2.setGame(this);
        player1.writeMessage("Joined game with " + player2.getPlayerName());
        player2.writeMessage("Joined game with " + player1.getPlayerName());
    }

    public Player getOpponent(Player self) {
        if (player1 == self) {
            return player2;
        }
        return player1;
    }

    public void killGame(Player player) {
        player1.setGame(null);
        player2.setGame(null);
        getOpponent(player).writeMessage("Lost connection to opponent");
    }

}
