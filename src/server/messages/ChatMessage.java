package server.messages;

import java.io.Serializable;

/**
 * Created by joe on 27/10/14.
 */
public class ChatMessage extends Message implements Serializable {

    private String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
