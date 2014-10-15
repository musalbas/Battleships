import javax.swing.*;
import java.awt.*;

/**
 * Created by user on 13.10.2014.
 */
public class Board extends JPanel {

    public Board() {
        setLayout(new GridLayout(1, 2, 10, 10));
        add(new GridView());
        add(new GridView());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Board());
        frame.pack();
        frame.setVisible(true);
    }
}
