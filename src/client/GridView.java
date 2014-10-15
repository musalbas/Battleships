package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 13.10.2014.
 */
public class GridView extends JPanel {

	private final Board board;

    public GridView( Board b ) {
	    this.board = b ;
	    int BOARD_SIZE = Board.BOARD_SIZE ;
	    int BOAT_SIZE = Board.BOAT_SIZE ;
	    int NUMBER_OF_BOATS = Board.NUMBER_OF_BOATS ;
	    int BOATS_LENGTH[] = Board.BOATS_LENGTH ;

        setPreferredSize(new Dimension((BOARD_SIZE+5) * BOAT_SIZE + 1, (BOARD_SIZE+5) * BOAT_SIZE + 50));
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                board.setCell ( i , j , new Cell(i * BOAT_SIZE, j * BOAT_SIZE, BOAT_SIZE, BOAT_SIZE) );
            }
        }

        int x = 0;
        int y = BOAT_SIZE * BOARD_SIZE + 5;
        for (int i = 0; i < NUMBER_OF_BOATS; i++) {
	        int length = BOATS_LENGTH[i];

	        board.addShip(new Ship(length, BOAT_SIZE, x, y));
            final int newPosition = x + length * BOAT_SIZE + 5;
            if (newPosition + length * BOAT_SIZE + 5 > BOAT_SIZE * 10) {
                x = 0;
                y += BOAT_SIZE + 5;
            } else {
                x = newPosition;
            }
        }
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
	            board.updateHoveredCell( e );
	            board.setHoveredCell ( e );
                getParent().repaint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
	            board.updateSelectedShip(e);
                getParent().repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
	            board.setHoveredCellState(Cell.CLEAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
	            board.setSelectedShip(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
	            board.updateHoveredCell (e);

                if (board.getSelectedShip () != null) {
	                board.moveSelectedShip( );
                } else {
	                board.setHoveredCell ( e );
                    board.setHoveredCellState (new Random().nextInt(2) + 2);
                }
                getParent().repaint();
            }
        });
    }

    public void paint(Graphics g) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.paint(g);
            }
        }
        for (Ship s : ships) {
            s.paint(g);
        }
    }
}
