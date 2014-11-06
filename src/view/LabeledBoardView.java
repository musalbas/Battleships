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

    /**
     * Created by user on 6-Nov-14.
     */
    static class VerticalBoardLabels extends JPanel {

        private int squareSize;
        private static final int WIDTH = 25;

        public VerticalBoardLabels() {
            squareSize = BoardView.CELL_SIZE;
            setPreferredSize(new Dimension(WIDTH, squareSize * 10 + 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 1; i <= 10; ++i) {
                int x = (WIDTH - fm.stringWidth(Integer.toString(i))) / 2;
                int y = i * squareSize - (squareSize - fm.getAscent()) / 2;
                g.drawString(Integer.toString(i), x, y);
            }
        }
    }

    static class HorizontalBoardLabels extends JPanel {
        private int squareSize;
        private static final int HEIGHT = 25;

        public HorizontalBoardLabels() {
            squareSize = BoardView.CELL_SIZE;
            setPreferredSize(new Dimension(squareSize * 11 + 1, HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < 10; ++i) {
                char label = (char) ('A' + i);
                int x = (squareSize - fm.stringWidth(Character.toString(label))) / 2
                        + squareSize * i + VerticalBoardLabels.WIDTH;
                int y = (HEIGHT + fm.getAscent()) / 2;
                g.drawString(Character.toString(label), x, y);
            }
        }
    }

}
