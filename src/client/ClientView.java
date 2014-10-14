package client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by alexstoick on 10/13/14.
 */
public class ClientView extends JFrame {

	JPanel rootPanel = new JPanel () ;
	JPanel playerBoard ;
	JPanel enemyBoard ;
	JPanel boards = new JPanel () ;
	JPanel controls = new JPanel () ;

	public ClientView() {

		rootPanel.setLayout (new BorderLayout ());
		rootPanel.add ( boards , BorderLayout.CENTER );
		rootPanel.add ( controls , BorderLayout.EAST );
		boards.setBorder (BorderFactory.createLineBorder (Color.red));
		boards.setLayout ( new GridLayout (1,2) );
		this.setContentPane ( rootPanel ) ;

		buildPlayerBoard() ;
		buildEnemyBoard() ;
		drawShips() ;
		attachListeners() ;

		setPreferredSize ( new Dimension (1000 , 1000 ));
		setVisible ( true );
		pack();
	}

	private void attachListeners() {

	}

	private void drawShips() {

	}

	private void buildPlayerBoard() {

		playerBoard = new Drawer( 25 , 25 , 10 , 10 , 30 , 30 ) ;
		playerBoard.setBorder (BorderFactory.createLineBorder (Color.red));
		boards.add (playerBoard);
	}
	private void buildEnemyBoard() {

		enemyBoard = new Drawer ( 25 , 25, 10 , 10 , 30 , 30 ) ;
		enemyBoard.setBorder (BorderFactory.createLineBorder (Color.red));
		boards.add (enemyBoard);

	}

}
