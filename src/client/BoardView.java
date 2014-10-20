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
public class BoardView extends JPanel {

    private static final int BOARD_SIZE = 10;
    private static final int NUMBER_OF_BOATS = 5;
    private static final int[] BOATS_LENGTH = {2, 3, 3, 4, 5};
    private static final int CELL_SIZE = 40;
    private CellView hoveredCell = null;
    private ShipView selectedShipView = null;
    private CellView[][] viewCells = new CellView[BOARD_SIZE][BOARD_SIZE];
    private ArrayList<ShipView> viewShips = new ArrayList<ShipView>();
    private int xDistance;
    private int yDistance;

    public BoardView() {
        addCellsAndShips();
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoveredCell(e);
                setHoveredCell(e);
                repaintRoot();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateSelectedShip(e);
                repaintRoot();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setHoveredCellState(CellView.CLEAR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setSelectedShipView(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateHoveredCell(e);

                if (getSelectedShip() != null) {
                    moveSelectedShip();
                } else {
                    setHoveredCell(e);
                    final int state = new Random().nextInt(2) + 2;
                    setHoveredCellState(state);
                }
                repaintRoot();
            }
        });
    }

    /**
     * Calling repaint() works only on Mac and Linux, on Windows repaint() causes issues with components layout.
     * The whole JFrame has to repainted.
     */
    public void repaintRoot() {
        Component c = SwingUtilities.getWindowAncestor(this);
        if (c != null) {
            c.repaint();
        }
    }

    private void addCellsAndShips() {
        setPreferredSize(new Dimension((BOARD_SIZE + 5) * CELL_SIZE + 1, (BOARD_SIZE + 5) * CELL_SIZE + 50));
        setVisible(true);
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                setCell(i, j, new CellView(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE));
            }
        }

        int x = 0;
        int y = CELL_SIZE * BOARD_SIZE + 5;
        for (int i = 0; i < NUMBER_OF_BOATS; i++) {
            int length = BOATS_LENGTH[i];
            viewShips.add(new ShipView(length, CELL_SIZE, x, y));
            final int newPosition = x + length * CELL_SIZE + 5;
            if (newPosition + length * CELL_SIZE + 5 > CELL_SIZE * 10) {
                x = 0;
                y += CELL_SIZE + 5;
            } else {
                x = newPosition;
            }
        }
    }

    void setSelectedShipView(MouseEvent e) {
        System.out.println("got a selected ship");
        int x = e.getX();
        int y = e.getY();
        if (selectedShipView != null) {
            selectedShipView.setSelected(false);
        }
        selectedShipView = getShip(x, y);
        if (selectedShipView != null) {
            selectedShipView.setSelected(true);
            xDistance = e.getX() - selectedShipView.getX();
            yDistance = e.getY() - selectedShipView.getY();
        }
    }

    void moveSelectedShip() {
        final int x = selectedShipView.getX() + CELL_SIZE / 2;
        final int y = selectedShipView.getY() + CELL_SIZE / 2;
        CellView hovered = getCell(x, y);
        if (hovered != null) {
            selectedShipView.setX(hovered.getX());
            selectedShipView.setY(hovered.getY());
        } else {
            selectedShipView.resetPosition();
        }
    }

    private ShipView getShip(int x, int y) {
        for (ShipView shipView : viewShips) {
            if (shipView.has(x, y)) {
                return shipView;
            }
        }
        return null;
    }

    ShipView getSelectedShip() {
        return selectedShipView;
    }

    CellView getCell(int x, int y) {
        int i = x / CELL_SIZE;
        int j = y / CELL_SIZE;
        return i >= 0 && j >= 0 && i < 10 && j < 10 ? viewCells[i][j] : null;
    }

    void setCell(int i, int j, CellView cell) {
        viewCells[i][j] = cell;
    }

    void updateSelectedShip(MouseEvent e) {
        ShipView selectedShipView = getSelectedShip();
        if (selectedShipView != null) {
            selectedShipView.setX(e.getX() - xDistance);
            selectedShipView.setY(e.getY() - yDistance);
        }
    }

    void setHoveredCell(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        hoveredCell = getCell(x, y);
        if (hoveredCell != null && hoveredCell.getState() == CellView.CLEAR) {
            hoveredCell.setState(CellView.HOVER);
        }
    }

    void updateHoveredCell(MouseEvent e) {
        if (hoveredCell != null && hoveredCell.getState() == CellView.HOVER) {
            hoveredCell.setState(CellView.CLEAR);
        }
    }

    void setHoveredCellState(int state) {
        if (hoveredCell != null) {
            if (state == CellView.HIT) {
                new ExplosionAnimation(hoveredCell, this).start();
            }
            hoveredCell.setState(state);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        for (CellView[] row : viewCells) {
            for (CellView cell : row) {
                cell.paint(g);
            }
        }
        for (ShipView s : viewShips) {
            s.paint(g);
        }
    }
}
