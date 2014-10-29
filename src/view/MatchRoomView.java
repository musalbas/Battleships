package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class MatchRoomView extends JFrame {
    
    private DefaultListModel playersListModel = new DefaultListModel();

    public MatchRoomView() {
        JList playersList = new JList();
        playersList.setModel(this.playersListModel);
        playersList.addMouseListener(new PlayersListMouseAdapter());
        
        this.add(playersList);
        
        setVisible(true);
        setSize(180,400);
        
        // Testing
        this.playersListModel.addElement("pl4y3r1");
        this.playersListModel.addElement("Classy Bob");
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
    
    public static void main(String[] args) {
        new MatchRoomView();
    }

}
