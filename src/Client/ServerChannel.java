package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServerChannel {
   private static Socket socket;
    private static DataInputStream dis;
    private  static PrintStream ps;
    public static boolean startChannel() {

        try {
            socket=new Socket("127.0.0.1",5005);
            dis=new DataInputStream(socket.getInputStream());
            ps=new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            return false;
        }

        return true;
    }
    public static String signIn(String message) {
    String response=null;
        try {
           ps.println(message);
           response=dis.readLine();
        } catch (IOException e) {

        }
        return response;
    }
    public static String signUP(String message) {
        String response=null;
        try {
            ps.println(message);
            response=dis.readLine();
        } catch (IOException e) {

        }
        return response;
    }
    public static String getUnFinishedGames(String message) {
        String response=null;
        try {
            ps.println(message);
            response=dis.readLine();
        } catch (IOException e) {

        }
        return response;
    }
    public static boolean logOut(String message) {
            ps.println(message);
            closeConnection();
        return true;
    }
    public static void closeConnection() {
        try {
            dis.close();
            ps.close();
            socket.close();
        } catch (IOException e) {
        }

    }


}
