package view;

import model.MatchRoom;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joe on 06/11/14.
 */
public class InviteSentPane extends JOptionPane {

    private JDialog dialog;
    private MatchRoom matchRoom;
    private String key;

    public InviteSentPane(String key, String name, MatchRoom matchRoom) {
        super();
        this.key = key;
        this.setMessage("Waiting for " + name + " to respond.");
        this.setMessageType(CANCEL_OPTION);
        String[] options = {"Cancel"};
        this.setOptions(options);
        this.matchRoom = matchRoom;
    }

    public void showPane(Component parent) {
        dialog = this.createDialog(parent, "Invite Sent");
        dialog.show();
        dialog.dispose();
        if (getValue() == "Cancel") {
            matchRoom.sendStringArray(new String[]{"join", "cancel"});
        }
    }

    public void dispose() {
        dialog.dispose();
    }
}
