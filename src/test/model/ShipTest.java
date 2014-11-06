/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author User
 */
public class ShipTest {

    @Test
    public void getTopLeftTest() {
        Assert.assertTrue("Is the top-left square returned",
                isTopLeftCoordsReturned());
    }

    public boolean isTopLeftCoordsReturned() {

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
