package view;

import model.MatchRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MatchRoomView extends JFrame {

    private DefaultListModel<RoomPlayer> playersListModel = new DefaultListModel<RoomPlayer>();
    private MatchRoom matchRoom;
    private boolean firstTimeListing = true;
    private HashMap<String, String> matchRoomList;
    private JList<RoomPlayer> playersList;

    public MatchRoomView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        JPanel mainPanel = new JPanel(new BorderLayout(10, 5));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        playersList = new JList<>();
        playersList.setModel(playersListModel);
        playersList.addMouseListener(new PlayersListMouseAdapter());
        playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JButton sendInvite = new JButton("Send invite");
        sendInvite.setEnabled(false);
        sendInvite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RoomPlayer player = playersList.getSelectedValue();
                matchRoom.sendJoinFriend(player.getKey(), player.getName());
            }
        });


        playersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                sendInvite.setEnabled(true);
            }
        });

        mainPanel.add(new JScrollPane(playersList), BorderLayout.CENTER);
        mainPanel.add(sendInvite, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
        setSize(300, 200);

        this.matchRoom = new MatchRoom(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class PlayersListMouseAdapter extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            RoomPlayer player = playersList.getSelectedValue();
            matchRoom.sendJoinFriend(player.getKey(), player.getName());
        }

    }

    private void askForName() {
        String message = "Please choose a nickname.";
        while (true) {
            String name = (String) JOptionPane.showInputDialog(this, message,
                    "Nickname", JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (playerNameExists(name)) {
                message = "This nickname already exists, please try again.";
            } else if (name.equals("")) {
                message = "You must choose a valid nickname.";
            } else {
                this.matchRoom.sendName(name);
                synchronized (matchRoom) {
                    try {
                        if (matchRoom.getNameState() == MatchRoom.NameState.WAITING) {
                            matchRoom.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MatchRoom.NameState state = matchRoom.getNameState();
                if (state == MatchRoom.NameState.ACCEPTED) {
                    break;
                } else if (state == MatchRoom.NameState.INVALID) {
                    message = "You must choose a valid nickname.";
                } else if (state == MatchRoom.NameState.TAKEN) {
                    message = "This nickname already exists, please try again.";
                }
            }
        }
    }

    public boolean playerNameExists(String name) {
        boolean exists = false;
        for (Map.Entry<String, String> entry : matchRoomList.entrySet()) {
            if (entry.getValue().equals(name)) {
                return true;
            }
        }
        return exists;
    }

    public synchronized void updateMatchRoomList(
            HashMap<String, String> matchRoomList) {
        this.matchRoomList = matchRoomList;
        if (firstTimeListing) {
            firstTimeListing = false;
            askForName();
        }

        this.playersListModel.clear();
        for (Map.Entry<String, String> entry : matchRoomList.entrySet()) {
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
