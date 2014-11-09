package view;

import model.Square;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;

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
    private Image water;
    private Image splash;
    private BoardView boardView;
    private Square squareModel;


    public SquareView(int x, int y, int width, int height, BoardView boardView,
                      Square squareModel) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        state = CLEAR;
        this.boardView = boardView;
        this.squareModel = squareModel;
        squareModel.addChangeListener(this);
        try {
            water = ImageIO.read(new File("resources/water/water.png"));
            splash = ImageIO.read(new File("resources/water/splash.png"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Some files have been deleted.", "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    public boolean animated() {
        return explosionImage != null;
    }

    public void paint(Graphics g) {
        g.drawImage(water, x, y, width, height, null);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        if (state == HOVER && !animated()) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
        if (state == MISS) {
            g.drawImage(splash, x, y, width, height, null);
        }
    }

    public void drawCross(Graphics g) {
        final int padding = 5;
        g.setColor(Color.RED);
        g.drawLine(x + padding, y + padding, x + width - padding, y + height
                - padding);
        g.drawLine(x + width - padding, y + padding, x + padding, y + height
                - padding);
    }

    public void drawExplosion(Graphics g) {
        if (explosionImage != null) {
            g.drawImage(explosionImage, x, y, width, height, null);
        }
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

        if (!boardView.getModel().isOwnBoard()) {
            if (state == SquareView.HIT) {
                new ExplosionAnimation(this, boardView).start();
            }
        }
        boardView.repaint();
    }

}
