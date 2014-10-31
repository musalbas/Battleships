package model;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import server.messages.MatchRoomListMessage;
import view.MatchRoomView;

public class MatchRoom extends Thread {
    
    private MatchRoomView matchRoomView;
    private ObjectOutputStream out;
    private ObjectInputStream in;

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
                parseInput(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void parseInput(Object input) {
        if (input instanceof MatchRoomListMessage) {
            HashMap<String, String> matchRoomList = ((MatchRoomListMessage) input).getMatchRoomList();
            this.matchRoomView.updateMatchRoomList(matchRoomList);
        }
    }
    
}
