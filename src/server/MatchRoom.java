package server;

import java.util.HashMap;
import java.util.Random;

public class MatchRoom {

    private Player waitingRandomPlayer;
    private HashMap<String, Player> waitingPlayerList;

    private final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public MatchRoom() {
        this.waitingPlayerList = new HashMap<String, Player>();
    }

    public void join(Player player, String[] args) {
        if (args.length < 2) {
            // Send fail response to client
        }
        String option = args[1];
        switch (option) {
            case "random":
                joinRandom(player);
                break;
            case "start":
                startWithKey(player);
                break;
            case "join":
                if (args.length == 3) {
                    joinFriend(player, args[2]);
                }
                break;
        }
    }

    /**
     * Either queues a player up to be paired with the next player who chooses
     * to join a random game, or pairs with a player waiting to play.
     * @param player
     */
    private synchronized void joinRandom(Player player) {
        if (waitingRandomPlayer == null) {
            waitingRandomPlayer = player; // there is no current waiting player
        } else {
            new Game(waitingRandomPlayer, player);
            waitingRandomPlayer = null; // next player to join has to wait
        }
    }

    /**
     * Puts a key and a player into a HashMap, they key is sent back to the
     * user, which is to be shared with a friend to start a game between them.
     * @param player
     */
    private synchronized void startWithKey(Player player) {
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        int length = ALPHABET.length();
        for (int i = 0; i < 10; ++i) {
            keyBuilder.append(ALPHABET.charAt(random.nextInt(length)));
        }
        String key = keyBuilder.toString();
        waitingPlayerList.put(key, player);
        player.writeMessage("Game token: " + key);
    }

    /**
     * Searches the HashMap for the key and starts a game between the newly
     * joined player and waiting player if found.
     * @param player
     * @param key
     */
    private synchronized void joinFriend(Player player, String key) {
        Player opponent = waitingPlayerList.remove(key);
        if (opponent != null) {
            new Game(opponent, player);
        } else {
            player.writeMessage("That game does not exist");
        }
    }

    /**
     * Removes a player from any queue.
     * @param player
     */
    public synchronized void removeWaitingPlayer(Player player) {
        if (player == waitingRandomPlayer) {
            waitingRandomPlayer = null;
        } else {
            waitingPlayerList.values().remove(player);
        }
    }

}