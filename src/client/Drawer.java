package client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by alexstoick on 10/13/14.
 */
public class Drawer extends JPanel {

	private static final int BORDER_SIZE = 0 ;


	private int startX  ;
	private int startY  ;
	private int rows  ;
	private int columns  ;
	private int boxWidth  ;
	private int boxHeight ;

	public Drawer( int startX, int startY, int rows, int columns, int boxWidth, int boxHeight ) {
		this.rows = rows ;
		this.startX = startX ;
		this.columns = columns ;
		this.boxWidth = boxWidth ;
		this.boxHeight = boxHeight ;
		this.startY = startY ;
		this.setPreferredSize ( new Dimension ( rows * boxWidth + BORDER_SIZE * (rows-1) ,
												columns * boxHeight + BORDER_SIZE * (columns-1)
											  )
							  );
	}

	@Override
	protected void paintComponent (Graphics g) {
		for ( int i = 0 ; i < rows ; ++ i ) {
			for ( int j = 0 ; j < columns ; ++ j ) {
				g.drawRect ( startX + boxHeight * i + BORDER_SIZE * i ,
						startY + boxWidth * j + BORDER_SIZE * j ,
						boxWidth , boxHeight );
			}
		}
	}

}
