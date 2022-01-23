package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    Thread listener;

    private void initializeServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5005); //server socket created
        listener = new Thread(() -> {
            while(true){             //this while for make server always listening for any request
                try {
                    Socket s = serverSocket.accept();   //server socket received request and give it to internal socket
                    new PlayerHandler(s);     //created object of chat handler and pass to it the internal socket
                }catch (IOException ex) {
                }

            }
        });
        listener.start();
    }
}
