package model;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JFrame;

import server.messages.MatchRoomListMessage;
import server.messages.NotificationMessage;
import view.ClientView;
import view.MatchRoomView;

public class MatchRoom extends Thread {
    
    private MatchRoomView matchRoomView;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile Client clientModel;
    private String key = "";
    private volatile NameState nameState;

    public MatchRoom(MatchRoomView matchRoomView) {
        this.matchRoomView = matchRoomView;
        
        try {
            Socket socket = new Socket("localhost", 8900);
            out = new ObjectOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            in = new ObjectInputStream(socket.getInputStream());
            out.flush ();
            out.writeObject (new String[]{ "join", "start" });
            out.flush ();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
    
    public void sendJoinFriend(String key) {
        try {
            out.writeObject(new String[]{"join", "join", key});
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendName(String name) {
        this.nameState = NameState.WAITING;
        try {
            out.writeObject(new String[]{ "name", name });
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
            final HashMap<String, String> matchRoomList = ((MatchRoomListMessage) input).getMatchRoomList();
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
            }
        }
    }
    
    private void startGame(Object firstInput) {
        ClientView clientView = new ClientView(this.out, this.in);
        clientModel = clientView.getModel();
        clientModel.parseInput(firstInput);
    }

    public String getKey() {
        return key;
    }
    
}
