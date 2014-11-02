package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import model.Client;

/**
 * Created by alexstoick on 10/15/14.
 */
public class ClientView extends JFrame {

	private final JTextField inputField = new JTextField ();
	private final JButton submitButton = new JButton ("Submit");
	private final JButton rotateButton = new JButton ("Rotate");
	private final JButton saveShipState = new JButton ("Done placing ships!");
	private JTextArea chat = new JTextArea();
    private Client model;

	public ClientView (ObjectOutputStream out, ObjectInputStream in) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // TODO handle
        }

        JPanel rootPanel = new JPanel(new BorderLayout(5, 5));
        rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        final BoardView myBoard = new BoardView(true);
        final BoardView enemyBoard = new BoardView(false);

        model = new Client(this, myBoard.getModel(), enemyBoard.getModel(), out, in);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 5));
        controlPanel.add(new JScrollPane(chat), BorderLayout.CENTER);
        chat.setEditable(false);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.EAST);

		submitButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				try {
					model.sendChatMessage (inputField.getText ());
				} catch (IOException e1) {
					e1.printStackTrace ();
				}
			}
		});

        controlPanel.add(bottomPanel, BorderLayout.SOUTH);
        controlPanel.setPreferredSize(new Dimension(200, 150));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		rotateButton.setEnabled (false);
		rotateButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
                ShipView shipView = myBoard.getSelectedShip();
                if (shipView != null) {
	                myBoard.getModel ().selectedShipRotated ();
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

		saveShipState.setEnabled (false);
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
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);

        setContentPane(rootPanel);

        pack();
        System.out.println(getSize());
        setVisible(true);

    }

    public static void main(String[] args) {
        //new ClientView();
    }

    public void addChatMessage(String text) {
        chat.append(text + "\n");
    }

	public void setSendShipState (boolean state) {
		saveShipState.setEnabled (state);
	}

	public void setRotateButtonState (boolean state) {
		rotateButton.setEnabled (state);
	}
	
	public Client getModel() {
	    return this.model;
	}
}
