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

	private Cell hoveredCell = null;

    public GridView( Board b ) {
	    this.board = b ;


        addCellsAndShips();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
	            updateHoveredCell( e );
	            setHoveredCell ( e );
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
	            setHoveredCellState(Cell.CLEAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
	            board.setSelectedShip(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
	            updateHoveredCell (e);

                if (board.getSelectedShip () != null) {
	                board.moveSelectedShip( );
                } else {
	                setHoveredCell ( e );
                    setHoveredCellState (new Random().nextInt(2) + 2);
                }
                getParent().repaint();
            }
        });
    }

	private void addCellsAndShips() {
		int BOARD_SIZE = Board.BOARD_SIZE ;
		int BOAT_SIZE = Board.BOAT_SIZE ;
		int NUMBER_OF_BOATS = Board.NUMBER_OF_BOATS ;
		int BOATS_LENGTH[] = Board.BOATS_LENGTH ;
		setPreferredSize(new Dimension((BOARD_SIZE+5) * BOAT_SIZE + 1, (BOARD_SIZE+5) * BOAT_SIZE + 50));
		setVisible (true);
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
	}

	void setHoveredCell(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		hoveredCell = board.getCell(x, y);
		if (hoveredCell != null && hoveredCell.getState() == Cell.CLEAR) {
			hoveredCell.setState(Cell.HOVER);
		}
	}

	void updateHoveredCell(MouseEvent e) {
		if (hoveredCell != null && hoveredCell.getState() == Cell.HOVER) {
			hoveredCell.setState(Cell.CLEAR);
		}
	}

	void setHoveredCellState(int state) {
		if ( hoveredCell != null ) {
			hoveredCell.setState (state);
		}
	}

    public void paint(Graphics g) {
        for (Cell[] row : board.getCells() ) {
            for (Cell cell : row) {
                cell.paint(g);
            }
        }
        for (Ship s : board.getShips() ) {
            s.paint(g);
        }
    }
}
