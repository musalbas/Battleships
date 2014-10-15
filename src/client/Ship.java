package client;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by user on 13.10.2014.
 */
public class Ship {
    private int length;
    private int cellSize;
    private int x;
    private int y;
    private boolean horizontal;
    private boolean onBoard;
    private BufferedImage image; // TODO ship image. If we decide to replace a squares with proper image of a ship.

    public Ship(int length, int cellSize, int x, int y) {
        this.length = length;
        this.cellSize = cellSize;
        this.x = x;
        this.y = y;
        horizontal = false;
        onBoard = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean has(int x, int y) {
        return this.x <= x && x <= this.x + length * cellSize && this.y <= y && y <= this.y + cellSize;
    }

    public void setOnBoard(boolean onBoard) {
        this.onBoard = onBoard;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void paint(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, length * cellSize, cellSize);
    }
}
