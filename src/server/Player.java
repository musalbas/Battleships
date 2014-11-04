package server;

import model.Board;
import server.messages.ChatMessage;
import server.messages.MoveMessage;
import server.messages.NotificationMessage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player extends Thread {

	private Socket socket;
	private MatchRoom matchRoom;
	private String name = "(new player)";
	private ObjectOutputStream out;
	private Game game;
	private Board board;

	public Player (Socket socket, MatchRoom matchRoom) {
		this.socket = socket;
		this.matchRoom = matchRoom;
	}

	@Override
	public void run () {
		super.run ();
		try {
			out = new ObjectOutputStream (new BufferedOutputStream (
					socket.getOutputStream ()));
			out.flush ();
			ObjectInputStream in = new ObjectInputStream (
					socket.getInputStream ());

			Object input;

			while ((input = in.readObject ()) != null) {
				if ( input instanceof String[] ) {
					String[] array = (String[]) input;
					int length = array.length;

					if ( length > 0 ) {
						String message = array[ 0 ];

						switch (message) {
							case "join":
								if ( game == null ) {
									matchRoom.join (this, array);
								}
								break;
							case "name":
								if ( length != 2 || array[ 1 ].equals("")) {
									writeNotification(NotificationMessage.INVALID_NAME);
								} else if ( matchRoom.playerNameExists(array[ 1 ])) {
									writeNotification(NotificationMessage.NAME_TAKEN);
								} else {
									name = array[ 1 ];
									writeNotification(NotificationMessage.NAME_ACCEPTED);
									matchRoom.sendMatchRoomList();
								}
								break;
						}
					}
				} else if ( input instanceof Board ) {
					Board board = (Board) input;
					if ( Board.isValid (board) && game != null ) {
                        writeNotification (NotificationMessage.BOARD_ACCEPTED);
						this.board = board;
						game.checkBoards ();
					} else if ( game == null ) {
						writeNotification (NotificationMessage.NOT_IN_GAME);
					} else {
						writeNotification (NotificationMessage.INVALID_BOARD);
					}
				} else if ( input instanceof MoveMessage ) {
					if ( game != null ) {
						game.applyMove ((MoveMessage) input, this);
					}
				} else if ( input instanceof ChatMessage ) {
					if ( game != null ) {
						Player opponent = game.getOpponent(this);
						if ( opponent != null ) {
							opponent.writeObject(input);
						}
					}
				}
			}
		} catch (IOException e) {
			if ( game != null ) {
				game.killGame ();
				// TODO: Alert other player they win
			} else {
				matchRoom.removeWaitingPlayer (this);
			}
			e.printStackTrace ();
		} catch (ClassNotFoundException e) {
			e.printStackTrace ();
		}
	}

	public void setGame (Game game) {
		this.game = game;
	}

	public String getPlayerName () {
		return name;
	}

	/**
	 * Writes a String to the view.
	 *
	 * @param message
	 */
	public void writeMessage (String message) {
		try {
			out.writeObject (message);
			out.flush ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	public void writeObject (Object object) {
		try {
			out.writeObject (object);
			out.flush ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	public void writeNotification (int notificationMessage, String... text) {
		try {
			NotificationMessage nm = new
					NotificationMessage (notificationMessage, text);
			out.writeObject (nm);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}

	public Board getBoard () {
		return this.board;
	}

}
