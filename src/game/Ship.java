package game;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class Ship implements Serializable {

    private Type type;

    private Square[] squares;
    private boolean horizontal;

    public Ship(Type type) {
        this.type = type;
        squares = new Square[type.length];
    }

    public enum Type {
        AIRCRAFT_CARRIER(5),
        BATTLESHIP(4),
        SUBMARINE(3),
        DESTROYER(3),
        PATROL_BOAT(2);

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

    public boolean isHorizontal() {
        return horizontal;
    }

    public Square[] getSquares() {
        return squares;
    }

    public void setSquares(Square[] squares, boolean horizontal) {
        this.squares = squares;
        this.horizontal = horizontal;
        for (Square s : squares) {
            s.setShip(this);
        }
    }

}
