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
    private JButton sendInvite;
    private JLabel playersNumber;

    public MatchRoomView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 5));
        setTitle("Battleships");
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        playersList = new JList<>();
        playersList.setModel(playersListModel);
        playersList.addMouseListener(new PlayersListMouseAdapter());
        playersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sendInvite = new JButton("Send invite");
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

        playersNumber = new JLabel("Players in room: " + playersListModel.getSize());
        playersNumber.setHorizontalAlignment(JLabel.CENTER);

        mainPanel.add(playersNumber, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(playersList), BorderLayout.CENTER);
        mainPanel.add(sendInvite, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
        pack();

        this.matchRoom = new MatchRoom(this);
        askForName();
        matchRoom.joinLobby();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class PlayersListMouseAdapter extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }

            RoomPlayer player = playersList.getSelectedValue();

            if (player != null) {
                matchRoom.sendJoinFriend(player.getKey(), player.getName());
            }
        }

    }

    private void askForName() {
        String message = "Please choose a nickname.";
        while (true) {
            String name = (String) JOptionPane.showInputDialog(this, message,
                "Nickname", JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (name == null) {
                System.exit(-1);
            }
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
                matchRoom.setOwnName(name);
                break;
            } else if (state == MatchRoom.NameState.INVALID) {
                message = "You must choose a valid nickname.";
            } else if (state == MatchRoom.NameState.TAKEN) {
                message = "This nickname already exists, please try again.";
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
        this.playersListModel.clear();
        for (Map.Entry<String, String> entry : matchRoomList.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(matchRoom.getKey())) {
                String name = entry.getValue();
                RoomPlayer player = new RoomPlayer(key, name);
                this.playersListModel.addElement(player);
            }
        }
        if (playersList.isSelectionEmpty()) {
            sendInvite.setEnabled(false);
        }
        playersNumber.setText("Players in room: " + playersListModel.getSize());
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

    public void showConfigFileError() {
        String message = "Make sure you have a config.properties file\n" +
                "in the current working directory containing:\n\n" +
                "hostname=<hostname/ip>\n" +
                "port=<port>";
        JOptionPane.showMessageDialog(this,
                message, "Can't find a valid config.properties",
                JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }

    public int showInitialConnectionError() {
        String message = "Could not connect to server, did you set the " +
                "correct hostname and port in config.properties?";
        String options[] = {"Quit", "Retry"};
        return JOptionPane.showOptionDialog(this, message,
                "Could not connect to server", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE, null, options, options[1]);
    }

    public void showLostConnectionError() {
        JOptionPane.showMessageDialog(this,
                "Lost connection to server.", "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
}
