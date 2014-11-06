package test.server;

import model.Board;
import model.Ship;

import java.util.ArrayList;

public class TestBoards {

    public static Board getTestBoard(int i) {
        Board board = new Board(true);
        ArrayList<Ship> ships = board.getShips();
        switch (i) {
        case 1:
            ships.get(0).setVertical(false);
            board.placeShip(ships.get(0), 0, 0);
            board.placeShip(ships.get(1), 0, 1);
            board.placeShip(ships.get(2), 1, 1);
            board.placeShip(ships.get(3), 3, 1);
            board.placeShip(ships.get(4), 4, 4);
            break;
        case 2:
            ships.get(0).setVertical(false);
            board.placeShip(ships.get(0), 0, 9);
            board.placeShip(ships.get(1), 5, 4);
            ships.get(2).setVertical(true);
            board.placeShip(ships.get(2), 3, 5);
            board.placeShip(ships.get(3), 0, 2);
            board.placeShip(ships.get(4), 1, 4);
        }
        return board;
    }
}
