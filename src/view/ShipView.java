package view;

import model.Ship;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

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
    private Image horizontalImage;
    private Image verticalImage;
    private Ship model;

    public ShipView(int length, int cellSize, int x, int y, Ship model) {
        this.length = length;
        this.cellSize = cellSize;
        this.x = this.initialX = x;
        this.y = this.initialY = y;
        this.model = model;
        horizontal = true;
        selected = false;

        String filename = "resources/ships/" + model.getType().getName();
        try {
            horizontalImage = ImageIO.read(new File(filename + ".png"));
            verticalImage = ImageIO.read(new File(filename + "_v.png"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Some files have been deleted",
                    "Fatal error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
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
            return this.x <= x && x <= this.x + length * cellSize
                    && this.y <= y && y <= this.y + cellSize;
        } else {
            return this.x <= x && x <= this.x + cellSize && this.y <= y
                    && y <= this.y + length * cellSize;
        }
    }

    public void resetPosition() {
        horizontal = true;
        model.setVertical(false);
        setX(initialX);
        setY(initialY);
    }

    public Ship getModel() {
        return model;
    }

    public void rotate() {
        horizontal = !horizontal;
        model.setVertical(!horizontal);
    }

    public void paint(Graphics g) {

        if (horizontal) {
            if (selected) {
                g.setColor(Color.GREEN);
                g.fillRect(x, y, length * cellSize, cellSize);
            }
            g.drawImage(horizontalImage, x, y, length * cellSize, cellSize,
                    null);
        } else {
            if (selected) {
                g.setColor(Color.GREEN);
                g.fillRect(x, y, cellSize, length * cellSize);
            }
            g.drawImage(verticalImage, x, y, cellSize, length * cellSize, null);
        }
    }
}
