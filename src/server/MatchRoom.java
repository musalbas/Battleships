package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import server.messages.MatchRoomListMessage;
import server.messages.NotificationMessage;

public class MatchRoom {

    private final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private Player waitingRandomPlayer;
    private HashMap<String, Player> waitingPlayerList;

    public MatchRoom() {
        this.waitingPlayerList = new HashMap<String, Player>();
    }

    public void join(Player player, String[] args) {
        if (args.length < 2) {
            // Send fail response to view
        }
        String option = args[1];
        switch (option) {
        case "random":
            player.leaveGame();
            joinRandom(player);
            break;
        case "start":
            startWithKey(player);
            break;
        case "join":
            player.leaveGame();
            if (args.length == 3) {
                player.leaveGame();
                joinRequest(player, args[2]);
            }
            break;
        case "accept":
            player.leaveGame();
            if (args.length == 3) {
                acceptRequest(player, args[2]);
            }
            break;
        case "reject":
            if (args.length == 3) {
                rejectRequest(player, args[2]);
            }
        case "cancel":
            if (args.length == 2) {
                cancelRequest(player);
            }
        }
    }

    /**
     * Either queues a player up to be paired with the next player who chooses
     * to join a random game, or pairs with a player waiting to play.
     * 
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
     * 
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
        player.setOwnKey(key);
        waitingPlayerList.put(key, player);
        player.writeNotification(NotificationMessage.GAME_TOKEN, key);
        sendMatchRoomList();
    }

    /**
     * Searches the HashMap for the key and starts a game between the newly
     * joined player and waiting player if found.
     * 
     * @param player
     * @param key
     */
    private synchronized void joinFriend(Player player, String key) {
        Player opponent = waitingPlayerList.remove(key);
        if (player == opponent) {
            player.writeNotification(NotificationMessage.CANNOT_PLAY_YOURSELF);
            waitingPlayerList.put(key, opponent);
            return;
        }
        if (opponent != null) {
            waitingPlayerList.values().remove(player);
            new Game(opponent, player);
            sendMatchRoomList();
        } else {
            player.writeMessage("That game does not exist");
        }
    }

    private synchronized void joinRequest(Player player, String key) {
        Player opponent = waitingPlayerList.get(key);
        if (player == opponent) {
            player.writeNotification(NotificationMessage.CANNOT_PLAY_YOURSELF);
        } else if (opponent != null) {
            opponent.sendRequest(player);
        }
    }

    private synchronized void acceptRequest(Player player, String key) {
        Player opponent = waitingPlayerList.remove(key);
        if (opponent != null) {
            waitingPlayerList.values().remove(player);
            opponent.requestAccepted(player);
            new Game(opponent, player);
            sendMatchRoomList();
            player.rejectAll();
            opponent.rejectAll();
        }
    }

    private synchronized void rejectRequest(Player player, String key) {
        Player opponent = waitingPlayerList.get(key);
        if (opponent != null) {
            opponent.requestRejected(player);
        }
    }

    private synchronized void cancelRequest(Player player) {
        Player opponent = waitingPlayerList.get(player.getRequestedGameKey());
        player.setRequestedGameKey(null);
        if (opponent != null) {
            opponent.writeNotification(
                    NotificationMessage.JOIN_GAME_REQUEST_CANCELLED,
                    player.getOwnKey());
        }
    }

    /**
     * Removes a player from any queue.
     * 
     * @param player
     */
    public synchronized void removeWaitingPlayer(Player player) {
        if (player == waitingRandomPlayer) {
            waitingRandomPlayer = null;
        } else {
            waitingPlayerList.values().remove(player);
            sendMatchRoomList();
        }
    }

    public boolean playerNameExists(String name) {
        boolean exists = false;
        for (Map.Entry<String, Player> entry : waitingPlayerList.entrySet()) {
            if (entry.getValue().equals(name)) {
                return true;
            }
        }
        return exists;
    }

    public synchronized void sendMatchRoomList() {
        HashMap<String, String> matchRoomList = new HashMap<String, String>();
        for (Map.Entry<String, Player> entry : waitingPlayerList.entrySet()) {
            String key = entry.getKey();
            Player player = entry.getValue();
            System.out.println(player.getPlayerName());
            matchRoomList.put(key, player.getPlayerName());
        }
        MatchRoomListMessage message = new MatchRoomListMessage(matchRoomList);
        for (Map.Entry<String, Player> entry : waitingPlayerList.entrySet()) {
            Player player = entry.getValue();
            player.writeObject(message);
        }
    }

}
