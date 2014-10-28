package view;

import model.Board;
import model.Ship;
import model.Square;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 13.10.2014.
 */
public class BoardView extends JPanel implements PropertyChangeListener {

    private static int CELL_SIZE = 40;
    private int BOARD_SIZE;
	private SquareView hoveredCell = null;
	private ShipView selectedShipView = null;
	private SquareView[][] viewCells;
	private ArrayList<ShipView> viewShips = new ArrayList<ShipView>();
    private int xDistance;
    private int yDistance;
    private Board model;

    public BoardView(boolean ownBoard) {
        this.model = new Board(ownBoard);
        model.setView(this);
        model.addPropertyChangeListener(this);

        addCellsAndShips();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                resetHoveredCell();
                setHoveredCell(e);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateSelectedShip(e);
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
	            setCellState(hoveredCell, SquareView.CLEAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setSelectedShipView(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                resetHoveredCell();

                if (getSelectedShip() != null) {
                    moveSelectedShip();
                } else {
                    setHoveredCell(e);
	                // send move
	                if ( !model.isOwnBoard () ) {
		                int[] coords = translateCoordinates (e.getX (), e.getY ());
		                try {
			                model.sendMove (coords[ 0 ], coords[ 1 ]);
		                } catch (IOException e1) {
			                e1.printStackTrace ();
		                }
	                }
                }
                repaint();
            }
        });
    }

	public void resetSelectedShipView () {
		selectedShipView.setSelected (false);
		selectedShipView = null;
		repaint();
	}

    public Board getModel() {
        return model;
    }

    private void setSelectedShipView(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (selectedShipView != null) {
            selectedShipView.setSelected(false);
        }
	    if ( model.isBoatPositionLocked () ) {
		    return;
	    }
        selectedShipView = getShip(x, y);
        if (selectedShipView != null) {
            selectedShipView.setSelected(true);
            xDistance = e.getX() - selectedShipView.getX();
            yDistance = e.getY() - selectedShipView.getY();
        }
    }

    private void moveSelectedShip() {
        final int x = selectedShipView.getX() + CELL_SIZE / 2;
        final int y = selectedShipView.getY() + CELL_SIZE / 2;
	    SquareView hovered = getCell (x, y);
	    int[] newPosition = translateCoordinates(x, y);
        boolean shouldReset = true;
        if (hovered != null) {
            boolean result = this.model.placeShip(selectedShipView.getModel(), newPosition[0], newPosition[1]);
            if (result) {
                selectedShipView.setX(hovered.getX());
                selectedShipView.setY(hovered.getY());
                shouldReset = false;
            }
        }
        if (shouldReset) {
            selectedShipView.resetPosition();
            this.model.pickUpShip(selectedShipView.getModel());
        }
        this.model.printBoard(true);
    }

    private ShipView getShip(int x, int y) {
        for (ShipView shipView : viewShips) {
            if (shipView.has(x, y)) {
                return shipView;
            }
        }
        return null;
    }

    public ShipView getSelectedShip() {
        return selectedShipView;
    }

    private int[] translateCoordinates(int x, int y) {
        return new int[]{x / CELL_SIZE, y / CELL_SIZE};
    }

	private SquareView getCell (int x, int y) {
		int i = x / CELL_SIZE;
        int j = y / CELL_SIZE;
        return i >= 0 && j >= 0 && i < 10 && j < 10 ? viewCells[i][j] : null;
    }

	private void setCell (int i, int j, SquareView cell) {
		viewCells[i][j] = cell;
    }

    private void updateSelectedShip(MouseEvent e) {
        ShipView selectedShipView = getSelectedShip();
        if (selectedShipView != null) {
            selectedShipView.setX(e.getX() - xDistance);
            selectedShipView.setY(e.getY() - yDistance);
        }
    }

    private void setHoveredCell(MouseEvent e) {
        if (model.isOwnBoard()) {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        hoveredCell = getCell(x, y);
	    if ( hoveredCell != null && hoveredCell.getState () == SquareView.CLEAR ) {
		    hoveredCell.setState(SquareView.HOVER);
	    }
    }

    private void resetHoveredCell() {
	    if ( hoveredCell != null && hoveredCell.getState () == SquareView.HOVER && !model.isOwnBoard () ) {
		    hoveredCell.setState (SquareView.CLEAR);
	    }
    }

	public void setCellState (SquareView squareView, int state) {
		if (squareView != null && !model.isOwnBoard()) {
			if ( state == SquareView.HIT ) {
				new ExplosionAnimation(squareView, this).start();
            }
        }
    }

    public void addShipView (Ship ship) {
        int topLeft[] = ship.getTopLeft();
        ShipView shipView = new ShipView(ship.getLength(), CELL_SIZE, topLeft[0] * CELL_SIZE, topLeft[1] * CELL_SIZE, ship);
        if (ship.isVertical()) {
            shipView.rotate();
        }
        viewShips.add(shipView);
    }

    private void addCellsAndShips() {

        BOARD_SIZE = model.BOARD_DIMENSION;

	    viewCells = new SquareView[ BOARD_SIZE ][ BOARD_SIZE ];

        setPreferredSize(new Dimension((BOARD_SIZE) * CELL_SIZE + 1, (BOARD_SIZE + 3) * CELL_SIZE + 15));
        setVisible(true);
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
	            setCell (i, j, new SquareView (i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE, this, model.getSquare(i, j)));
            }
        }
        if (model.isOwnBoard()) {
            int x = 0;
            int y = CELL_SIZE * BOARD_SIZE + 5;
            for (Ship shipModel : model.getShips()) {
                int length = shipModel.getLength();
                ShipView shipView = new ShipView(length, CELL_SIZE, x, y, shipModel);
                shipModel.setView(shipView);
                viewShips.add(shipView);
                final int newPosition = x + length * CELL_SIZE + 5;
                if (newPosition + length * CELL_SIZE + 5 > CELL_SIZE * 10) {
                    x = 0;
                    y += CELL_SIZE + 5;
                } else {
                    x = newPosition;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
	    for ( SquareView[] row : viewCells ) {
		    for ( SquareView cell : row ) {
			    cell.paint(g);
            }
        }
        for (ShipView s : viewShips) {
            s.paint(g);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("sankShip")) {
            addShipView((Ship) evt.getNewValue());
        }
    }
}
