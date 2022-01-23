package model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PlayerHandler extends Thread{

    private GameDAO database;
    String query,email,password,username,status,inGame;
    StringTokenizer token;
    DataInputStream dis;
    PrintStream ps;
    int index;
    static ArrayList<PlayerHandler> arrayList=new ArrayList<>();

    public PlayerHandler (Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());    //internal socket ear
        ps = new PrintStream(socket.getOutputStream());      //internal socket mouth
        arrayList.add(this);                    //adding the data in array to use it latter
        index=arrayList.size()-1;
        start();                                 //start the thread
    }

    @Override
    public void run() {
        try {
            while (true){
                String message =dis.readLine();
                token = new StringTokenizer(message," ");
                query = token.nextToken();
                switch(query){
                    case "SignIn":
                        signIn();
                        break;
                    case "SignUp":
                        signUp();
                        break;
                    case "playerlist":
                        listOnlinePlayers();
                        break;
                    case "request":
                        requestPlaying();
                        break;
                    case "accept":
                        acceptChallenge();
                        break;
                    case "decline":
                        refusedChallenge();
                        break;
                    case "withdraw":
                        withdraw();
                        break;
                    case "gameTic":
                        forwardPress();
                        break;
                    case "finishgameTic":
                        fforwardPress();
                        break;
                    case "updateScore":
                        updateScore();
                        break;
                    case "available":
                        reset();
                        break;
                    case "logout":
                        logout();
                        break;
                    default :
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                dis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            ps.close();
            arrayList.remove(index);
        }
    }

    private void withdraw() {

    }

    private void refusedChallenge() {

    }

    private void acceptChallenge() {

    }

    private void requestPlaying() {

    }

    private void logout() {

    }

    private void listOnlinePlayers() {
        List<Player> onlinePlayers= database.getOnlinePlayers();
        for (Player player :  onlinePlayers){
            ps.println(onlinePlayers.size()+" "+
                    player.id+" "+
                    player.getUsername()+" "+
                    player.getEmail()+" "+
                    player.getPassword()+" "+
                    player.isStatus()+" "+
                    player.isInGame());
        }
        ps.println(" "+"null");
    }

    private void signUp() {

    }

    private void signIn() {
        email=token.nextToken();
        password=token.nextToken();
        String checker= database.checkSignIn(email,password);

        switch (checker) {
            case "Logged in successfully":
                Player player = database.getEmailData(email);
                ps.println("Logged in successfully" + " " +
                        player.id + " " +
                        player.getUsername() + " " +
                        player.getEmail() + " " +
                        player.getPassword() + " " +
                        player.isStatus() + " " +
                        player.isInGame());

                break;
            case "Password is incorrect":
                ps.println("Password is incorrect");
                break;
            case "Connection issue, please try again later":
                ps.println("Connection issue, please try again later");
                break;
        }



    }
}
