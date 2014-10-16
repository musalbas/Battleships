package logic;

import server.messages.MoveResponseMessage;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
	private final int BOARD_DIMENSION = 10;
	private Square[][] squares;
	private ArrayList<Ship> ships;
	private boolean ownBoard;

	public Board(boolean ownBoard) {
		this.ownBoard = ownBoard;
		squares = new Square[BOARD_DIMENSION][BOARD_DIMENSION];

		// populates the squares array
		for (int i = 0; i < BOARD_DIMENSION; i++) {
			for (int j = 0; j < BOARD_DIMENSION; j++) {
				squares[i][j] = new Square(i, j, ownBoard);
			}
		}

		ships = new ArrayList<Ship>();
		ships.add(new Ship(Ship.Type.AIRCRAFT_CARRIER));
		ships.add(new Ship(Ship.Type.BATTLESHIP));
		ships.add(new Ship(Ship.Type.DESTROYER));
		ships.add(new Ship(Ship.Type.PATROL_BOAT));
		ships.add(new Ship(Ship.Type.SUBMARINE));
	}

	public boolean isOwnBoard() {
		return (ownBoard);
	}

	public Square getSquare(int x, int y) {
		return squares[x][y];
	}

	public boolean placeShip(Ship ship, int x, int y) {

		int end = (ship.isVertical()) ? y + ship.getLength() - 1 : x
				+ ship.getLength() - 1;
		if (x < 0 || y < 0 || end >= BOARD_DIMENSION) {
			return false; // doesn't fit on board
		}

		// puts ship on squares
		for (int i = 0; i < ship.getLength(); i++) {
			if (ship.isVertical()) {
				squares[x][y + i].setShip(ship);
				ship.setSquare(squares[x][y + i]);
			} else if (!ship.isVertical()) {
				squares[x + i][y].setShip(ship);
				ship.setSquare(squares[x + 1][y]);
			}
		}

		return true;

	}

	public void pickUpShip(Ship ship) {
		for (Square s : ship.getSquares()) {
			s.setShip(null);
		}
		ship.clearSquares();
	}

	public void setHit(Square s) {
		s.setGuessed(true);
	}

	public boolean gameOver() {
		for (Ship ship : ships) {
			if (!ship.isSunk())
				return false;
		}
		return true;
	}

	public void printBoard() {
		for (int i = 0; i < BOARD_DIMENSION; ++i) {
			for (int j = 0; j < BOARD_DIMENSION; ++j) {
				Square s = squares[j][i];
				Ship ship = s.getShip();
				char c = '-';
				if (ship != null) {
					switch (ship.getType()) {
					case AIRCRAFT_CARRIER:
						c = 'A';
						break;
					case BATTLESHIP:
						c = 'B';
						break;
					case SUBMARINE:
						c = 'S';
						break;
					case DESTROYER:
						c = 'D';
						break;
					case PATROL_BOAT:
						c = 'p';
					}
				}
				System.out.print(c + " ");
			}
			System.out.println();
		}
	}

	public ArrayList<Ship> getShips() {
		return ships;
	}

    public void applyMove(MoveResponseMessage move) {
        Square s = getSquare(move.getX(), move.getY());
        s.update(move.isHit(), move.shipSank());
        move.shipSank().sink();
    }
}