package logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable {

	private Type type;
	private ArrayList<Square> squares;
	private boolean vertical;
	private int health;

	// ////////////////////////// private int headX, headY;

	public Ship(Type type) {
		this.type = type;
		this.vertical = true;
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

}
