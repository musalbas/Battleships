package model;

import view.ShipView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Ship which can be placed on the {@link Board} for the other player to fire 
 * at. It has a length of 2-5 (inclusive) {@link Square}s and must have a 
 * {@link Type}.
 */
public class Ship implements Serializable {

    private Type type;
    private ArrayList<Square> squares;
    private boolean vertical;
    private int health;
    private transient ShipView view;

    // ////////////////////////// private int headX, headY;

    /**
     * Constructs a Ship with its health and number of {@link Square}s 
     * dependant on the {@link Type} provided. The default orientation is 
     * horizontal.
     * @param type
     *          The type of the Ship
     */
    public Ship(Type type) {
        this.type = type;
        this.vertical = false;
        this.health = type.length;
        squares = new ArrayList<Square>();
    }

    /**
     * Gets the length of the Ship.
     * @return the length of the Ship (the number of {@link Square}s it covers)
     */
    public int getLength() {
        return type.length;
    }

    /**
     * Gets the {@link Type} of the Ship.
     * @return the {@link Type} of the Ship
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the orientation of the Ship.
     * @return true if Ship is vertical, false if Ship is horizontal
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * Sets the orientation of the Ship.
     * @param b 
     *          true for vertical, false for horizontal
     */
    public void setVertical(boolean b) {
        this.vertical = b;
    }

    /**
     * Gets all of the {@link Square}s that the Ship is on (its location on 
     * the {@link Board}).
     * @return an ArrayList of {@link Square}s
     */
    public ArrayList<Square> getSquares() {
        return squares;
    }

    /**
     * Adds a {@link Square} to the list of {@link Square}s the Ship is on.
     * @param square 
     *          The new {@link Square} to be added to the list of 
     *          {@link Square}s
     */
    public void setSquare(Square square) {
        this.squares.add(square);
    }

    /**
     * Removes all of the {@link Square}s from the Ship.
     */
    public void clearSquares() {
        squares.clear();
    }

    /**
     * Reduces the health of the Ship by 1 when it is hit.
     */
    public void gotHit() {
        health--;
    }

    /**
     * Gets whether the Ship is sunk or still afloat.
     * @return true if Ship has been sunk, false otherwise
     */
    public boolean isSunk() {
        return (health == 0);
    }

    /**
     * Reduces the health of the Ship to 0, regardless of previous level of 
     * health.
     */
    public void sink() {
        health = 0;
    }

    /**
     * Gets the co-ordinates of the top-left (the head) {@link Square} of the 
     * Ship which is used for positioning the Ship.
     * @return 
     *          the co-ordinates of the top-left (the head) {@link Square} of the Ship
     */
    public int[] getTopLeft() {
        Square firstSquare = squares.get(0);
        int[] tl = { firstSquare.getX(), firstSquare.getY() };
        for (int i = 1; i < squares.size(); ++i) {
            Square s = squares.get(i);
            if (s.getX() < tl[0]) {
                tl[0] = s.getX();
            }
            if (s.getY() < tl[1]) {
                tl[1] = s.getY();
            }
        }
        return tl;
    }

    /**
     * Sets the {@link ShipView} belonging to the Ship. This is used for displaying the 
     * Ship in the GUI.
     * @param view 
     *          The {@link ShipView} used for displaying the Ship
     */
    public void setView(ShipView view) {
        this.view = view;
    }

    /**
     * The different types of Ship available, with lengths and names of types
     */
    public enum Type {
        AIRCRAFT_CARRIER(5, "aircraft carrier"), BATTLESHIP(4, "battleship"), SUBMARINE(
                3, "submarine"), DESTROYER(3, "destroyer"), PATROL_BOAT(2,
                "patrol boat");

        private int length;
        private String name;

        /**
         * Constructs a new Type of Ship
         * @param length
         *          The length of the Type
         * @param name 
         *          The name of the Type
         */
        Type(int length, String name) {
            this.length = length;
            this.name = name;
        }

        /**
         * Gets the name of the Type
         * @return 
         *          the name of the Type
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Updates the list of {@link Square}s which indicate the position of the 
     * Ship to match board's {@link Square}s
     * @param board 
     *          The {@link Board} which is being used to update from
     */
    public void updateSquareReferences(Board board) {
        ArrayList<Square> newSquares = new ArrayList<>();
        for (Square s : squares) {
            newSquares.add(board.getSquare(s.getX(), s.getY()));
        }
        this.squares = newSquares;
    }

}
