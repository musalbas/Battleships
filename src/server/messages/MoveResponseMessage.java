package server.messages;

import model.Ship;

import java.io.Serializable;

/**
 * An object that is sent to both clients in response to a valid move,
 * containing information about the {@link model.Square} the move was applied
 * to, if it hit a ship, and contains the {@link model.Ship} it sank, if any.
 */
public class MoveResponseMessage implements Serializable {

    private int x;
    private int y;
    private Ship shipSunk;
    private boolean hit;
    private boolean ownBoard;

    /**
     * Initialise a move response message where no ship was sunk.
     */
    public MoveResponseMessage(int x, int y, boolean hit, boolean ownBoard) {
        this(x, y, null, hit, ownBoard);
    }

    /**
     * Constructs a MoveResponseMessage with coordinates of the
     * {@link model.Square} the move was applied to, a boolean stating if the
     * ship was sunk, the {@link model.Ship} that was sunk as a result from the
     * move, which is null if no {@link model.Ship} was sunk, and a boolean
     * stating if the MoveResponseMessage is to be applied to a player's own
     * board of their opponents.
     *
     * @param x x coordinate of the {@link model.Square}
     * @param y y coordinate of the {@link model.Square}
     * @param shipSunk the ship that was sunk, if any
     * @param hit true if the move hit a ship
     * @param ownBoard true if receiving player's own board
     */
    public MoveResponseMessage(int x, int y, Ship shipSunk, boolean hit,
            boolean ownBoard) {
        this.x = x;
        this.y = y;
        this.shipSunk = shipSunk;
        this.hit = hit;
        this.ownBoard = ownBoard;
    }

    /**
     * Returns the x coordinate of the {@link model.Square} involved in the
     * move.
     *
     * @return x coordinate of the {@link model.Square}
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the {@link model.Square} involved in the
     * move.
     *
     * @return y coordinate of the {@link model.Square}
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the {@link model.Ship} that was sunk in the move. Returns null
     * if the move did not result in a ship sinking.
     * @return the ship that was sunk, if any
     */
    public Ship shipSank() {
        return this.shipSunk;
    }

    /**
     * Returns true if the move hit a ship.
     * @return true if move hit a ship
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Returns true if the MoveResponseMessage is in response to a move related
     * to the player's own {@link model.Board}, that is the board they placed
     * ships on. This will be false in response to a move a player submitted.
     * @return true if own board
     */
    public boolean isOwnBoard() {
        return ownBoard;
    }

    /**
     * Sets whether the related board is the client due to receive the message's
     * own {@link model.Board}.
     * @param ownBoard tue if own board
     */
    public void setOwnBoard(boolean ownBoard) {
        this.ownBoard = ownBoard;
    }

}
