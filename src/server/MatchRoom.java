package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import server.messages.MatchRoomListMessage;
import server.messages.NotificationMessage;

public class MatchRoom {

    private final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private HashMap<String, Player> waitingPlayerList;
    private ArrayList<Player> connectedPlayers;

    /**
     * Constructs MatchRoom with an empty waiting player list and an empty
     * connected player list.
     */
    public MatchRoom() {
        this.waitingPlayerList = new HashMap<String, Player>();
        this.connectedPlayers = new ArrayList<>();
    }

    /**
     * Parses messages from the client that are intended for the MatchRoom.
     *
     * @param player the player who send the message
     * @param args the String array sent by the player
     */
    public void parse(Player player, String[] args) {
        if (args.length < 2 || player.getPlayerName().equals("")) {
            return;
        }
        String option = args[1];
        switch (option) {
        case "start":
            player.leaveGame();
            joinWaitingList(player);
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
     * Puts a key and a player into a HashMap, the key is sent back to the
     * user. This key is used for other players to identify them and send
     * requests to them.
     * 
     * @param player player to join waiting list
     */
    private synchronized void joinWaitingList(Player player) {
        waitingPlayerList.put(player.getOwnKey(), player);
        player.writeNotification(NotificationMessage.GAME_TOKEN,
                player.getOwnKey());
        sendMatchRoomList();
    }

    public synchronized void assignKey(Player player) {
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        int length = ALPHABET.length();
        for (int i = 0; i < 10; ++i) {
            keyBuilder.append(ALPHABET.charAt(random.nextInt(length)));
        }
        String key = keyBuilder.toString();
        player.setOwnKey(key);
    }

    /**
     * Sends a join request, coming from a player, to a player matching the
     * given key.
     *
     * @param player player sending the request
     * @param key key of player being invited
     */
    private synchronized void joinRequest(Player player, String key) {
        Player opponent = waitingPlayerList.get(key);
        if (player == opponent) {
            player.writeNotification(NotificationMessage.CANNOT_PLAY_YOURSELF);
        } else if (opponent != null) {
            opponent.sendRequest(player);
        }
    }

    /**
     * Called when a player accepts a game request from a player matching the
     * given key.
     *
     * @param player player accepting the request
     * @param key key of player who sent the request
     */
    private synchronized void acceptRequest(Player player, String key) {
        Player opponent = waitingPlayerList.get(key);
        if (opponent != null &&
                opponent.getRequestedGameKey().equals(player.getOwnKey())) {
            waitingPlayerList.remove(key);
            waitingPlayerList.values().remove(player);
            opponent.requestAccepted(player);
            new Game(opponent, player);
            sendMatchRoomList();
            player.rejectAll();
            opponent.rejectAll();
        }
    }

    /**
     * Called when a player rejects a game request from a player matching the
     * given key.
     *
     * @param player player accepting the request
     * @param key key of player who sent the request
     */
    private synchronized void rejectRequest(Player player, String key) {
        Player opponent = waitingPlayerList.get(key);
        if (opponent != null &&
                opponent.getRequestedGameKey().equals(player.getOwnKey())) {
            opponent.requestRejected(player);
        }
    }

    /**
     * Called when a game request from a player gets cancelled.
     *
     * @param player the player who sent and cancelled the invite
     */
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
     * @param player player to be removed
     */
    public synchronized void removeWaitingPlayer(Player player) {
        waitingPlayerList.values().remove(player);
        sendMatchRoomList();
    }

    /**
     * Checks if a player connected to the server already has the requested
     * name.
     *
     * @param name desired name
     * @return true if name taken
     */
    public boolean playerNameExists(String name) {
        for (Player player : connectedPlayers) {
            if (name.equals(player.getPlayerName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends the match room list to all players in the match room list.
     */
    public synchronized void sendMatchRoomList() {
        HashMap<String, String> matchRoomList = new HashMap<String, String>();
        for (Map.Entry<String, Player> entry : waitingPlayerList.entrySet()) {
            String key = entry.getKey();
            Player player = entry.getValue();
            matchRoomList.put(key, player.getPlayerName());
        }
        MatchRoomListMessage message = new MatchRoomListMessage(matchRoomList);
        for (Map.Entry<String, Player> entry : waitingPlayerList.entrySet()) {
            Player player = entry.getValue();
            player.writeObject(message);
        }
    }

    /**
     * Adds player to the list of all connected players.
     *
     * @param player player to be added
     */
    public void addPlayer(Player player) {
        if (!connectedPlayers.contains(player)) {
            connectedPlayers.add(player);
        }
    }

    /**
     * Removes player from the list of all connected players.
     *
     * @param player player to be removed
     */
    public void removePlayer(Player player) {
        connectedPlayers.remove(player);
    }

}
