package server.messages;

import java.io.Serializable;

import logic.Ship;

public class MoveResponseMessage extends ServerMessage implements Serializable {

    private int[] xyCo;
    private Ship shipHit = null;

    /**
     * Initialise a move response message where no ship was hit.
     */
    public MoveResponseMessage(int[] xyCo) {
        this.xyCo = xyCo;
    }

    /**
     * Initialise a move response message where a ship was hit.
     */
    public MoveResponseMessage(int[] xyCo, Ship shipHit) {
        this.xyCo = xyCo;
        this.shipHit = shipHit;
    }

    public int[] getXyCo() {
        return this.xyCo;
    }

    public Ship shipHit() {
        return this.shipHit;
    }

}
