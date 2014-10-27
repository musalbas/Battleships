package client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

public class MatchroomView extends JFrame {
    
    private DefaultListModel playersListModel = new DefaultListModel();

    public MatchroomView() {

        JList playersList = new JList();
        playersList.setModel(this.playersListModel);
        
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
            
            // Send player a match request here
            
        }
        
    }
    
    public static void main(String[] args) {
        new MatchroomView();
    }

}
