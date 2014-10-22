package view;

import model.Board;
import model.Ship;

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
public class BoardView extends JPanel {

	private static int CELL_SIZE = 40;
	private int BOARD_SIZE;
	private int NUMBER_OF_BOATS;
	private Ship.Type[] BOATS_TYPE;
	private CellView hoveredCell = null;
	private ShipView selectedShipView = null;
	private CellView[][] viewCells;
	private ArrayList<ShipView> viewShips = new ArrayList<ShipView> ();
	private int xDistance;
	private int yDistance;
	private Board model;

	public BoardView (boolean ownBoard) {
		this.model = new Board (ownBoard);
		model.setView (this);

		addCellsAndShips ();
		addMouseMotionListener (new MouseMotionAdapter () {
			@Override
			public void mouseMoved (MouseEvent e) {
				updateHoveredCell (e);
				setHoveredCell (e);
				repaintRoot ();
			}

			@Override
			public void mouseDragged (MouseEvent e) {
				updateSelectedShip (e);
				repaintRoot ();
			}
		});
		addMouseListener (new MouseAdapter () {
			@Override
			public void mouseExited (MouseEvent e) {
				setHoveredCellState (CellView.CLEAR);
			}

			@Override
			public void mousePressed (MouseEvent e) {
				setSelectedShipView (e);
			}

			@Override
			public void mouseReleased (MouseEvent e) {
				updateHoveredCell (e);

				if ( getSelectedShip () != null ) {
					moveSelectedShip ();
				} else {
					setHoveredCell (e);
					final int state = new Random ().nextInt (2) + 2;
					setHoveredCellState (state);
				}
				repaintRoot ();
			}
		});
	}

	public Board getModel () {
		return model;
	}

	/**
	 * Calling repaint() works only on Mac and Linux, on Windows repaint() causes issues with components layout.
	 * The whole JFrame has to repainted.
	 */
	public void repaintRoot () {
		Component c = SwingUtilities.getWindowAncestor (this);
		if ( c != null ) {
			c.repaint ();
		}
	}

	void setSelectedShipView (MouseEvent e) {
		int x = e.getX ();
		int y = e.getY ();
		if ( selectedShipView != null ) {
			selectedShipView.setSelected (false);
		}
		selectedShipView = getShip (x, y);
		if ( selectedShipView != null ) {
			selectedShipView.setSelected (true);
			xDistance = e.getX () - selectedShipView.getX ();
			yDistance = e.getY () - selectedShipView.getY ();
		}
	}

	void moveSelectedShip () {
		final int x = selectedShipView.getX () + CELL_SIZE / 2;
		final int y = selectedShipView.getY () + CELL_SIZE / 2;
		CellView hovered = getCell (x, y);
		int[] newPosition = translateCoordinates (x, y);
		boolean shouldReset = true;
		if ( hovered != null ) {
			boolean result = this.model.placeShip (selectedShipView.getModel (), newPosition[ 0 ], newPosition[ 1 ]);
			if ( result ) {
				selectedShipView.setX (hovered.getX ());
				selectedShipView.setY (hovered.getY ());
				shouldReset = false;
			}
		}
		if ( shouldReset ) {
			selectedShipView.resetPosition ();
			this.model.pickUpShip (selectedShipView.getModel ());
		}
		this.model.printBoard (true);
	}

	private ShipView getShip (int x, int y) {
		for ( ShipView shipView : viewShips ) {
			if ( shipView.has (x, y) ) {
				return shipView;
			}
		}
		return null;
	}

	ShipView getSelectedShip () {
		return selectedShipView;
	}

	private int[] translateCoordinates (int x, int y) {
		return new int[]{ x / CELL_SIZE, y / CELL_SIZE };
	}

	CellView getCell (int x, int y) {
		int i = x / CELL_SIZE;
		int j = y / CELL_SIZE;
		return i >= 0 && j >= 0 && i < 10 && j < 10 ? viewCells[ i ][ j ] : null;
	}

	void setCell (int i, int j, CellView cell) {
		viewCells[ i ][ j ] = cell;
	}

	void updateSelectedShip (MouseEvent e) {
		ShipView selectedShipView = getSelectedShip ();
		if ( selectedShipView != null ) {
			selectedShipView.setX (e.getX () - xDistance);
			selectedShipView.setY (e.getY () - yDistance);
		}
	}

	void setHoveredCell (MouseEvent e) {
		if ( model.isOwnBoard () ) {
			return;
		}
		int x = e.getX ();
		int y = e.getY ();
		hoveredCell = getCell (x, y);
		if ( hoveredCell != null && hoveredCell.getState () == CellView.CLEAR ) {
			hoveredCell.setState (CellView.HOVER);
		}
	}

	void updateHoveredCell (MouseEvent e) {
		if ( hoveredCell != null && hoveredCell.getState () == CellView.HOVER && !model.isOwnBoard () ) {
			hoveredCell.setState (CellView.CLEAR);
		}
	}

	void setHoveredCellState (int state) {
		if ( hoveredCell != null && !model.isOwnBoard () ) {
			if ( state == CellView.HIT ) {
				new ExplosionAnimation (hoveredCell, this).start ();
			}
			hoveredCell.setState (state);
		}
	}

	private void addCellsAndShips () {

		BOARD_SIZE = model.BOARD_DIMENSION;
		NUMBER_OF_BOATS = model.getNumberOfBoats ();
		BOATS_TYPE = model.getShipTypes ();

		viewCells = new CellView[ BOARD_SIZE ][ BOARD_SIZE ];

		setPreferredSize (new Dimension ((BOARD_SIZE + 5) * CELL_SIZE + 1, (BOARD_SIZE + 5) * CELL_SIZE + 50));
		setVisible (true);
		for ( int i = 0 ; i < BOARD_SIZE ; ++i ) {
			for ( int j = 0 ; j < BOARD_SIZE ; ++j ) {
				setCell (i, j, new CellView (i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE));
			}
		}
		if ( model.isOwnBoard () ) {
			int x = 0;
			int y = CELL_SIZE * BOARD_SIZE + 5;
			for ( int i = 0 ; i < NUMBER_OF_BOATS ; i++ ) {
				Ship shipModel = new Ship (BOATS_TYPE[ i ]);
				int length = shipModel.getLength ();
				ShipView shipView = new ShipView (length, CELL_SIZE, x, y, shipModel);
				shipModel.setView (shipView);
				viewShips.add (shipView);
				final int newPosition = x + length * CELL_SIZE + 5;
				if ( newPosition + length * CELL_SIZE + 5 > CELL_SIZE * 10 ) {
					x = 0;
					y += CELL_SIZE + 5;
				} else {
					x = newPosition;
				}
			}
		}
	}

	@Override
	protected void paintComponent (Graphics g) {
		for ( CellView[] row : viewCells ) {
			for ( CellView cell : row ) {
				cell.paint (g);
			}
		}
		for ( ShipView s : viewShips ) {
			s.paint (g);
		}
	}
}
