package model;

import server.messages.MatchRoomListMessage;
import server.messages.NotificationMessage;
import view.ClientView;
import view.InviteReceivedPane;
import view.InviteSentPane;
import view.MatchRoomView;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

/**
 * A MatchRoom responsible for finding player's to play a game with. Makes the
 * initial connection to the server and responsible for setting a player's name.
 * It contains an {@link java.io.ObjectOutputStream} to write to the server, and
 * and {@link java.io.ObjectInputStream} to receive objects from the server.
 */
public class MatchRoom extends Thread {

    private MatchRoomView matchRoomView;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Client clientModel;
    private String key = "";
    private String ownName;
    private volatile NameState nameState;
    private HashMap<String, InviteReceivedPane> inviteDialogs;
    private InviteSentPane inviteSentPane;

    /**
     * Constructs MatchRoom with a reference {@link view.MatchRoomView}. The
     * connection information to the server is loaded from a config file and the
     * initial connection to the server is made.
     *
     * @param matchRoomView the related view
     */
    public MatchRoom(MatchRoomView matchRoomView) {
        this.matchRoomView = matchRoomView;

        boolean connected = false;

        while (!connected) {
            try {
                InputStream inputStream = new FileInputStream("config.properties");
                Properties properties = new Properties();
                properties.load(inputStream);
                String hostname = properties.getProperty("hostname");
                String portStr = properties.getProperty("port");
                if (hostname == null || portStr == null) {
                    matchRoomView.showConfigFileError();
                }
                int port = Integer.parseInt(portStr);
                Socket socket = new Socket(hostname, port);
                out = new ObjectOutputStream(new BufferedOutputStream(
                        socket.getOutputStream()));
                in = new ObjectInputStream(socket.getInputStream());
                out.flush();
                connected = true;
            } catch (FileNotFoundException e) {
                matchRoomView.showConfigFileError();
            } catch (IOException e) {
                int response = matchRoomView.showInitialConnectionError();
                if (response == 0) {
                    System.exit(-1);
                }
            }
        }

        inviteDialogs = new HashMap<>();

        start();
    }

    /**
     * Runs this {@link Thread}. Waits to receive input from the server, checks
     * to see if {@link model.Client} is active, if so, parses the input to
     * {@link model.Client}. If {@link model.Client} is null, the input is
     * parsed in this object.
     */
    @Override
    public void run() {
        super.run();
        Object input;
        try {
            while ((input = in.readObject()) != null) {
                System.out.println(input);
                if (clientModel != null) {
                    clientModel.parseInput(input);
                } else {
                    parseInput(input);
                }
            }
            System.out.println("stopped");
        } catch (IOException e) {
            matchRoomView.showLostConnectionError();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a game request to the player matching the given key, and displays
     * a {@link view.InviteSentPane} informing the player that they have sent
     * a request and who to, and allows them to cancel it.
     *
     * @param key key of invited player
     * @param name name of invited player
     */
    public void sendJoinFriend(String key, final String name) {
        try {
            out.writeObject(new String[]{"join", "join", key});
            out.flush();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    inviteSentPane = new InviteSentPane(name, MatchRoom.this);
                    inviteSentPane.showPane(matchRoomView);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the player's desired name to the server.
     *
     * @param name the player's desired name
     */
    public void sendName(String name) {
        this.nameState = NameState.WAITING;
        sendStringArray(new String[]{"name", name});
    }


    /**
     * Sends a request to the server to join the {@link server.MatchRoom} lobby.
     */
    public void joinLobby() {
        sendStringArray(new String[]{"join", "start"});
    }

    /**
     * Enumerations to represent the state of the player's name. The state is
     * WAITING when they are waiting for a response from the server, ACCEPTED
     * means the name has been accepted by the server, INVALID means the name
     * was not a valid name, TAKEN means another player already has the name.
     */
    public static enum NameState {
        WAITING, ACCEPTED, INVALID, TAKEN
    }

    private void setNameState(NameState nameState) {
        synchronized (this) {
            this.nameState = nameState;
            this.notifyAll();
        }
    }

    /**
     * Gets the state of the request of being assigned the desired name.
     *
     * @return state of the name request
     */
    public NameState getNameState() {
        return nameState;
    }

    private void parseInput(Object input) {
        if (input instanceof MatchRoomListMessage) {
            final HashMap<String, String> matchRoomList = ((MatchRoomListMessage) input)
                    .getMatchRoomList();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    matchRoomView.updateMatchRoomList(matchRoomList);
                }
            });
        } else if (input instanceof NotificationMessage) {
            NotificationMessage n = (NotificationMessage) input;
            switch (n.getCode()) {
                case NotificationMessage.GAME_TOKEN:
                    if (n.getText().length == 1) {
                        key = n.getText()[0];
                    }
                    break;
                case NotificationMessage.OPPONENTS_NAME:
                    disposeAllPanes();
                    startGame(input);
                    break;
                case NotificationMessage.NAME_ACCEPTED:
                    setNameState(NameState.ACCEPTED);
                    break;
                case NotificationMessage.NAME_TAKEN:
                    setNameState(NameState.TAKEN);
                    break;
                case NotificationMessage.INVALID_NAME:
                    setNameState(NameState.INVALID);
                    break;
                case NotificationMessage.NEW_JOIN_GAME_REQUEST:
                    final InviteReceivedPane dialog = new InviteReceivedPane(
                            n.getText()[0], n.getText()[1], this);
                    System.out.println("request from " + n.getText()[0] + " " + n.getText()[1]);
                    inviteDialogs.put(n.getText()[0], dialog);
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            dialog.showOptionPane(matchRoomView);
                        }
                    });
                    break;
                case NotificationMessage.JOIN_GAME_REQUEST_REJECTED:
                    System.out.println("Join request rejected");
                    if (inviteSentPane != null) {
                        inviteSentPane.dispose();
                    }
                    break;
                case NotificationMessage.JOIN_GAME_REQUEST_ACCEPTED:
                    System.out.println("Join request accepted");
                    break;
                case NotificationMessage.JOIN_GAME_REQUEST_CANCELLED:
                    System.out.println("cancelled");
                    InviteReceivedPane pane = inviteDialogs.get(n.getText()[0]);
                    if (pane != null) {
                        pane.dispose();
                    } else {
                        System.out.println("can't find " + n.getText()[0]);
                    }
            }
        }
    }

    /**
     * Starts the game and opens {@link view.ClientView}. Passes the information
     * just received by the server to {@link model.Client} to be parsed.
     *
     * @param firstInput data to be passed to {@link model.Client}
     */
    private void startGame(Object firstInput) {
        matchRoomView.setVisible(false);
        ClientView clientView = new ClientView(this.out, this.in, this);
        clientModel = clientView.getModel();
        clientModel.parseInput(firstInput);
    }

    /**
     * Returns the client's unique key.
     *
     * @return the client's key
     */
    public String getKey() {
        return key;
    }

    /**
     * Reopens {@link view.MatchRoomView}, disposing of {@link view.ClientView},
     * and stops {@link model.Client} from handling the input from the server.
     */
    public void reopen() {
        if (clientModel != null) {
            this.clientModel.getView().dispose();
            this.clientModel = null;
        }
        matchRoomView.setVisible(true);
        joinLobby();
    }

    /**
     * Writes a String array to the server and flushes it.
     *
     * @param array String array to be sent to server
     */
    public void sendStringArray(String[] array) {
        try {
            out.writeObject(array);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disposes of all open invite sent and invite received panes.
     */
    private void disposeAllPanes() {
        for (InviteReceivedPane pane : inviteDialogs.values()) {
            pane.dispose();
        }
        if (inviteSentPane != null) {
            inviteSentPane.dispose();
        }
    }

    /**
     * Sets the player's local reference of their own name.
     *
     * @param ownName the player's name
     */
    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    /**
     * Returns the player's local reference of their own name.
     *
     * @return the player's name
     */
    public String getOwnName() {
        return ownName;
    }
}
