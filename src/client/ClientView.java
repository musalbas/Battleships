package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by alexstoick on 10/15/14.
 */
public class ClientView extends JFrame {

    ClientView() {

        JPanel rootPanel = new JPanel(new BorderLayout());


        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        final BoardView myBoard = new BoardView();
        boardsPanel.add(myBoard);

        JPanel controlPanel = new JPanel(new GridLayout(1, 1));
        JButton rotateButton = new JButton("Rotate");
        controlPanel.add(rotateButton);
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShipView shipView = myBoard.getSelectedShip();
                if (shipView != null) {
                    shipView.rotate();
                    repaint();
                }
            }
        });

        setContentPane(rootPanel);
        rootPanel.add(boardsPanel, BorderLayout.CENTER);
        rootPanel.add(controlPanel, BorderLayout.EAST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }

    public static void main(String[] args) {
        new ClientView();
    }

}
