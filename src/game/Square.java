package game;

import java.io.Serializable;

public class Square implements Serializable {

    private Ship ship;
    private boolean guessed;
    private State state;
    private int x;
    private int y;

    public Square(int x, int y, boolean ownBoard) {
        this.ship = null;
        this.guessed = false;
        if (ownBoard) {
            this.state = State.EMPTY;
        } else {
            this.state = State.UNKNOWN;
        }
        this.x = x;
        this.y = y;
    }

    public void removeShip() {
        ship = null;
        state = State.EMPTY;
    }

    public boolean containsShip(Ship ship) {
        return this.ship == ship;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    private enum State {
        UNKNOWN,
        CONTAINS_SHIP,
        EMPTY
    }

}
