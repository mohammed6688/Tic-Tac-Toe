package model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class PlayerHandler extends Thread {

    static private GameDAO database;
    Game gameObj = null;
    Player player;
    String query;
    StringTokenizer token;
    DataInputStream dis;
    PrintStream ps;
    static ArrayList<PlayerHandler> playersList = new ArrayList<>();
    static HashMap<String, PlayerHandler> game = new HashMap();   //key is player id : value is obj of PlayerHandler
    int[][] gameBoard = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    boolean unFinished = false;
    ArrayList<PlayerSession> unfinishedList;

    public PlayerHandler(Socket socket) throws IOException, SQLException {
        dis = new DataInputStream(socket.getInputStream());    //internal socket ear
        ps = new PrintStream(socket.getOutputStream());      //internal socket mouth
        //playersList.add(this);                    //adding the data in array to use it latter
        if (database == null) {
            database = new GameDAO();
        }
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
                    case "gameTic":
                        forwardPress();
                        break;
                    case "finishgameTic":
                        finalForwardPress();
                        break;
                    case "updateScore":
                        updateScore();
                        break;
                    /*case "checkUnFinished":
                        checkUnfinishedGame();
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
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                dis.close();
                ps.close();
                playersList.remove(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateScore() {
        String playerId = token.nextToken();

        System.out.println("score player id " +playerId);
        for (PlayerHandler player : playersList) {
            if (player.player.id==Integer.parseInt(playerId)) {
                database.updateScore(player.player.getId(), player.player.getScore() + 10);
            }
        }
    }

    private void finalForwardPress() throws SQLException {         //when the final tic played
        String flagTie = token.nextToken();
        String playerId = token.nextToken();  //first player id
        String cellNum = token.nextToken();
        String buttonId = token.nextToken();
        String sign = token.nextToken();  //x or o

        System.out.println("final game tic received playerid "+playerId);

        PlayerHandler connection = game.get(playerId);
        connection.ps.println("gameTic");
        connection.ps.println(buttonId);

        System.out.println("final game tic other playerid "+connection.player.getId());

        saveCell(cellNum, sign);
        connection.saveCell(cellNum, sign);
        //saveCell(connection, cellNum, sign);

        int fPlayerSign = 0, sPlayerSign = 0;
        if (sign.equals("X")) {
            fPlayerSign = 1;
            sPlayerSign = 2;
        } else {
            fPlayerSign = 2;
            sPlayerSign = 1;
        }

        int sPlayerId = connection.player.getId();  //second player id

        System.out.println("fUserId " + playerId);
        System.out.println("sUserId " + sPlayerId);


        //updatePlayerSessions(Integer.parseInt(playerId), sPlayerId, connection.gameObj.gameId, fPlayerSign, sPlayerSign);

        if (flagTie.equals("true")) {
            if (connection.gameObj == null) {
                updatePlayerSessions(Integer.parseInt(playerId), sPlayerId, gameObj.gameId, fPlayerSign, sPlayerSign);
                database.updateGameSession(gameObj.gameId, Integer.parseInt(playerId), true, true, sPlayerId);    //game is finished
            } else {

                updatePlayerSessions(Integer.parseInt(playerId), sPlayerId, connection.gameObj.gameId, fPlayerSign, sPlayerSign);
                database.updateGameSession(connection.gameObj.gameId, Integer.parseInt(playerId), true, true, sPlayerId);    //game is finished
            }
        } else {
            if (connection.gameObj == null) {

                updatePlayerSessions(Integer.parseInt(playerId), sPlayerId, gameObj.gameId, fPlayerSign, sPlayerSign);
                database.updateGameSession(gameObj.gameId, Integer.parseInt(playerId), true, false, sPlayerId);    //game is finished

            } else {

                updatePlayerSessions(Integer.parseInt(playerId), sPlayerId, connection.gameObj.gameId, fPlayerSign, sPlayerSign);
                database.updateGameSession(connection.gameObj.gameId, Integer.parseInt(playerId), true, false, sPlayerId);    //game is finished

            }
        }
        resetBox();
        connection.resetBox();
        game.remove(Integer.toString(this.player.id));
        game.remove(Integer.toString(sPlayerId));
        this.gameObj = null;
        connection.gameObj = null;

    }

    public void resetBox() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = 0;
            }
        }
    }

    private void forwardPress() {                    //when a tic played
        String playerId = token.nextToken();        //the opponent player
        String cellNum = token.nextToken();
        String buttonId = token.nextToken();
        String sign = token.nextToken();    //x or o

        System.out.println(playerId);
        PlayerHandler connection = game.get(playerId);
        System.out.println("the target player " + connection.player.getUsername());
        connection.ps.println("gameTic");
        connection.ps.println(buttonId);
        saveCell(cellNum, sign);
        connection.saveCell(cellNum, sign);
    }

    private void saveCell(String cellNum, String sign) {
        int flag;
        if (sign.equals("X")) {
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
        List<Player> leaderBoard = database.getLeaderBoard();
        System.out.println(leaderBoard.size());
        for (Player player : leaderBoard) {
            ps.println(leaderBoard.size() + " " +
                    player.id + " " +
                    player.getUsername() + " " +
                    player.getEmail() + " " +
                    player.getPassword() + " " +       //TODO remove password from list
                    player.isStatus() + " " +
                    player.isInGame());

        }
        ps.println("null");
    }

    private void withdraw() throws SQLException {
        String playerId = token.nextToken();  //first player id
        String sign = token.nextToken();  //first player id

        PlayerHandler connection = game.get(playerId);
        connection.ps.println("withdraw");
        //connection.ps.println(playerId);

        System.out.println("received player id "+playerId);
        System.out.println("other player id "+connection.player.getId());

        int fPlayerSign = 0, sPlayerSign = 0;
        if (sign.equals("X")) {
            fPlayerSign = 1;
            sPlayerSign = 2;
        } else {
            fPlayerSign = 2;
            sPlayerSign = 1;
        }


        int sPlayerId = -1;  //second player id
        if (connection.gameObj == null) {
            updatePlayerSessions(Integer.parseInt(playerId), connection.player.id, gameObj.gameId, fPlayerSign, sPlayerSign);

        } else {
            updatePlayerSessions(Integer.parseInt(playerId), connection.player.id, connection.gameObj.gameId, fPlayerSign, sPlayerSign);

        }
        database.updateInGame(Integer.parseInt(playerId), connection.player.id, false);
        resetBox();
        connection.resetBox();
        game.remove(Integer.toString(this.player.id));
        game.remove(Integer.toString(sPlayerId));
        this.gameObj = null;
        connection.gameObj = null;
    }

    private void refusedChallenge() {
        String OpponentMail = token.nextToken(); //refused request email
        for (PlayerHandler i : playersList) {
            if (i.player.email.equals(OpponentMail)) {
                i.ps.println("decline");
            }
        }
    }

    private void acceptChallenge() throws SQLException {
        String secondaryPlayerMail = token.nextToken(); //the accepter, opponent mail
        String mainPlayerMail = token.nextToken();   //the request sender mail

        int secondaryPlayerId = -1, mainPlayerId = -1;
        PlayerHandler p1 = null, p2 = null;

        for (PlayerHandler player : playersList) {
            if (player.player.email.equals(secondaryPlayerMail)) {
                secondaryPlayerId = player.player.id;
                player.ps.println("gameOn");

                p2 = player;
            }
            if (player.player.email.equals(mainPlayerMail)) {
                mainPlayerId = player.player.id;
                //player.ps.println("gameOn");
                p1 = player;
            }
        }
        p2.ps.println(p1.player.getUsername());
        p2.ps.println(p1.player.getEmail());
        p2.ps.println(p1.player.getId());
        p2.ps.println(p1.player.getScore());

        System.out.println("the flag is: "+unFinished);
        if (unFinished) {
            p2.ps.println("true");
            sendPlayerSessionData(p2, unfinishedList);
            setCells(unfinishedList, p2);

        } else {
            p2.ps.println("false");
            this.gameObj = database.createGameSession(mainPlayerId, secondaryPlayerId);
        }

        game.put(String.valueOf(mainPlayerId), p2);     //player 1 has obj from player 2
        game.put(String.valueOf(secondaryPlayerId), p1);   //player 2 has obj from player 1

        //System.out.println("game size "+game.size());

    }

    private void requestPlaying() throws SQLException {
        String secondaryPlayerMail = token.nextToken(); // opponent mail
        String mainPlayerData = token.nextToken(); // mail
        for (PlayerHandler i : playersList) {
            if (i.player.email.equals(secondaryPlayerMail)) {
                System.out.println("sending request");
                System.out.println(i.player.email);
                System.out.println("opponent mail" + mainPlayerData);

                Player oppPlayer = null;
                for (PlayerHandler player : playersList) {
                    if (player.player.getEmail().equals(mainPlayerData)) {
                        oppPlayer = player.player;

                    }
                }

                i.ps.println("requestPlaying");
                i.ps.println(oppPlayer.getEmail());
                i.ps.println(oppPlayer.getUsername());
                i.ps.println(oppPlayer.getId());
                i.ps.println(oppPlayer.getScore());

                checkUnfinishedGame(i, oppPlayer.getId(), i.player.getId());
            }
        }
    }

    private void logout() {
        int PlayerId = Integer.parseInt(token.nextToken());
        database.logOut(PlayerId);
        playersList.remove(this);
    }

    private void listOnlinePlayers() {
        Thread thread = new Thread(() -> {
            while (true) {
                //String email = token.nextToken();     //playerEmail
                List<Player> onlinePlayers = database.getOnlinePlayers();
                //System.out.println(onlinePlayers.size());
                for (Player player : onlinePlayers) {
                    //if (!player.email.equals(email)) {         //to not send the current player as one of the online players
                    ps.println(player.id + " " +
                            player.getUsername() + " " +
                            player.getEmail() + " " +
                            player.getPassword() + " " +       //TODO remove password from list
                            player.isStatus() + " " +
                            player.isInGame());
                    //}
                }
                ps.println("null");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            }
        });
        thread.start();

    }

    private void signUp() throws SQLException {
        String UserName = token.nextToken();
        String Email = token.nextToken();
        String Password = token.nextToken();
        String checker = database.checkSignUp(UserName, Password, Email);

        switch (checker) {
            case "SignedUp Successfully":
                Player player = database.getEmailData(Email);
                ps.println("SignedUpSuccessfully" + " " +
                        player.id + " " +
                        player.getUsername() + " " +
                        player.getEmail() + " " +
                        player.getPassword() + " " +
                        player.isStatus() + " " +
                        player.isInGame());
                this.player = player;
                playersList.add(this);
                break;
            case "username or email already exists":
                ps.println("usernameOrEmailAlreadyExists");
                break;
        }
    }

    private void signIn() throws SQLException {
        String email = token.nextToken();
        String password = token.nextToken();
        String checker = database.checkSignIn(email, password);
        System.out.println(checker);
        switch (checker) {

            case "Logged in successfully":
                boolean updated = database.updateStatus(email, true);
                if (updated) {
                    Player player = database.getEmailData(email);
                    ps.println("LoggedInSuccessfully" + " " +
                            player.id + " " +
                            player.getUsername() + " " +
                            player.getEmail() + " " +
                            player.getPassword() + " " +
                            player.isStatus() + " " +
                            player.isInGame());
                    this.player = player;
                    playersList.add(this);
                } else {
                    ps.println("CantUpdateStatus");
                }
                break;
            case "Email or Password is not correct":
                ps.println("EmailOrPasswordIsIncorrect");
                break;
            case "Connection issue, please try again later":
                ps.println("ConnectionIssue,PleaseTryAgainLater");
                break;
            case "you have already signed in from another device":
                ps.println("already signed in");
                break;
        }

    }

    public void updatePlayerSessions(int FirstPlayerId, int secondPlayerId, int GameId, int firstPlayerSign, int secondPlayerSign) throws SQLException {

        PlayerSession firstPlayerSession = new PlayerSession(FirstPlayerId, GameId, firstPlayerSign);
        PlayerSession secondPlayerSession = new PlayerSession(secondPlayerId, GameId, secondPlayerSign);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] != 0) {
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
        database.updatingGame(firstPlayerSession, secondPlayerSession);
    }

    public void checkUnfinishedGame(PlayerHandler i, int fPlayerId, int sPlayerId) throws SQLException {
//        String fPlayerId=token.nextToken();
//        String sPlayerId=token.nextToken();

        Game game = database.getUnFinishedGamesForACertainOpponent(fPlayerId, sPlayerId);
        if (game != null) {
            System.out.println("there is unfinished game");
            System.out.println("gameId: " + game.gameId);

            this.unFinished = true;
            i.unFinished = true;

            this.gameObj=game;

            unfinishedList = game.obj;
            i.unfinishedList = game.obj;

            i.ps.println("true");

            //ps.println("unFinishedList");
            //ps.println("hasUnfinishedList");
            sendPlayerSessionData(i, unfinishedList);
            //setCells(unfinishedList);

        } else {
            this.unFinished = false;
            i.unFinished = false;
            i.ps.println("false");
            //ps.println("unFinishedList");
            //ps.println("noUnfinished");
            System.out.println("no unfinished game");
        }
    }

    private void sendPlayerSessionData(PlayerHandler i, ArrayList<PlayerSession> playerSessions) {

        for (PlayerSession playerSession : playerSessions) {
            String data = playerSession.playerId + " "
                    + playerSession.GameId + " "
                    + playerSession.sign + " "
                    + playerSession.gameDate + " "
                    + playerSession.c00 + " "
                    + playerSession.c01 + " "
                    + playerSession.c02 + " "
                    + playerSession.c10 + " "
                    + playerSession.c11 + " "
                    + playerSession.c12 + " "
                    + playerSession.c20 + " "
                    + playerSession.c21 + " "
                    + playerSession.c22;
            i.ps.println(data + " ");
            System.out.println("player id: " + playerSession.playerId + " player tick " + playerSession.sign);
        }
    }

    private void setCells(ArrayList<PlayerSession> playerSessions, PlayerHandler p2) {
        for (PlayerSession playerSession : playerSessions) {

            if (playerSession.isC00()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c00", "X");
                    p2.saveCell("c00", "X");

                } else {
                    this.saveCell("c00", "O");
                    p2.saveCell("c00", "O");

                }
            }
            if (playerSession.isC01()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c01", "X");
                    p2.saveCell("c01", "X");

                } else {
                    this.saveCell("c01", "O");
                    p2.saveCell("c01", "O");

                }
            }
            if (playerSession.isC02()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c02", "X");
                    p2.saveCell("c02", "X");

                } else {
                    this.saveCell("c02", "O");
                    p2.saveCell("c02", "O");

                }
            }

            if (playerSession.isC10()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c10", "X");
                    p2.saveCell("c10", "X");

                } else {
                    this.saveCell("c10", "O");
                    p2.saveCell("c10", "O");
                }
            }
            if (playerSession.isC11()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c11", "X");
                    p2.saveCell("c11", "X");
                } else {
                    this.saveCell("c11", "O");
                    p2.saveCell("c11", "O");
                }
            }
            if (playerSession.isC12()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c12", "X");
                    p2.saveCell("c12", "X");
                } else {
                    this.saveCell("c12", "O");
                    p2.saveCell("c12", "O");
                }
            }

            if (playerSession.isC20()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c20", "X");
                    p2.saveCell("c20", "X");
                } else {
                    this.saveCell("c20", "O");
                    p2.saveCell("c20", "O");
                }
            }
            if (playerSession.isC21()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c21", "X");
                    p2.saveCell("c21", "X");
                } else {
                    this.saveCell("c21", "O");
                    p2.saveCell("c21", "O");
                }
            }
            if (playerSession.isC22()) {
                if (playerSession.getSign() == 1) {
                    this.saveCell("c22", "X");
                    p2.saveCell("c22", "X");
                } else {
                    this.saveCell("c22", "O");
                    p2.saveCell("c22", "O");
                }
            }


//            for (int i =0;i<9;i++){
//                 playerSession.getCell(0,0);
//            }

        }
    }

}