package model;

import server.messages.MoveResponseMessage;
import view.BoardView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {
	public final int BOARD_DIMENSION = 10;
	private Square[][] squares;
	private ArrayList<Ship> ships;
	private boolean ownBoard;
	private transient BoardView view;
	private transient Client client;
	private transient boolean boatPositionLocked = false;
	private transient ArrayList<PropertyChangeListener> changeListeners;

	public Board (boolean ownBoard) {
		this.ownBoard = ownBoard;
		squares = new Square[ BOARD_DIMENSION ][ BOARD_DIMENSION ];

		// populates the squares array
		for ( int i = 0 ; i < BOARD_DIMENSION ; i++ ) {
			for ( int j = 0 ; j < BOARD_DIMENSION ; j++ ) {
				squares[ i ][ j ] = new Square (i, j, ownBoard);
			}
		}

		ships = new ArrayList<Ship> ();
		ships.add (new Ship (Ship.Type.AIRCRAFT_CARRIER));
		ships.add (new Ship (Ship.Type.BATTLESHIP));
		ships.add (new Ship (Ship.Type.DESTROYER));
		ships.add (new Ship (Ship.Type.PATROL_BOAT));
		ships.add (new Ship (Ship.Type.SUBMARINE));

		this.changeListeners = new ArrayList<>();
	}

	public static boolean isValid (Board board) {
		Board tempBoard = new Board (true);
		for ( Ship s : board.getShips () ) {
			if ( s.getSquares ().size () == 0 ) {
				return false;
			}
			int[] tl = s.getTopLeft ();
			Ship tempBoardShip = tempBoard.findShipByType (s.getType ());
			tempBoardShip.setVertical (s.isVertical ());
			if ( !tempBoard.placeShip (tempBoardShip, tl[ 0 ], tl[ 1 ]) ) {
				return false;
			}
		}
		return tempBoard.shipPlacementEquals (board);
	}

	public boolean isBoatPositionLocked () {
		return boatPositionLocked;
	}

	public void setBoatPositionLocked (boolean boatPositionLocked) {
		this.boatPositionLocked = boatPositionLocked;
		client.getView ().setSendShipState (boatPositionLocked);
		firePropertyChange ("resetSelectedShipView", null, null);
	}

	public boolean isOwnBoard () {
		return (ownBoard);
	}

	public Square getSquare (int x, int y) {
		return squares[ x ][ y ];
	}

	public boolean placeShip (Ship ship, int x, int y) {


		// checks if it is within the board
		int end = (ship.isVertical ()) ? y + ship.getLength () - 1 : x
				+ ship.getLength () - 1;
		if ( x < 0 || y < 0 || end >= BOARD_DIMENSION ) {
			return false;
		}

		// checks for overlapping
		for ( int i = 0 ; i < ship.getLength () ; i++ ) {
			if ( ship.isVertical () ) {
				if ( squares[ x ][ y + i ].isShip () )
					return false;
			} else {
				if ( squares[ x + i ][ y ].isShip () )
					return false;
			}
		}

		// puts ship on squares
		for ( int i = 0 ; i < ship.getLength () ; i++ ) {
			if ( ship.isVertical () ) {
				squares[ x ][ y + i ].setShip (ship);
				ship.setSquare (squares[ x ][ y + i ]);
			} else if ( !ship.isVertical () ) {
				squares[ x + i ][ y ].setShip (ship);
				ship.setSquare (squares[ x + i ][ y ]);
			}
		}

		return true;

	}

	public void pickUpShip (Ship ship) {
		for ( Square s : ship.getSquares () ) {
			s.setShip (null);
		}
		ship.clearSquares ();
	}

	public void setHit (Square s) {
		s.setGuessed (true);
	}

	public boolean gameOver () {
		for ( Ship ship : ships ) {
			if ( !ship.isSunk () )
				return false;
		}
		return true;
	}

	public void printBoard (boolean clean) {
		for ( int i = 0 ; i < BOARD_DIMENSION ; ++i ) {
			for ( int j = 0 ; j < BOARD_DIMENSION ; ++j ) {
				Square s = squares[ j ][ i ];
				Ship ship = s.getShip ();
				char c = '-';
				if ( s.isGuessed () && !clean
						&& s.getState () == Square.State.CONTAINS_SHIP ) {
					c = 'X';
				} else if ( s.isGuessed () && !clean ) {
					c = 'O';
				} else if ( ship != null ) {
					switch (ship.getType ()) {
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
							c = 'P';
					}
				}
				System.out.print (c + " ");
			}
			System.out.println ();
		}
	}

	public ArrayList<Ship> getShips () {
		return ships;
	}

	public void applyMove (MoveResponseMessage move) {
		Ship ship = move.shipSank ();
		if ( ship != null ) {
			ship.sink ();
			if (!ownBoard) {
				ship.updateSquareReferences(this);
				ships.add(ship);
				firePropertyChange("sankShip", null, ship);
			}
			for ( Square shipSquare : ship.getSquares () ) {
				Square boardSquare = getSquare(shipSquare.getX(), shipSquare.getY());
				boardSquare.update (true, ship);
			}
			//TODO: Fix me
			client.getView ().addChatMessage ("SUNK SHIP" + ship.toString ());
		} else {
			Square square = getSquare(move.getX(), move.getY());
			square.update(move.isHit(), null);
		}
	}

	public boolean shipPlacementEquals (Board board) {
		for ( int y = 0 ; y < BOARD_DIMENSION ; ++y ) {
			for ( int x = 0 ; x < BOARD_DIMENSION ; ++x ) {
				Square s1 = this.getSquare (x, y);
				Square s2 = board.getSquare (x, y);
				if ( (s1.isShip () != s2.isShip ()) ) {
					return false;
				}
				if ( s1.getShip () != null && s2.getShip () != null &&
						s1.getShip ().getType () != s2.getShip ().getType () ) {
					return false;
				}
			}
		}
		return true;
	}

	private Ship findShipByType (Ship.Type type) {
		for ( Ship s : ships ) {
			if ( s.getType () == type ) {
				return s;
			}
		}
		return null;
	}

	// TODO: Fix me!
	public int getNumberOfBoats () {
		return 5;
	}

	public Ship.Type[] getShipTypes () {
		return new Ship.Type[]{ Ship.Type.AIRCRAFT_CARRIER,
				Ship.Type.BATTLESHIP,
				Ship.Type.DESTROYER,
				Ship.Type.PATROL_BOAT,
				Ship.Type.SUBMARINE
		};
	}

	public void setView (BoardView view) {
		this.view = view;
	}

	//returns true if the square is next to a ship, to be used to display near misses
	public boolean isSquareNearShip (Square square) {

		for ( int x = square.getX () - 1 ; x <= square.getX () + 1 ; x++ ) {
			for ( int y = square.getY () - 1 ; y <= square.getY () + 1 ; y++ ) {
				if ( isCoordWithinBounds (x, y) && getSquare (x, y).isShip () &&
				     !(x == square.getX() && y == square.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	//checks if x and y are between 0 and 9 inclusive
	private boolean isCoordWithinBounds (int x, int y) {
		return (x >= 0 && x < 10 && y >= 0 && y < 10);
	}

	public void setClient (Client client) {
		this.client = client;
	}

	public void sendMove (int x, int y) throws IOException {
		client.sendMove (x, y);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeListeners.add(listener);
	}

	public void selectedShipRotated () {
		firePropertyChange ("rotateSelectedShip", null, null);
	}

	private void firePropertyChange(String property, Object oldValue, Object newValue) {
		PropertyChangeEvent event =
				new PropertyChangeEvent(this, property, oldValue, newValue);
		for (PropertyChangeListener listener : changeListeners) {
			listener.propertyChange(event);
		}
	}
}
