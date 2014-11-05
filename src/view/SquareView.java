package view;

import model.Square;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by user on 13.10.2014.
 */
public class SquareView implements ChangeListener {

    public static final int CLEAR = 0;
    public static final int HOVER = 1;
    public static final int MISS = 2;
    public static final int HIT = 3;
    private int x;
    private int y;
    private int width;
    private int height;
    private int state;
    private Image explosionImage;
    private BoardView boardView;
    private Square squareModel;

    /* (x,y)
           *----*
           |    | height
           *----*
           width
     */
    public SquareView(int x, int y, int width, int height, BoardView boardView, Square squareModel) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        state = CLEAR;
        this.boardView = boardView;
        this.squareModel = squareModel;
        squareModel.addChangeListener(this);
    }

    public boolean mouseEntered(int x, int y) {
        return (this.x <= x && x <= this.x + width) && (this.y <= y && y <= this.y + height);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state < 0 || state > 3) {
            throw new IllegalArgumentException("Invalid state");
        }
        this.state = state;
    }

    public void setExplosionImage(Image explosionImage) {
        this.explosionImage = explosionImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private boolean animationDisplayed() {
        return explosionImage != null;
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        switch (state) {
            case HOVER:
                if (!animationDisplayed()) {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, width, height);
                }
                break;
            case HIT:
                g.setColor(Color.GRAY);
                g.fillRect(x, y, width, height);
                g.setColor(Color.RED);
                if (!animationDisplayed()) {
                    drawCross(g);
                }
                break;
            case MISS:
                g.setColor(Color.RED);
                drawCross(g);
                break;
        }

        if (explosionImage != null) {
            g.drawImage(explosionImage, x, y, width, height, null);
        }
    }

    private void drawCross(Graphics g) {
        final int padding = 5;
        g.drawLine(x + padding, y + padding, x + width - padding, y + height - padding);
        g.drawLine(x + width - padding, y + padding, x + padding, y + height - padding);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        switch (squareModel.getState()) {
            case CONTAINS_SHIP:
                this.state = HIT;
                break;
            case NO_SHIP:
                this.state = MISS;
        }
        boardView.setCellState(this, this.state);
        boardView.repaint();
    }

}
