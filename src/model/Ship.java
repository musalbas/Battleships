package model;

import view.ShipView;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable {

    private Type type;
    private ArrayList<Square> squares;
    private boolean vertical;
    private int health;
    private transient ShipView view;

    // ////////////////////////// private int headX, headY;

    public Ship(Type type) {
        this.type = type;
        this.vertical = false;
        this.health = type.length;
        squares = new ArrayList<Square>();
    }

    public int getLength() {
        return type.length;
    }

    public Type getType() {
        return type;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean b) {
        this.vertical = b;
    }

    public ArrayList<Square> getSquares() {
        return squares;
    }

    public void setSquare(Square square) {
        this.squares.add(square);
    }

    public void clearSquares() {
        squares.clear();
    }

    public void gotHit() {
        health--;
    }

    public boolean isSunk() {
        return (health == 0);
    }

    public void sink() {
        health = 0;
    }

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

    public void setView(ShipView view) {
        this.view = view;
    }

    public enum Type {
        AIRCRAFT_CARRIER(5, "aircraft carrier"), BATTLESHIP(4, "battleship"), SUBMARINE(
                3, "submarine"), DESTROYER(3, "destroyer"), PATROL_BOAT(2,
                "patrol boat");

        private int length;
        private String name;

        Type(int length, String name) {
            this.length = length;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public void updateSquareReferences(Board board) {
        ArrayList<Square> newSquares = new ArrayList<>();
        for (Square s : squares) {
            newSquares.add(board.getSquare(s.getX(), s.getY()));
        }
        this.squares = newSquares;
    }

}
