package model;

import server.messages.MoveResponseMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Battleships board containing a 10x10 {@link Square} grid and 5 {@link Ship}
 * objects. The Board implements Serializable to allow it to be sent over
 * ObjectOutputStreams/ObjectInputStreams
 */
public class Board implements Serializable {
    /**
     * The dimension of the Board
     */
    public static final int BOARD_DIMENSION = 10;
    private Square[][] squares;
    private ArrayList<Ship> ships;
    private boolean ownBoard;
    private transient Client client;
    private transient boolean boatPositionLocked = true;
    private transient ArrayList<PropertyChangeListener> changeListeners;

    /**
     * Creates a Board with a 10x10 {@link Square} grid and 5 unplaced
     * {@link Ship}s
     * 
     * @param ownBoard
     *            Indicates whether it is its own Board
     */
    public Board(boolean ownBoard) {
        this.ownBoard = ownBoard;
        squares = new Square[BOARD_DIMENSION][BOARD_DIMENSION];

        // populates the squares array
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                squares[i][j] = new Square(i, j, ownBoard);
            }
        }

        ships = new ArrayList<>();
        ships.add(new Ship(Ship.Type.AIRCRAFT_CARRIER));
        ships.add(new Ship(Ship.Type.BATTLESHIP));
        ships.add(new Ship(Ship.Type.DESTROYER));
        ships.add(new Ship(Ship.Type.PATROL_BOAT));
        ships.add(new Ship(Ship.Type.SUBMARINE));

        this.changeListeners = new ArrayList<>();
    }

    /**
     * Validates a Board by checking its {@link Ship} positions are correct
     * 
     * @param board
     *            The Board which is having its validity tested
     * @return true if the Board and its {@link Ship}s are valid(i.e. not
     *         overlapping or over the edge of the board), otherwise false
     */
    public static boolean isValid(Board board) {
        Board tempBoard = new Board(true);
        for (Ship s : board.getShips()) {
            if (s.getSquares().size() == 0) {
                return false;
            }
            int[] tl = s.getTopLeft();
            Ship tempBoardShip = tempBoard.findShipByType(s.getType());
            tempBoardShip.setVertical(s.isVertical());
            if (!tempBoard.placeShip(tempBoardShip, tl[0], tl[1])) {
                return false;
            }
        }
        return tempBoard.shipPlacementEquals(board);
    }

    /**
     * Checks whether the {@link Ship} positions are locked
     * 
     * @return true if the {@link Ship} position is locked, otherwise false
     */
    public boolean isBoatPositionLocked() {
        return boatPositionLocked;
    }

    /**
     * Sets the {@link Ship} positions to be locked or unlocked
     * 
     * @param boatPositionLocked
     *            True to lock the {@link Ship} positions, false to unlock
     */
    public void setBoatPositionLocked(boolean boatPositionLocked) {
        this.boatPositionLocked = boatPositionLocked;
        client.getView().setSendShipState(!boatPositionLocked);
        firePropertyChange("resetSelectedShip", null, null);
    }

    /**
     * Checks whether it is its own Board
     * 
     * @return true if it is own, otherwise false
     */
    public boolean isOwnBoard() {
        return (ownBoard);
    }

    /**
     * Gets a {@link Square} from the Board
     * 
     * @param x
     *            The index of the {@link Square} on the X-axis
     * @param y
     *            The index of the {@link Square} on the Y-axis
     * @return The {@link Square} at the provided co-ordinates on the Board
     */
    public Square getSquare(int x, int y) {
        return squares[x][y];
    }

    /**
     * Places a {@link Ship} on the Board with the top-left {@link Square} of
     * the {@link Ship} at the given co-ordinates
     * 
     * @param ship
     *            The {@link Ship} to be placed on the Board
     * @param x
     *            The index of the {@link Square} on the X-axis
     * @param y
     *            The index of the {@link Square} on the Y-axis
     * @return true if the {@link Ship} has been placed on the Board, false if
     *         it can't be placed there due to overlapping or being off the
     *         Board
     */
    public boolean placeShip(Ship ship, int x, int y) {
        // checks if it is within the board
        int end = (ship.isVertical()) ? y + ship.getLength() - 1 : x
                + ship.getLength() - 1;
        if (x < 0 || y < 0 || end >= BOARD_DIMENSION) {
            return false;
        }

        // checks for overlapping
        for (int i = 0; i < ship.getLength(); i++) {
            if (ship.isVertical()) {
                if (squares[x][y + i].isShip())
                    return false;
            } else {
                if (squares[x + i][y].isShip())
                    return false;
            }
        }

        // puts ship on squares
        for (int i = 0; i < ship.getLength(); i++) {
            if (ship.isVertical()) {
                squares[x][y + i].setShip(ship);
                ship.setSquare(squares[x][y + i]);
            } else if (!ship.isVertical()) {
                squares[x + i][y].setShip(ship);
                ship.setSquare(squares[x + i][y]);
            }
        }

        return true;
    }

    /**
     * Picks up the {@link Ship} from the Board and clears its {@link Square}s
     * 
     * @param ship
     *            The {@link Ship} to pick up
     */
    public void pickUpShip(Ship ship) {
        for (Square s : ship.getSquares()) {
            s.setShip(null);
        }
        ship.clearSquares();
    }

    /**
     * Checks if the game is over(i.e. if all the {@link Ship}s are sunk)
     * 
     * @return true if all {@link Ship}s are sunk, false otherwise
     */
    public boolean gameOver() {
        for (Ship ship : ships) {
            if (!ship.isSunk())
                return false;
        }
        return true;
    }

    /**
     * Prints the Board to the console, showing the status of each
     * {@link Square}
     * 
     * @param clean
     */
    public void printBoard(boolean clean) {
        for (int i = 0; i < BOARD_DIMENSION; ++i) {
            for (int j = 0; j < BOARD_DIMENSION; ++j) {
                Square s = squares[j][i];
                Ship ship = s.getShip();
                char c = '-';
                if (s.isGuessed() && !clean
                        && s.getState() == Square.State.CONTAINS_SHIP) {
                    c = 'X';
                } else if (s.isGuessed() && !clean) {
                    c = 'O';
                } else if (ship != null) {
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
                        c = 'P';
                    }
                }
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    /**
     * Gets all of the {@link Ship}s belonging to the Board
     * 
     * @return an ArrayList of the {@link Ship}s from the Board (the
     *         {@link Ship}s might not be placed on the Board yet)
     */
    public ArrayList<Ship> getShips() {
        return ships;
    }

    /**
     * Applies a move to the Board, updating the {@link Square} and sinking the
     * {@link Ship} if necessary
     * 
     * @param move
     *            The {@link MoveResponseMessage} being applied to the Board
     */
    public void applyMove(MoveResponseMessage move) {
        Ship ship = move.shipSank();
        if (ship != null) {
            ship.sink();
            if (!ownBoard) {
                ship.updateSquareReferences(this);
                ships.add(ship);
                firePropertyChange("sankShip", null, ship);
            }
            for (Square shipSquare : ship.getSquares()) {
                Square boardSquare = getSquare(shipSquare.getX(),
                        shipSquare.getY());
                boardSquare.update(true, ship);
            }
            // TODO: Fix me
            client.getView().addChatMessage("SUNK SHIP" + ship.toString());
        } else {
            Square square = getSquare(move.getX(), move.getY());
            square.update(move.isHit(), null);
        }
    }

    /**
     * Checks if two Boards have identical {@link Ship} positions
     * 
     * @param board
     *            The Board which this Board is being compared against
     * @return true if the Boards have {@link Ship}s in identical positions
     */
    public boolean shipPlacementEquals(Board board) {
        for (int y = 0; y < BOARD_DIMENSION; ++y) {
            for (int x = 0; x < BOARD_DIMENSION; ++x) {
                Square s1 = this.getSquare(x, y);
                Square s2 = board.getSquare(x, y);
                if ((s1.isShip() != s2.isShip())) {
                    return false;
                }
                if (s1.getShip() != null && s2.getShip() != null
                        && s1.getShip().getType() != s2.getShip().getType()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Ship findShipByType(Ship.Type type) {
        for (Ship s : ships) {
            if (s.getType() == type) {
                return s;
            }
        }
        return null;
    }

    /**
     * Checks if this {@link Square} is next to a {@link Ship} horizontally,
     * vertically or diagonally
     * 
     * @param square
     *            The {@link Square} which is being checked for nearby
     *            {@link Ship}s
     * @return true if there is a {@link Ship} next to this {@link Square},
     *         false otherwise
     */
    public boolean isSquareNearShip(Square square) {
        for (int x = square.getX() - 1; x <= square.getX() + 1; x++) {
            for (int y = square.getY() - 1; y <= square.getY() + 1; y++) {
                if (isCoordWithinBounds(x, y) && getSquare(x, y).isShip()
                        && !(x == square.getX() && y == square.getY())) {
                    return true;
                }
            }
        }
        return false;
    }

    // checks if x and y are between 0 and 9 inclusive
    private boolean isCoordWithinBounds(int x, int y) {
        return (x >= 0 && x < 10 && y >= 0 && y < 10);
    }

    /**
     * Sends a move at the provided co-ordinates to the {@link Client}'s
     * ObjectOutputStream
     * 
     * @param x
     *            The index of the move on the X-axis
     * @param y
     *            The index of the move on the Y-axis
     * @throws IOException
     */
    public void sendMove(int x, int y) throws IOException {
        client.sendMove(x, y);
    }

    /**
     * Adds a new PropertyChangeListener to the Board
     * 
     * @param listener
     *            The PropertyChangeListener which is being added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Fires a PropertyChangeEvent when a {@link Ship} is rotated
     */
    public void selectedShipRotated() {
        firePropertyChange("rotateSelectedShip", null, null);
    }

    /**
     * Gets the Board's {@link Client}
     * 
     * @return the Board's {@link Client}
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the Board's {@link Client}
     * 
     * @param client
     *            The new {@link Client} for the Board
     */
    public void setClient(Client client) {
        this.client = client;
    }

    private void firePropertyChange(String property, Object oldValue,
            Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property,
                oldValue, newValue);
        for (PropertyChangeListener listener : changeListeners) {
            listener.propertyChange(event);
        }
    }
}
