package server.messages;

import java.io.Serializable;

import logic.Ship;

public class MoveResponseMessage extends ServerMessage implements Serializable {

    private int x;
    private int y;
    private Ship shipSunk;
    private boolean hit;

    /**
     * Initialise a move response message where no ship was sunk.
     */
    public MoveResponseMessage(int x, int y, boolean hit) {
        this(x, y, null, hit);
    }

    /**
     * Initialise a move response message where a ship was sunk.
     */
    public MoveResponseMessage(int x, int y, Ship shipSunk, boolean hit) {
        this.x = x;
        this.y = y;
        this.shipSunk = shipSunk;
        this.hit = hit;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Ship shipSank() {
        return this.shipSunk;
    }

    public boolean isHit() {
        return hit;
    }

}
