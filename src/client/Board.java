package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by user on 13.10.2014.
 */
public class Board {

	static final int BOARD_SIZE = 10 ;
	static final int NUMBER_OF_BOATS = 5 ;
	static final int[] BOATS_LENGTH = { 2, 3, 3, 4, 5} ;
	static final int BOAT_SIZE = 40;
	private Cell[][] cells = new Cell[BOARD_SIZE][BOARD_SIZE];
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private Ship selectedShip = null;

	private int xDistance;
	private int yDistance;
	private GridView view ;

	void addShip( Ship ship){
		ships.add(ship);
	}

	void setSelectedShip(MouseEvent e) {
		System.out.println("got a selected ship");
		int x = e.getX();
		int y = e.getY();
		if ( selectedShip != null) {
			selectedShip.setSelected (false);
		}
		selectedShip = getShip(x, y);
		if (selectedShip != null) {
			selectedShip.setSelected(true);
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

	void moveSelectedShip() {
		final int x = selectedShip.getX() + BOAT_SIZE / 2;
		final int y = selectedShip.getY() + BOAT_SIZE / 2;
		Cell hovered = getCell(x, y);
		if ( hovered != null ) {
			selectedShip.setX(hovered.getX());
			selectedShip.setY(hovered.getY());
		} else {
			selectedShip.resetPosition();
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

	Cell getCell(int x, int y) {
		int i = x / BOAT_SIZE;
		int j = y / BOAT_SIZE;
		return i >= 0  && j >= 0 && i < 10 && j < 10 ? cells[i][j] : null;
	}

	void setCell ( int i , int j , Cell cell){
		cells[i][j] = cell ;
	}

	public ArrayList<Ship> getShips () {
		return ships;
	}

	public Cell[][] getCells () {
		return cells;
	}

	public GridView getView () {
		return view;
	}

	Board() {
        view = new GridView( this ) ;
    }
}
