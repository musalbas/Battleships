package view;

import model.MatchRoom;

import javax.swing.*;
import java.awt.*;

/**
 * An option pane that is displayed when a player invites another player to a
 * game, giving them the chance to cancel the request.
 */
public class InviteSentPane extends JOptionPane {

    private JDialog dialog;
    private MatchRoom matchRoom;

    /**
     * Constructs InviteSentPane with the name of the invited player, and
     * a reference to {@link model.MatchRoom} for if the player needs to cancel
     * a request.
     *
     * @param name name of invited player
     * @param matchRoom MatchRoom object to send cancellation to
     */
    public InviteSentPane(String name, MatchRoom matchRoom) {
        super();
        this.setMessage("Waiting for " + name + " to respond.");
        this.setMessageType(CANCEL_OPTION);
        String[] options = {"Cancel"};
        this.setOptions(options);
        this.matchRoom = matchRoom;
    }

    /**
     * Shows the InviteSentPane, with a cancel button. If the cancel button is
     * pressed, {@link model.MatchRoom} informs the server and the request is
     * cancelled.
     *
     * @param parent the frame to display the dialog in
     */
    public void showPane(Component parent) {
        dialog = this.createDialog(parent, "Invite Sent");
        dialog.setVisible(true);
        dialog.dispose();
        if (getValue() == "Cancel") {
            matchRoom.sendStringArray(new String[]{"join", "cancel"});
        }
    }

    /**
     * Disposes of the frame, cancelling the user's chance to cancel the invite.
     */
    public void dispose() {
        dialog.dispose();
    }
}
