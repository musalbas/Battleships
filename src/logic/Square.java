package logic;

import java.io.Serializable;

public class Square implements Serializable {
	private Ship ship;
	private boolean guessed;
	private int x, y;
    private State state;

	public Square(int x, int y, boolean ownBoard) {
		this.ship = null;
		this.guessed = false;
		this.x = x;
		this.y = y;
        this.state = (ownBoard) ? State.NO_SHIP : State.UNKNOWN;
	}

	public boolean isShip() {
		return (ship != null);
	}

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
        this.state = State.CONTAINS_SHIP;
	}

	public boolean isGuessed() {
		return guessed;
	}

	public void setGuessed(boolean b) {
		if (ship != null)
            ship.gotHit();
		guessed = b;
	}

    public void update(boolean hit, Ship shipSunk) {
        this.guessed = true;
        if (this.state == State.UNKNOWN) {
            this.state = (hit) ? State.CONTAINS_SHIP : State.NO_SHIP;
            this.ship = shipSunk;
        } else if (this.ship != null) {
            ship.gotHit();
        }
    }

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

    public enum State {
        CONTAINS_SHIP,
        NO_SHIP,
        UNKNOWN
    }

}