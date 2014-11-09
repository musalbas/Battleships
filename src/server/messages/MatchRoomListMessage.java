package server.messages;

import java.io.Serializable;
import java.util.HashMap;

/**
 * An Object sent to the clients containing a list of all players in the
 * {@link server.MatchRoom}.
 */
public class MatchRoomListMessage implements Serializable {

    private HashMap<String, String> matchRoomList;

    /**
     * Constructs a MatchRoomListMessage, storing the given match room list in
     * a field.
     *
     * @param matchRoomList list of clients in {@link server.MatchRoom}
     */
    public MatchRoomListMessage(HashMap<String, String> matchRoomList) {
        this.matchRoomList = matchRoomList;
    }

    /**
     * Returns the match room list stored in the object.
     *
     * @return list of clients in {@link server.MatchRoom}
     */
    public HashMap<String, String> getMatchRoomList() {
        return this.matchRoomList;
    }
    
}
