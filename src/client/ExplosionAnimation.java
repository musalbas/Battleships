package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by user on 13.10.2014.
 */
public class ExplosionAnimation {

    private int x;
    private int y;
    private Graphics g;

    public ExplosionAnimation(int x, int y, Graphics g) {
        this.x = x;
        this.y = y;
        this.g = g;
    }

    public void start()  {
        final Timer t = new Timer(1, null);
        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO explosion
                t.stop();
            }
        });
        t.start();
    }
}
