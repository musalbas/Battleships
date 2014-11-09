package server.messages;

import java.io.Serializable;

public class MoveMessage implements Serializable {

    private int x;
    private int y;

    public MoveMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
