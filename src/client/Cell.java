package client;

import java.awt.*;

/**
 * Created by user on 13.10.2014.
 */
public class Cell {

    public static final int CLEAR = 0;
    public static final int HOVER = 1;
    public static final int MISS = 2;
    public static final int HIT = 3;
    private boolean hasShip;
    private int x;
    private int y;
    private int width;
    private int height;
    private int state;

    /* (x,y)
           *----*
           |    |
           |    | height
           *----*
           width
     */
    public Cell(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        state = CLEAR;
    }

    public boolean mouseEntered(int x, int y) {
        return (this.x <= x && x <= this.x + width) && (this.y <= y && y <= this.y + height);
    }

    public int getState() {
        return state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void setState(int state) {
        if (state < 0 || state > 3) {
            throw new IllegalArgumentException("Invalid state");
        }
        this.state = state;
    }

    public void paint(Graphics g) {
        if (hasShip) {
            g.setColor(Color.BLACK);
            g.fillRect(x, y, width, height);
        } else {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            switch (state) {
                case HOVER:
                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                    break;
                case HIT:
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, width, height);
                    g.setColor(Color.RED);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    break;
                case MISS:
                    g.setColor(Color.RED);
                    g.drawLine(x, y, x + width, y + height);
                    g.drawLine(x + width, y, x, y + height);
                    break;
            }
        }
    }
}
