package client;

import model.Ship;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by user on 13.10.2014.
 */
public class ShipView {
    private final int initialX;
    private final int initialY;
	private int length;
	private int cellSize;
	private int x;
    private int y;
    private boolean horizontal;
    private boolean selected;
    private BufferedImage image; // TODO ship image. If we decide to replace a squares with proper image of a ship.
	private Ship model ;

    public ShipView(int length, int cellSize, int x, int y, Ship model) {
        this.length = length;
        this.cellSize = cellSize;
        this.x = this.initialX = x;
        this.y = this.initialY = y;
	    this.model = model ;
        horizontal = true;
        selected = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
        if (horizontal) {
            return this.x <= x && x <= this.x + length * cellSize && this.y <= y && y <= this.y + cellSize;
        } else {
            return this.x <= x && x <= this.x + cellSize && this.y <= y && y <= this.y + length * cellSize;
        }
    }

    public void resetPosition() {
        setX(initialX);
        setY(initialY);
    }

	public Ship getModel () {
		return model;
	}

	public void rotate() {
        horizontal = !horizontal;
	    model.setVertical( !horizontal ) ;
    }

    public void paint(Graphics g) {
        if (selected) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.GRAY);
        }
        if (horizontal) {
            g.fillRect(x, y, length * cellSize, cellSize);
        } else {
            g.fillRect(x, y, cellSize, length * cellSize);
        }
    }
}
