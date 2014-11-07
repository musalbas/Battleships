package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by user on 6-Nov-14.
 */
public class LabeledBoardView extends JPanel {

    public LabeledBoardView(BoardView boardView) {
        super(new BorderLayout());
        add(new HorizontalBoardLabels(), BorderLayout.NORTH);
        add(new VerticalBoardLabels(), BorderLayout.WEST);
        add(boardView, BorderLayout.CENTER);
    }

    static class VerticalBoardLabels extends JPanel {

        private final int SQUARE_WIDTH = BoardView.SQUARE_WIDTH;
        private static final int WIDTH = 25;

        public VerticalBoardLabels() {
            setPreferredSize(new Dimension(WIDTH, SQUARE_WIDTH * 10 + 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 1; i <= 10; ++i) {
                int x = (WIDTH - fm.stringWidth(Integer.toString(i))) / 2;
                int y = i * SQUARE_WIDTH - (SQUARE_WIDTH - fm.getAscent()) / 2;
                g.drawString(Integer.toString(i), x, y);
            }
        }
    }

    static class HorizontalBoardLabels extends JPanel {
        private static final int SQUARE_WIDTH = BoardView.SQUARE_WIDTH;
        private static final int HEIGHT = 25;

        public HorizontalBoardLabels() {
            setPreferredSize(new Dimension(SQUARE_WIDTH * 11 + 1, HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < 10; ++i) {
                char label = (char) ('A' + i);
                int x = (SQUARE_WIDTH - fm.stringWidth(Character.toString(label))) / 2
                        + SQUARE_WIDTH * i + VerticalBoardLabels.WIDTH;
                int y = (HEIGHT + fm.getAscent()) / 2;
                g.drawString(Character.toString(label), x, y);
            }
        }
    }

}
