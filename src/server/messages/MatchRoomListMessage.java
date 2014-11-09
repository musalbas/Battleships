package server.messages;

import java.io.Serializable;
import java.util.HashMap;

public class MatchRoomListMessage implements Serializable {

    private HashMap<String, String> matchRoomList;
    
    public MatchRoomListMessage(HashMap<String, String> matchRoomList) {
        this.matchRoomList = matchRoomList;
    }
    
    public HashMap<String, String> getMatchRoomList() {
        return this.matchRoomList;
    }
    
}
