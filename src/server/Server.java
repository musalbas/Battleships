package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public Server(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            MatchRoom matchRoom = new MatchRoom();

            while (true) {
                new Player(serverSocket.accept(), matchRoom).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8900;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        new Server(port);
    }

}
