package client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alexstoick on 10/13/14.
 */
public class RectangleDrawer extends JPanel {

	private int width;
	private int height ;

	RectangleDrawer(int width, int height) {
		this.width = width;
		this.height = height ;
		this.setPreferredSize (new Dimension (width, height));
	}

	@Override
	protected void paintComponent (Graphics g) {
		super.paintComponent (g);
		g.setColor(Color.white);
		g.fillRect(0,0, width, height);
	}
}
