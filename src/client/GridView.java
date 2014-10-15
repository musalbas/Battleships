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

	private final BoardView board;

	static final int BOAT_SIZE = 40;
	private CellView hoveredCell = null;
	private ShipView selectedShipView = null;
	private CellView[][] viewCells = new CellView[ BoardView.BOARD_SIZE][ BoardView.BOARD_SIZE];
	private ArrayList<ShipView> viewShips = new ArrayList<ShipView>();
	private int xDistance;
	private int yDistance;

    public GridView( BoardView b ) {
	    this.board = b ;


        addCellsAndShips();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
	            updateHoveredCell (e);
	            setHoveredCell (e);
                getParent ().repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
	            updateSelectedShip (e);
                getParent().repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
	            setHoveredCellState(CellView.CLEAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
	            setSelectedShipView (e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
	            updateHoveredCell (e);

                if ( getSelectedShipView () != null) {
	                moveSelectedShip ();
                } else {
	                setHoveredCell (e);
                    setHoveredCellState (new Random ().nextInt (2) + 2);
                }
                getParent().repaint();
            }
        });
    }

	private void addCellsAndShips() {
		int BOARD_SIZE = BoardView.BOARD_SIZE ;
		int NUMBER_OF_BOATS = BoardView.NUMBER_OF_BOATS ;
		int BOATS_LENGTH[] = BoardView.BOATS_LENGTH ;
		setPreferredSize(new Dimension((BOARD_SIZE+5) * BOAT_SIZE + 1, (BOARD_SIZE+5) * BOAT_SIZE + 50));
		setVisible (true);
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				setCell (i, j, new CellView (i * BOAT_SIZE, j * BOAT_SIZE, BOAT_SIZE, BOAT_SIZE));
			}
		}

		int x = 0;
		int y = BOAT_SIZE * BOARD_SIZE + 5;
		for (int i = 0; i < NUMBER_OF_BOATS; i++) {
			int length = BOATS_LENGTH[i];
			viewShips.add(new ShipView (length, BOAT_SIZE, x, y));
			final int newPosition = x + length * BOAT_SIZE + 5;
			if (newPosition + length * BOAT_SIZE + 5 > BOAT_SIZE * 10) {
				x = 0;
				y += BOAT_SIZE + 5;
			} else {
				x = newPosition;
			}
		}
	}

	void setSelectedShipView (MouseEvent e) {
		System.out.println("got a selected ship");
		int x = e.getX();
		int y = e.getY();
		if ( selectedShipView != null) {
			selectedShipView.setSelected (false);
		}
		selectedShipView = getShip(x, y);
		if ( selectedShipView != null) {
			selectedShipView.setSelected(true);
			xDistance = e.getX() - selectedShipView.getX();
			yDistance = e.getY() - selectedShipView.getY();
		}
	}

	void moveSelectedShip() {
		final int x = selectedShipView.getX() + BOAT_SIZE / 2;
		final int y = selectedShipView.getY() + BOAT_SIZE / 2;
		CellView hovered = getCell(x, y);
		if ( hovered != null ) {
			selectedShipView.setX(hovered.getX());
			selectedShipView.setY(hovered.getY());
		} else {
			selectedShipView.resetPosition();
		}
	}

	private ShipView getShip(int x, int y) {
		for ( ShipView shipView : viewShips  ) {
			if ( shipView.has(x, y)) {
				return shipView;
			}
		}
		return null;
	}

	ShipView getSelectedShipView () {
		return selectedShipView;
	}

	CellView getCell(int x, int y) {
		int i = x / BOAT_SIZE;
		int j = y / BOAT_SIZE;
		return i >= 0  && j >= 0 && i < 10 && j < 10 ? viewCells[i][j] : null;
	}

	void setCell ( int i , int j , CellView cell){
		viewCells[i][j] = cell ;
	}

	void updateSelectedShip(MouseEvent e) {
		ShipView selectedShipView = getSelectedShipView () ;
		if ( selectedShipView != null) {
			selectedShipView.setX(e.getX() - xDistance);
			selectedShipView.setY(e.getY() - yDistance);
		}
	}

	void setHoveredCell(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		hoveredCell = getCell (x, y);
		if (hoveredCell != null && hoveredCell.getState() == CellView.CLEAR) {
			hoveredCell.setState(CellView.HOVER);
		}
	}

	void updateHoveredCell(MouseEvent e) {
		if (hoveredCell != null && hoveredCell.getState() == CellView.HOVER) {
			hoveredCell.setState(CellView.CLEAR);
		}
	}

	void setHoveredCellState(int state) {
		if ( hoveredCell != null ) {
			hoveredCell.setState (state);
		}
	}

    public void paint(Graphics g) {
        for ( CellView[] row : viewCells ) {
            for ( CellView cell : row) {
                cell.paint(g);
            }
        }
        for ( ShipView s : viewShips ) {
            s.paint(g);
        }
    }
}
