package model;

import view.MatchRoomView;

public class MatchRoom extends Thread {
    
    private MatchRoomView matchRoomView;

    public MatchRoom(MatchRoomView matchRoomView) {
        this.matchRoomView = matchRoomView;
        
        
    }
    
    @Override
    public void run() {
        super.run();
    }
    
}
