package view;

import model.MatchRoom;

import javax.swing.*;
import java.awt.*;

public class InviteSentPane extends JOptionPane {

    private JDialog dialog;
    private MatchRoom matchRoom;

    public InviteSentPane(String name, MatchRoom matchRoom) {
        super();
        this.setMessage("Waiting for " + name + " to respond.");
        this.setMessageType(CANCEL_OPTION);
        String[] options = {"Cancel"};
        this.setOptions(options);
        this.matchRoom = matchRoom;
    }

    public void showPane(Component parent) {
        dialog = this.createDialog(parent, "Invite Sent");
        dialog.setVisible(true);
        dialog.dispose();
        if (getValue() == "Cancel") {
            matchRoom.sendStringArray(new String[]{"join", "cancel"});
        }
    }

    public void dispose() {
        dialog.dispose();
    }
}
