package model;

import server.messages.MatchRoomListMessage;
import server.messages.NotificationMessage;
import view.ClientView;
import view.InviteReceivedPane;
import view.InviteSentPane;
import view.MatchRoomView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

public class MatchRoom extends Thread {

    private MatchRoomView matchRoomView;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Client clientModel;
    private String key = "";
    private volatile NameState nameState;
    private HashMap<String, InviteReceivedPane> inviteDialogs;
    private InviteSentPane inviteSentPane;

    public MatchRoom(MatchRoomView matchRoomView) {
        this.matchRoomView = matchRoomView;

        try {
            InputStream inputStream = new FileInputStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String hostname = properties.getProperty("hostname");
            int port = Integer.parseInt(properties.getProperty("port"));
            Socket socket = new Socket(hostname, port);
            out = new ObjectOutputStream(new BufferedOutputStream(
                    socket.getOutputStream()));
            in = new ObjectInputStream(socket.getInputStream());
            out.flush();
        } catch (FileNotFoundException e) {
            String message = "Please make sure you have a config.properties " +
                    "file in the current working directory.\n\n" +
                    "It should contain:\n\n" +
                    "hostname=<hostname/ip>\n" +
                    "port=<port>";
            JOptionPane.showMessageDialog(matchRoomView,
                    message, "Can't find config.properties",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inviteDialogs = new HashMap<>();

        start();
    }

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
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendJoinFriend(String key, String name) {
        try {
            out.writeObject(new String[]{"join", "join", key});
            out.flush();
            final String currentKey = key;
            final String currentName = name;
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    inviteSentPane = new InviteSentPane(currentKey, currentName,
                            MatchRoom.this);
                    inviteSentPane.showPane(matchRoomView);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendName(String name) {
        this.nameState = NameState.WAITING;
        try {
            out.writeObject(new String[]{"name", name});
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinLobby() {
        try {
            out.writeObject(new String[]{"join", "start"});
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static enum NameState {
        WAITING, ACCEPTED, INVALID, TAKEN
    }

    public void setNameState(NameState nameState) {
        synchronized (this) {
            this.nameState = nameState;
            this.notifyAll();
        }
    }

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

    private void startGame(Object firstInput) {
        matchRoomView.setVisible(false);
        ClientView clientView = new ClientView(this.out, this.in, this);
        clientModel = clientView.getModel();
        clientModel.parseInput(firstInput);
    }

    public String getKey() {
        return key;
    }

    public void reopen() {
        if (clientModel != null) {
            this.clientModel.getView().dispose();
            this.clientModel = null;
        }
        matchRoomView.setVisible(true);
        joinLobby();
    }

    public void sendStringArray(String[] array) {
        try {
            out.writeObject(array);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disposeAllPanes() {
        for (InviteReceivedPane pane : inviteDialogs.values()) {
            pane.dispose();
        }
        if (inviteSentPane != null) {
            inviteSentPane.dispose();
        }
    }
}
