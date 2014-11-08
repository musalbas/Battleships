package model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Square on the {@link Board} which can contain a {@link Ship} and can also 
 * be guessed by the player.
 */
public class Square implements Serializable {
    private Ship ship;
    private boolean guessed;
    private int x, y;
    private State state;
    private transient ArrayList<ChangeListener> changeListeners;

    /**
     * Constructs a Square with the given co-ordinates. By default the Square 
     * has no {@link Ship} on it and is not guessed. If it is on the player's 
     * own {@link Board}, the initial {@link State} is NO_SHIP; if it is not 
     * on the player's own Board then the {@link State} is UNKOWN.
     * @param x
     *          The index of the Square on the X-axis of the 
     *          {@link Board}.
     * @param y
     *          The index of the Square on the Y-axis of the 
     *          {@link Board}.
     * @param ownBoard 
     *          Indicates whether the {@link Board} belongs to the player
     */
    public Square(int x, int y, boolean ownBoard) {
        this.ship = null;
        this.guessed = false;
        this.x = x;
        this.y = y;
        this.state = (ownBoard) ? State.NO_SHIP : State.UNKNOWN;
        this.changeListeners = new ArrayList<>();
    }

    /**
     * Gets whether there is a {@link Ship} on this Square.
     * @return true if there is a {@link Ship} on the Square, false otherwise
     */
    public boolean isShip() {
        return (ship != null);
    }

    /**
     * Gets the {@link Ship} on this Square. 
     * @return the {@link Ship} on this Square, returns null if there is no 
     * the {@link Ship} currently on the Square
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Sets the {@link Ship} on the Square and updates the {@link State}.
     * @param ship 
     *          The new the {@link Ship} on the Square
     */
    public void setShip(Ship ship) {
        this.ship = ship;
        this.state = State.CONTAINS_SHIP;
    }

    /**
     * Gets whether the Square has been guessed.
     * @return true if guessed, false otherwise
     */
    public boolean isGuessed() {
        return guessed;
    }

    /**
     * Sets whether the Square has been guessed and reduces the health of the 
     * the {@link Ship} on the Square.
     * @param b 
     *          Indicates whether Square has been guessed
     */
    public void setGuessed(boolean b) {
        if (ship != null)
            ship.gotHit();
        guessed = b;
    }

    /**
     * Guesses the Square and reduces the health of the {@link Ship} if there 
     * is one on the Square.
     * @return true if there is a {@link Ship} on the Square, false otherwise
     */
    public boolean guess() {
        guessed = true;
        if (ship != null) {
            ship.gotHit();
            return true;
        }
        return false;
    }

    /**
     * Updates the Square to be guessed, sets {@link State} depending on if 
     * there is a {@link Ship} on it and reduces the health of the {@link Ship}.
     * @param hit
     *          Indicates if there is a {@link Ship} on the Square to hit
     * @param shipSunk 
     *          The new {@link Ship} to update the Square with
     */
    public void update(boolean hit, Ship shipSunk) {
        this.guessed = true;
        if (this.state == State.UNKNOWN) {
            this.state = (hit) ? State.CONTAINS_SHIP : State.NO_SHIP;
        } else if (this.ship != null) {
            ship.gotHit();
        }
        if (this.ship == null) {
            this.ship = shipSunk;
        }
        fireChange();
    }

    /**
     * Gets the index of the Square on the X-axis of the {@link Board}.
     * @return the index of the Square on the X-axis of the {@link Board}
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the index of the Square on the Y-axis of the {@link Board}.
     * @return the index of the Square on the Y-axis of the {@link Board}
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the current {@link State} of the Square.
     * @return the {@link State} of the Square
     */
    public State getState() {
        return state;
    }

    /**
     * The {@link State} of the Square indicating if there is a {@link Ship} or 
     * if it is unknown if there is a {@link Ship}.
     */
    public enum State {
        CONTAINS_SHIP, NO_SHIP, UNKNOWN
    }

    /**
     * Adds a new ChangeListener to the Square.
     * @param listener 
     *          The new ChangeListener for the Square
     */
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    private void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }
}