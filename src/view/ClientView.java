package view;

import model.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by alexstoick on 10/15/14.
 */
public class ClientView extends JFrame {

    private final JTextField inputField = new JTextField();
    private final JButton submitButton = new JButton("Submit");
    private final JButton rotateButton = new JButton("Rotate");
    private final JButton saveShipState = new JButton("Done placing ships!");
    private JList<String> chat = new JList<>();
    private DefaultListModel<String> chatModel = new DefaultListModel<>();
    private Client model;

    public ClientView(ObjectOutputStream out, final ObjectInputStream in) {
        chat.setModel(chatModel);

        JPanel rootPanel = new JPanel(new BorderLayout(5, 5));
        rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        final BoardView myBoard = new BoardView(true);
        final BoardView enemyBoard = new BoardView(false);

        model = new Client(this, myBoard.getModel(), enemyBoard.getModel(), out, in);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        JScrollPane chatScrollPane = new JScrollPane(chat);

        chatScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        controlPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        controlPanel.add(bottomPanel, BorderLayout.SOUTH);
        controlPanel.setPreferredSize(new Dimension(200, 150));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        rotateButton.setEnabled(false);
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShipView shipView = myBoard.getSelectedShip();
                if (shipView != null) {
                    myBoard.getModel().selectedShipRotated();
                }
            }
        });

        saveShipState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    model.sendBoard(myBoard.getModel());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        saveShipState.setEnabled(false);
        buttons.add(saveShipState);
        buttons.add(rotateButton);

        JPanel boards = new JPanel(new GridLayout(1, 2, 10, 10));

        boards.add(enemyBoard);
        boards.add(myBoard);

        JPanel gamePanel = new JPanel(new BorderLayout(10, 10));

        gamePanel.add(boards, BorderLayout.CENTER);
        gamePanel.add(buttons, BorderLayout.SOUTH);

        rootPanel.add(gamePanel, BorderLayout.CENTER);
        rootPanel.add(controlPanel, BorderLayout.EAST);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setContentPane(rootPanel);

        pack();
        System.out.println(getSize());
        setVisible(true);

    }

    public static void main(String[] args) {
        //new ClientView();
    }

    public void sendChatMessage() {
        try {
            model.sendChatMessage(inputField.getText());
            inputField.setText("");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void addChatMessage(String text) {
        chatModel.addElement("<html><b>" + text + "</b></html>" + "\n");
    }

    public void setSendShipState(boolean state) {
        saveShipState.setEnabled(state);
    }

    public void setRotateButtonState(boolean state) {
        rotateButton.setEnabled(state);
    }

    public Client getModel() {
        return this.model;
    }
}
