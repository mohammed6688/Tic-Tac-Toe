package model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class PlayerHandler extends Thread {

    private GameDAO database;
    Game gameObj=null;
    Player player;
    String query;
    StringTokenizer token;
    DataInputStream dis;
    PrintStream ps;
    static ArrayList<PlayerHandler> playersList = new ArrayList<>();
    HashMap<String, PlayerHandler> game = new HashMap();   //key is player id : value is obj of PlayerHandler
    int[][] gameBoard = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

    public PlayerHandler(Socket socket) throws IOException, SQLException {
        dis = new DataInputStream(socket.getInputStream());    //internal socket ear
        ps = new PrintStream(socket.getOutputStream());      //internal socket mouth
        //playersList.add(this);                    //adding the data in array to use it latter
        database = new GameDAO();
        start();                                 //start the thread
    }
//    //for testing
//    public PlayerHandler() throws SQLException
//    {
//        database = new GameDAO();
//
//    }
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
                    case "gameTic":
                        forwardPress();
                        break;
                    case "finishgameTic":
                        finalForwardPress();
                        break;
                    /*case "updateScore":
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

    private void finalForwardPress() throws SQLException{         //when the final tic played
        String playerId = token.nextToken();  //first player id
        String cellNum = token.nextToken();
        String sign = token.nextToken();  //x or o

        PlayerHandler connection = game.get(playerId);
        connection.ps.println("gameTic");
        connection.ps.println(cellNum);

        int fPlayerSign=0,sPlayerSign=0;
        if (sign.equals("x")){
            fPlayerSign=1;
            sPlayerSign=2;
        }else {
            fPlayerSign=2;
            sPlayerSign=1;
        }

        int sPlayerId = -1;  //second player id
        for (Map.Entry<String, PlayerHandler> set : game.entrySet()){
            if (!set.getKey().equals(playerId)){
                sPlayerId=Integer.parseInt(set.getKey());
            }
        }
        createPlayerSessions(Integer.parseInt(playerId), sPlayerId ,gameObj.gameId,fPlayerSign,sPlayerSign);
    }

    private void forwardPress() {                    //when a tic played
        String playerId = token.nextToken();
        String cellNum = token.nextToken();
        String sign = token.nextToken();    //x or o

        PlayerHandler connection = game.get(playerId);
        connection.ps.println("gameTic");
        connection.ps.println(cellNum);
        saveCell(cellNum, sign);
    }

    private void saveCell(String cellNum, String sign) {
        int flag;
        if (sign.equals("x")) {
            flag = 1;
        } else {
            flag = 2;
        }
        switch (cellNum) {
            case "c00":
                gameBoard[0][0] = flag;
                break;
            case "c01":
                gameBoard[0][1] = flag;
                break;
            case "c02":
                gameBoard[0][2] = flag;
                break;
            case "c10":
                gameBoard[1][0] = flag;
                break;
            case "c11":
                gameBoard[1][1] = flag;
                break;
            case "c12":
                gameBoard[1][2] = flag;
                break;
            case "c20":
                gameBoard[2][0] = flag;
                break;
            case "c21":
                gameBoard[2][1] = flag;
                break;
            case "c22":
                gameBoard[2][2] = flag;
                break;
            default:
                break;
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

        this.gameObj= database.createGameSession(mainPlayerId, secondaryPlayerId);
        PlayerHandler p1 = null, p2 = null;
        for (PlayerHandler i : playersList) {
            if (i.player.email.equals(mainPlayerId)) {
                ps.println("gameOn");
                p1 = i;
            } else if (i.player.email.equals(secondaryPlayerId)) {
                ps.println("gameOn");
                p2 = i;
            }
        }
        game.put(mainPlayerId, p2);     //player 1 has obj from player 2
        game.put(secondaryPlayerId, p1);   //player 2 has obj from player 1
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
        int PlayerId=Integer.parseInt(token.nextToken());
        database.logOut(PlayerId);

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

    private String signUp() throws SQLException{
    String UserName=token.nextToken();
    String Email=token.nextToken();
    String Password=token.nextToken();
        String checker = database.checkSignUp(UserName,Password,Email);
       return checker;

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

    public void createPlayerSessions(int FirstPlayerId, int secondPlayerId, int GameId, int firstPlayerSign, int secondPlayerSign) throws SQLException {

        PlayerSession firstPlayerSession = new PlayerSession(FirstPlayerId, GameId, firstPlayerSign);
        PlayerSession secondPlayerSession = new PlayerSession(secondPlayerId, GameId, secondPlayerSign);


        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                if(gameBoard[i][j]!=0){
                    if (gameBoard[i][j] == 1) {
                        if (firstPlayerSign == 1) {
                            firstPlayerSession.setCell(i, j, true);
                            secondPlayerSession.setCell(i, j, false);
                        } else {
                            firstPlayerSession.setCell(i, j, false);
                            secondPlayerSession.setCell(i, j, true);
                        }
                    } else if (gameBoard[i][j] == 2) {
                        if (firstPlayerSign == 1) {
                            firstPlayerSession.setCell(i, j, false);
                            secondPlayerSession.setCell(i, j, true);
                        } else {
                            firstPlayerSession.setCell(i, j, true);
                            secondPlayerSession.setCell(i, j, false);
                        }
                    }
                }
            }
        }


        database.updatingGame(firstPlayerSession,secondPlayerSession);


    }

}
