package test.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8900);

            ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            final ObjectInputStream in = new ObjectInputStream(
                    socket.getInputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object input;
                        while ((input = in.readObject()) != null) {
                            System.out.println(input);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            out.writeObject(new String[]{"name", args[0]});
            out.flush();
            if (args.length == 2) {
                out.writeObject(new String[]{"join", args[1]});
                out.flush();
            } else if (args.length == 3) {
                out.writeObject(new String[]{"join", args[1], args[2]});
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
