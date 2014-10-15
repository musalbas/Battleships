package game;

import java.io.Serializable;
import java.util.ArrayList;

public class Board implements Serializable {

    private Square[][] squares;
    private ArrayList<Ship> ships;
    private int width;
    private int height;
    private boolean ownBoard;

    public Board(int width, int height, boolean ownBoard) {
        this.width = width;
        this.height = height;
        this.ownBoard = ownBoard;
        squares = new Square[width][height];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                squares[j][i] = new Square(j, i, ownBoard);
            }
        }
        ships = new ArrayList<Ship>();
        ships.add(new Ship(Ship.Type.AIRCRAFT_CARRIER));
        ships.add(new Ship(Ship.Type.BATTLESHIP));
        ships.add(new Ship(Ship.Type.DESTROYER));
        ships.add(new Ship(Ship.Type.PATROL_BOAT));
        ships.add(new Ship(Ship.Type.SUBMARINE));
    }

    public boolean setShipLocation(Ship ship, int x, int y,
                                   boolean horizontal) {
        if (!ships.contains(ship)) {
            return false; // doesn't belong on board
        }
        int endX = (horizontal) ? x + ship.getLength() - 1 : x;
        int endY = (horizontal) ? y : y + ship.getLength() - 1;
        if (x < 0 || y < 0 || endX >= width || endY >= height) {
            return false; // doesn't fit on board
        }
        Square[] shipSquares = new Square[ship.getLength()];
        for (int i = 0, xi = 0, yi = 0; i < shipSquares.length; ++i) {
            if (horizontal) {
                shipSquares[i] = squares[x + i][y];
            } else {
                shipSquares[i] = squares[x][y + i];
            }
        }
        for (Square s : shipSquares) {
            if (s.getShip() != null || s.containsShip(ship)) {
                return false; // square already taken by another ship
            }
        }
        for (Square s : ship.getSquares()) {
            if (s != null && s.containsShip(ship)) {
                s.removeShip(); // removes ship from squares
            }
        }
        ship.setSquares(shipSquares, horizontal);
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
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
}
