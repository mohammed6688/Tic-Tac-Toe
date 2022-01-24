package model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PlayerHandler extends Thread {

    private GameDAO database;
    Player player;
    String query;
    StringTokenizer token;
    DataInputStream dis;
    PrintStream ps;
    static ArrayList<PlayerHandler> playersList = new ArrayList<>();

    public PlayerHandler(Socket socket) throws IOException, SQLException {
        dis = new DataInputStream(socket.getInputStream());    //internal socket ear
        ps = new PrintStream(socket.getOutputStream());      //internal socket mouth
        //playersList.add(this);                    //adding the data in array to use it latter
        database = new GameDAO();
        start();                                 //start the thread
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = dis.readLine();
                token = new StringTokenizer(message, " ");
                query = token.nextToken();
                switch (query) {
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
                    case "leaderboard":
                        leaderBoard();
                        break;
                    /*case "gameTic":
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
                        break;*/
                    case "logout":
                        logout();
                        break;
                    default:
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
            playersList.remove(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void leaderBoard() {

    }

    private void withdraw() {

    }

    private void refusedChallenge() {
        String OpponentMail = token.nextToken();
        for (PlayerHandler i : playersList) {
            if (i.player.email.equals(OpponentMail)) {
                i.ps.println("decline");
            }
        }
    }

    private void acceptChallenge() throws SQLException {
        String secondaryPlayerId = token.nextToken(); // opponent id
        String mainPlayerId = token.nextToken();

        database.createGameSession(mainPlayerId, secondaryPlayerId);
        for (PlayerHandler i : playersList) {

            if (i.player.email.equals(mainPlayerId)) {
                ps.println("gameOn");
            } else if (i.player.email.equals(secondaryPlayerId)) {
                ps.println("gameOn");
            }
        }
    }

    private void requestPlaying() {
        String secondaryPlayerMail = token.nextToken(); // opponent mail
        String mainPlayerData = token.nextToken(""); // "mail&username"
        for (PlayerHandler i : playersList) {
            if (i.player.email.equals(secondaryPlayerMail)) {
                System.out.println("sending request");
                i.ps.println("requestPlaying");
                i.ps.println(mainPlayerData);
            }
        }
    }

    private void logout() {

    }

    private void listOnlinePlayers() {
        String email = token.nextToken();     //playerEmail
        List<Player> onlinePlayers = database.getOnlinePlayers();
        System.out.println(onlinePlayers.size());
        for (Player player : onlinePlayers) {
            if (!player.email.equals(email)) {         //to not send the current player as one of the online players
                ps.println(onlinePlayers.size() + " " +
                        player.id + " " +
                        player.getUsername() + " " +
                        player.getEmail() + " " +
                        player.getPassword() + " " +       //TODO remove password from list
                        player.isStatus() + " " +
                        player.isInGame());
            }
        }
        ps.println(" " + "null");
    }

    private void signUp() {

    }

    private void signIn() throws SQLException {
        String email = token.nextToken();
        String password = token.nextToken();
        String checker = database.checkSignIn(email, password);

        switch (checker) {
            case "Logged in successfully":
                boolean updated = database.updateStatus(email, true);
                if (updated) {
                    Player player = database.getEmailData(email);
                    ps.println("Logged in successfully" + " " +
                            player.id + " " +
                            player.getUsername() + " " +
                            player.getEmail() + " " +
                            player.getPassword() + " " +
                            player.isStatus() + " " +
                            player.isInGame());
                    this.player = player;
                    playersList.add(this);
                } else {
                    ps.println("Cant update status");
                }
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
