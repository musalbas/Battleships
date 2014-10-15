package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by alexstoick on 10/15/14.
 */
public class ClientView extends JFrame {

	ClientView() {

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout ( new BorderLayout ());
		setContentPane (rootPanel);

		JPanel boardsPanel = new JPanel (new GridLayout(1, 2, 10, 10)) ;
		final Board myBoard = new Board();
		boardsPanel.add(myBoard);

		rootPanel.add ( boardsPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel controlPanel = new JPanel( new GridLayout (1,1)) ;
		JButton rotateButton = new JButton ( "Rotate" ) ;
		rotateButton.addActionListener ( new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
			   if ( myBoard.getSelectedShip() )
			}
		});
		controlPanel.add(rotateButton);
		rootPanel.add ( controlPanel, BorderLayout.EAST ) ;
		pack ();
		setVisible (true);

	}

	public static void main ( String[] args ) {
		new ClientView ();
	}

}
