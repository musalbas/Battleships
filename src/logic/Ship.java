package logic;

import client.ShipView;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable {

	private Type type;
	private ArrayList<Square> squares;
	private boolean vertical;
	private int health;
	private ShipView view ;

	// ////////////////////////// private int headX, headY;

	public Ship(Type type) {
		this.type = type;
		this.vertical = false;
		this.health = type.length;
		squares = new ArrayList<Square>();
	}

	public enum Type {
		AIRCRAFT_CARRIER(5), BATTLESHIP(4), SUBMARINE(3), DESTROYER(3), PATROL_BOAT(
				2);

		private int length;

		Type(int length) {
			this.length = length;
		}
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
        int[] tl = {firstSquare.getX(), firstSquare.getY()};
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

	public void setView (ShipView view) {
		this.view = view;
	}

}
