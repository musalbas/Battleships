package logic;

import java.io.Serializable;

public class Square implements Serializable {
	private Ship ship;
	private boolean hit;
	private int x, y;

	public Square(int x, int y, boolean ownBoard) {
		this.ship = null;
		this.hit = false;
		this.x = x;
		this.y = y;
	}

	public boolean isShip() {
		return (ship != null);
	}

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean b) {
		if (ship != null)
			ship.gotHit();
		hit = b;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}