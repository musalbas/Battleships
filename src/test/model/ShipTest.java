package test.model;

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A JUnit class for testing {@link model.Ship}.
 * @author Olly
 */
public class ShipTest {

    /**
     * Test method for {@link model.Ship#getTopLeft()}.
     */
    @Test
    public void getTopLeftTest() {
        Assert.assertTrue("Is the top-left square returned",
                isTopLeftCoordsReturned());
    }

    private boolean isTopLeftCoordsReturned() {

        model.Board board = new model.Board(true);
        model.Ship ship1 = board.getShips().get(0); // AIRCRAFT_CARRIER
        model.Ship ship2 = board.getShips().get(1); // BATTLESHIP
        ship2.setVertical(true);

        board.placeShip(ship1, 2, 2);
        board.placeShip(ship2, 6, 4);

        return Arrays.equals(ship1.getTopLeft(), new int[] { 2, 2 })
                && Arrays.equals(ship2.getTopLeft(), new int[] { 6, 4 });
    }
}
