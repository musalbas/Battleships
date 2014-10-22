package view;

import model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by alexstoick on 10/15/14.
 */
public class ClientView extends JFrame {

	private JTextArea chat = new JTextArea ();
	private Client model;

	ClientView () {

		JPanel rootPanel = new JPanel (new GridLayout (1, 3, 5, 5));

		final BoardView myBoard = new BoardView (true);
		final BoardView enemyBoard = new BoardView (false);

		model = new Client (this, myBoard.getModel (), enemyBoard.getModel ());

		JPanel controlPanel = new JPanel (new BorderLayout ());
		controlPanel.add (new JScrollPane (chat), BorderLayout.CENTER);
		final JTextField inputField = new JTextField ();
		final JButton submitButton = new JButton ("Submit");
		JPanel bottomPanel = new JPanel (new GridLayout (1, 2, 10, 10));
		chat.setEditable (false);
		bottomPanel.add (inputField);
		bottomPanel.add (submitButton);
		controlPanel.add (bottomPanel, BorderLayout.SOUTH);

		JButton rotateButton = new JButton ("Rotate");
		controlPanel.add (rotateButton, BorderLayout.WEST);
		rotateButton.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				ShipView shipView = myBoard.getSelectedShip ();
				if ( shipView != null ) {
					shipView.rotate ();
					repaint ();
				}
			}
		});

		setContentPane (rootPanel);
		rootPanel.add (enemyBoard);
		rootPanel.add (myBoard);
		rootPanel.add (controlPanel);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		pack ();
		setVisible (true);

	}

	public static void main (String[] args) {
		new ClientView ();
	}

	public void addChatMessage (String text) {
		chat.append (text + "\n");
	}

}
