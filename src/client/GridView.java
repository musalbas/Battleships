package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 13.10.2014.
 */
public class GridView extends JPanel {

    private static final int SIZE = 40;
    private Cell hoveredCell;
    private Cell[][] cells = new Cell[10][10];
    private ArrayList<Ship> ships = new ArrayList<Ship>();
    private Ship selectedShip;
    private int xDistance;
    private int yDistance;

    public GridView() {
        setPreferredSize(new Dimension(10 * SIZE + 1, 10 * SIZE + 50));
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
                cells[i][j] = new Cell(i * SIZE, j * SIZE, SIZE, SIZE);
            }
        }

        int x = 0;
        int y = SIZE * 10 + 5;
        final int length = 2;
        for (int i = 0; i < 4; i++) {
            ships.add(new Ship(length, SIZE, x, y));
            final int newPosition = x + length * SIZE + 5;
            if (newPosition + length * SIZE + 5 > SIZE * 10) {
                x = 0;
                y += SIZE + 5;
            } else {
                x = newPosition;
            }
        }
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (hoveredCell != null && hoveredCell.getState() == Cell.HOVER) {
                    hoveredCell.setState(Cell.CLEAR);
                }
                int x = e.getX();
                int y = e.getY();

                hoveredCell = getCell(x, y);
                if (hoveredCell != null && hoveredCell.getState() == Cell.CLEAR) {
                    hoveredCell.setState(Cell.HOVER);
                }
                getParent().repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShip != null) {
                    selectedShip.setX(e.getX() - xDistance);
                    selectedShip.setY(e.getY() - yDistance);
                }
                getParent().repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredCell != null) {
                    hoveredCell.setState(Cell.CLEAR);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                selectedShip = getShip(x, y);
                if (selectedShip != null) {
                    xDistance = e.getX() - selectedShip.getX();
                    yDistance = e.getY() - selectedShip.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (hoveredCell != null && hoveredCell.getState() == Cell.HOVER) {
                    hoveredCell.setState(Cell.CLEAR);
                }
                if (selectedShip != null) {
                    final int x = selectedShip.getX() + SIZE / 2;
                    final int y = selectedShip.getY() + SIZE / 2;
                    Cell hovered = getCell(x, y);
                    selectedShip.setX(hovered.getX());
                    selectedShip.setY(hovered.getY());
                } else {
                    int x = e.getX();
                    int y = e.getY();
                    hoveredCell = getCell(x, y);
                    if (hoveredCell != null) {
                        hoveredCell.setState(new Random().nextInt(2) + 2);
                    }
                }
                getParent().repaint();
            }
        });
    }

    private Ship getShip(int x, int y) {
        for (Ship ship : ships) {
            if (ship.has(x, y)) {
                return ship;
            }
        }
        return null;
    }

    private Cell getCell(int x, int y) {
        int i = x / SIZE;
        int j = y / SIZE;
        return i < 10 && j < 10 ? cells[i][j] : null;
    }

    public void paint(Graphics g) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.paint(g);
            }
        }
        for (Ship s : ships) {
            s.paint(g);
        }
    }
}
