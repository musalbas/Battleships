package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import model.MatchRoom;

public class MatchRoomView extends JFrame {
    
    private DefaultListModel playersListModel = new DefaultListModel();
    private MatchRoom matchRoom;

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
            String playerName = (String) playersListModel.get(index);
            
            // Code to start game here
        }
        
    }
    
    public void updateMatchRoomList(HashMap<String, String> matchRoomList) {
        this.playersListModel.clear();
        for (Map.Entry<String, String> entry: matchRoomList.entrySet()) {
            String key = entry.getKey();
            String name = entry.getValue();
            RoomPlayer player = new RoomPlayer(key, name);
            this.playersListModel.addElement(player);
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
