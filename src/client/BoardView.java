package client;

import java.util.ArrayList;

/**
 * Created by user on 13.10.2014.
 */
public class BoardView {

	static final int BOARD_SIZE = 10 ;
	static final int NUMBER_OF_BOATS = 5 ;
	static final int[] BOATS_LENGTH = { 2, 3, 3, 4, 5} ;

	private GridView view ;

	public GridView getView () {
		return view;
	}
	public ShipView getSelectedShip () {
		return view.getSelectedShipView() ;
	}

	BoardView () {
        view = new GridView( this ) ;
    }
}
