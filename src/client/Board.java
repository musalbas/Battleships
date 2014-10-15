package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by user on 13.10.2014.
 */
public class Board extends JPanel {

	static final int BOARD_SIZE = 10 ;
	static final int NUMBER_OF_BOATS = 5 ;
	static final int[] BOATS_LENGTH = { 2, 3, 3, 4, 5} ;
	static final int BOAT_SIZE = 40;
	private Cell[][] cells = new Cell[BOARD_SIZE][BOARD_SIZE];
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private Ship selectedShip;
	private Cell hoveredCell;
	private int xDistance;
	private int yDistance;
	private GridView view ;

	void addShip( Ship ship){
		ships.add(ship);
	}

	void setHoveredCellState(int state) {
		hoveredCell.setState (state);
	}

	void setSelectedShip(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		selectedShip = getShip(x, y);
		selectedShip.setSelected(true);
		if (selectedShip != null) {
			xDistance = e.getX() - selectedShip.getX();
			yDistance = e.getY() - selectedShip.getY();
		}
	}

	void updateSelectedShip(MouseEvent e) {
		if (selectedShip != null) {
			selectedShip.setX(e.getX() - xDistance);
			selectedShip.setY(e.getY() - yDistance);
		}
	}

	void updateHoveredCell(MouseEvent e) {
		if (hoveredCell != null && hoveredCell.getState() == Cell.HOVER) {
			hoveredCell.setState(Cell.CLEAR);
		}
	}

	void moveSelectedShip() {
		final int x = selectedShip.getX() + BOAT_SIZE / 2;
		final int y = selectedShip.getY() + BOAT_SIZE / 2;
		Cell hovered = getCell(x, y);
		if ( hovered != null ) {
			selectedShip.setX(hovered.getX());
			selectedShip.setY(hovered.getY());
		} else {
			selectedShip.resetPosition();
			getParent().repaint();
		}
	}

	void setHoveredCell(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		hoveredCell = getCell(x, y);
		if (hoveredCell != null && hoveredCell.getState() == Cell.CLEAR) {
			hoveredCell.setState(Cell.HOVER);
		}
	}

	private Ship getShip(int x, int y) {
		for (Ship ship : ships) {
			if (ship.has(x, y)) {
				return ship;
			}
		}
		return null;
	}

	Ship getSelectedShip () {
		return selectedShip;
	}

	private Cell getCell(int x, int y) {
		int i = x / BOAT_SIZE;
		int j = y / BOAT_SIZE;
		return i >= 0  && j >= 0 && i < 10 && j < 10 ? cells[i][j] : null;
	}

	void setCell ( int i , int j , Cell cell){
		cells[i][j] = cell ;
	}
    Board() {
        view = new GridView( this ) ;
    }
}
