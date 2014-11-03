package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import model.MatchRoom;

public class MatchRoomView extends JFrame {
    
    private DefaultListModel playersListModel = new DefaultListModel();
    private MatchRoom matchRoom;
    private boolean firstTimeListing = true;
    private HashMap<String, String> matchRoomList;

    public MatchRoomView() {
        this.matchRoom = new MatchRoom(this);
        
        JList playersList = new JList();
        playersList.setModel(this.playersListModel);
        playersList.addMouseListener(new PlayersListMouseAdapter());
        
        this.add(playersList);
        
        setVisible(true);
        setSize(180,400);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private class PlayersListMouseAdapter extends MouseAdapter {
        
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }
            
            JList list = (JList) e.getSource();
            int index = list.locationToIndex(e.getPoint());
            RoomPlayer player = (RoomPlayer) playersListModel.get(index);
            
            matchRoom.sendJoinFriend(player.getKey());
        }
        
    }
    
    private void askForName() {
        String message = "Please choose a nickname.";
        while (true) {
            String name = (String)JOptionPane.showInputDialog(
                    this,
                    message,
                    "Nickname",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
            if (playerNameExists(name)) {
                message = "This nickname already exists, please try again.";
            } else if (name.equals("")) {
                message = "You must choose a valid nickname.";
            } else {
                this.matchRoom.sendName(name);
                break;
            }
        }
    }
    
    public boolean playerNameExists(String name) {
        boolean exists = false;
        for (Map.Entry<String, String> entry: matchRoomList.entrySet()) {
            if (entry.getValue().equals(name)) {
                return true;
            }
        }
        return exists;
    }
    
    public void updateMatchRoomList(HashMap<String, String> matchRoomList) {
        this.matchRoomList = matchRoomList;
        if (firstTimeListing) {
            askForName();
            firstTimeListing = false;
        }
        
        this.playersListModel.clear();
        for (Map.Entry<String, String> entry: matchRoomList.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(matchRoom.getKey())) {
                String name = entry.getValue();
                RoomPlayer player = new RoomPlayer(key, name);
                this.playersListModel.addElement(player);
            }
        }
    }
    
    public static void main(String[] args) {
        new MatchRoomView();
    }
    
    private class RoomPlayer {
        
        private String key;
        private String name;
        
        public RoomPlayer(String key, String name) {
            this.key = key;
            this.name = name;
        }
        
        public String toString() {
            return this.name;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getName() {
            return this.name;
        }
        
    }

}
