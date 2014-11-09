package server.messages;

import java.io.Serializable;

/**
 * An object sent from a player containing the coordinates of the move they
 * wish to make, which refers to the {@link model.Square} they are guessing.
 */
public class MoveMessage implements Serializable {

    private int x;
    private int y;

    /**
     * Constructs a MoveMessage with coordinates x and y.
     *
     * @param x x coordinate of guess
     * @param y y coordinate of guess
     */
    public MoveMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of the move.
     *
     * @return x coordinate of guess
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y coordinate of the move.
     *
     * @return y coordinate of guess
     */
    public int getY() {
        return this.y;
    }
}
