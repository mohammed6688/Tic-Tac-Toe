package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "javaGame";
    public static final String USER = "postgres";
    public static final String PASS = "mina2508";

    private Connection con;
    private static GameDAO instanceData;
    public GameDAO() throws SQLException {
        connect();
    }

    public GameDAO getDatabase() throws SQLException {
        if (instanceData == null) {
            instanceData = new GameDAO();
        }
        return instanceData;
    }

    private void connect() throws SQLException {
        con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        // stmt=
        // con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY)
        // ;
    }

    public synchronized List<Player> getOnlinePlayers() {
        try {
            String queryString = new String("select * from player where isonline = true");
            PreparedStatement stmt = con.prepareStatement("select * from player where isonline = true",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            List<Player> onlinePlayers = new ArrayList<>();
            while (rs.next()) {
                onlinePlayers.add(new Player(rs.getInt("playerid"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("userpassword"),
                        rs.getBoolean("isonline"),
                        rs.getBoolean("isingame")));
            }
            return onlinePlayers;
        } catch (SQLException ex) {
            System.out.println("error in online players");
        }

        return null;
    }

    public List<Player> getLeaderBoard() {
        try {
            String queryString = new String("select id , username ,score from player ORDER BY score DESC ");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            List<Player> leaderBoard = new ArrayList<>();
            while (rs.next()) {
                leaderBoard.add(new Player(rs.getInt("playerid"),
                        rs.getString("username"),
                        rs.getString("score"),
                        rs.getString("userpassword"),
                        rs.getBoolean("isonline"),
                        rs.getBoolean("isingame")));
            }
            return leaderBoard;
        } catch (SQLException ex) {
            System.out.println("error in online leaderboard");
        }
        return null;
    }

    public void updateScore(int player_id, int score, List<Player> players) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE player SET score=? WHERE id=?");
            preparedStatement.setInt(1, score);
            preparedStatement.setInt(2, player_id);
            int isUpdated = preparedStatement.executeUpdate();
            if (isUpdated > 0) {
                Player p = players.get(player_id);
                p.setScore(score);
                for (int i = 0; i < players.size(); i++) {
                    Player playerFound = players.get(i);
                    if (playerFound.getId() == player_id) {
                        players.set(i, p);
                    }
                }

            }
        } catch (SQLException ex) {
            System.out.println("error in online leaderboard");
        }
    }

    public void updatingGame(PlayerSession firstPlayerSession,PlayerSession secondPlayerSession) throws SQLException {
        String query=new String("update playersession set cell00=? , cell01=? ,cell02=?" +
                " , cell10=? , cell11=? , cell12=? , cell20=? , cell21=? ,cell22=? where" +
                " playerId=? and gameId=?");

        PreparedStatement pst=con.prepareStatement(query);
        pst.setBoolean(1,firstPlayerSession.c00);
        pst.setBoolean(2,firstPlayerSession.c01);
        pst.setBoolean(3,firstPlayerSession.c02);
        pst.setBoolean(4,firstPlayerSession.c10);
        pst.setBoolean(5,firstPlayerSession.c11);
        pst.setBoolean(6,firstPlayerSession.c12);
        pst.setBoolean(7,firstPlayerSession.c20);
        pst.setBoolean(8,firstPlayerSession.c21);
        pst.setBoolean(9,firstPlayerSession.c22);
        pst.setInt(10,firstPlayerSession.playerId);
        pst.setInt(11,firstPlayerSession.GameId);
        pst.addBatch();
        pst.setBoolean(1,secondPlayerSession.c00);
        pst.setBoolean(2,secondPlayerSession.c01);
        pst.setBoolean(3,secondPlayerSession.c02);
        pst.setBoolean(4,secondPlayerSession.c10);
        pst.setBoolean(5,secondPlayerSession.c11);
        pst.setBoolean(6,secondPlayerSession.c12);
        pst.setBoolean(7,secondPlayerSession.c20);
        pst.setBoolean(8,secondPlayerSession.c21);
        pst.setBoolean(9,secondPlayerSession.c22);
        pst.setInt(10,secondPlayerSession.playerId);
        pst.setInt(11,secondPlayerSession.GameId);
        pst.addBatch();
        pst.executeBatch();

        System.out.println("done");
    }

    public String checkSignUp(String userName, String password,String email) throws SQLException {
          try {
              PreparedStatement stmt = con.prepareStatement("insert into player" +
                      " (userName,userPassword,email,isonline,isingame,score)" +
                      " values(?,?,?,true,false,0)");
              stmt.setString(1, userName);
              stmt.setString(2, password);
              stmt.setString(3, email);
              int rs = stmt.executeUpdate();

        } catch (SQLException ex) {
              return "username or email already exists";
        }
        return "SignedUp Successfully";

    }

    public String checkSignIn(String email, String password) {
        try {
            PreparedStatement stmt = con.prepareStatement("select * from player where email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            Player player = null;
            while (rs.next()) {
                player = new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("userpassword"),
                        rs.getBoolean("isonline"),
                        rs.getBoolean("isingame"));
            }
            System.out.println(email + " " + player.password);

            if (player != null && password.equals(player.password)) {
                return "Logged in successfully";
            } else {
                return "Email or Password is not correct";
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            return "Connection issue, please try again later";
        }
    }

    public void logOut(int PlayerId) {
        String query=new String("Update player set isonline=false ,isingame=false where PlayerId=?");
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, PlayerId);
            pst.executeUpdate();
        }
        catch (SQLException ex){
            System.out.println(ex);
        }
    }

    public Player getEmailData(String email) {
        try {
            PreparedStatement stmt = con.prepareStatement("select * from player where email =?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            Player player = null;
            while (rs.next()) {
                player = new Player(rs.getInt("playerid"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("userpassword"),
                        rs.getBoolean("isonline"),
                        rs.getBoolean("isingame"));
            }

            if (player != null) {
                return player;
            }
        } catch (SQLException ex) {
            System.out.println("error in getEmailData");
        }
        return null;
    }

    public Game createGameSession(String MainPlayer, String SecondaryPlayer) throws SQLException {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            PreparedStatement stmt = con.prepareStatement("insert into game (gamedate,gamestatus) values(?,?) ",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, String.valueOf(now));
            stmt.setBoolean(2, false);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            Game game = null;

            while (rs.next()) {
                game = new Game(rs.getInt("gameid"),
                        rs.getString("gamedate"),
                        rs.getInt("gamewinnerid"),
                        rs.getBoolean("gamestatus"),
                        null);
            }
            createPlayerSession(MainPlayer, SecondaryPlayer, game);
            return game;

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    private int createPlayerSession(String mainPlayer, String secondaryPlayer, Game game) {
        try {

            PreparedStatement stmt = con.prepareStatement("insert into playersession (playerId,GameId) values(?,?) ");
            stmt.setString(1, mainPlayer);
            stmt.setInt(2, game.gameId);
            stmt.executeUpdate();

            PreparedStatement stmt2 = con.prepareStatement("insert into playersession (playerId,GameId) values(?,?) ");
            stmt2.setString(1, secondaryPlayer);
            stmt2.setInt(2, game.gameId);
            stmt2.executeUpdate();

            updateInGame(mainPlayer, secondaryPlayer, true);
            return 1;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return -1;
    }

    private void updateInGame(String mainPlayer, String secondaryPlayer, boolean state) {
        try {

            PreparedStatement stmt = con.prepareStatement("update player set isingame = ? where id = ? ");
            stmt.setBoolean(1, state);
            stmt.setString(2, mainPlayer);
            stmt.executeUpdate();

            PreparedStatement stmt2 = con.prepareStatement("update player set isingame = ? where id = ? ");
            stmt2.setBoolean(1, state);
            stmt2.setString(2, secondaryPlayer);
            stmt2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public boolean updateStatus(String email, boolean status) throws SQLException {
        try {
            PreparedStatement stmt = con.prepareStatement("update player set isonline= ? where email =?");
            stmt.setBoolean(1, status);
            stmt.setString(2, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return false;
    }

    public void updateGameSession(int gameId, boolean b) {
        try {
            PreparedStatement stmt = con.prepareStatement("update game set gamestatus = ? where gameid = ? ");
            stmt.setBoolean(1, b);
            stmt.setString(2, String.valueOf(gameId));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public ArrayList<Game> getUnFinishedGames(int playerId) throws SQLException {

        String queryString= new String("select ps.PlayerId,p.UserName,ps.sign,g.gameid,GameDate,cell00,cell01,cell02,cell10,cell11,cell12,cell20,cell21,cell22 from playersession ps ,player p,game g\n" +
                " where  ps.playerid=p.playerid  and ps.gameid=g.GameId and ps.gameid=Any(select gameid from playersession where playerid="+playerId+") and g.gamestatus=false  order by ps.gameid");
        PreparedStatement pst=con.prepareStatement(queryString);
        ResultSet rs=pst.executeQuery();
        ArrayList<PlayerSession> playerSessions=new ArrayList<PlayerSession>();

            PlayerSession playerSes;
            while (rs.next()) {
                playerSes = new PlayerSession(rs.getInt(1), rs.getInt(4), rs.getInt(3),rs.getString(5), rs.getBoolean(6),
                        rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9),
                        rs.getBoolean(10), rs.getBoolean(11), rs.getBoolean(12), rs.getBoolean(13),
                        rs.getBoolean(14), null, null);
                playerSessions.add(playerSes);
            }
            ArrayList<Game> unfinishedgames=new ArrayList<Game>();
            for(int i=0;i<playerSessions.size()-1;i+=2)
            {
                ArrayList<PlayerSession> playerSessions1=new ArrayList<PlayerSession>();
                playerSessions1.add(playerSessions.get(i));
                playerSessions1.add(playerSessions.get(i+1));
                Game g=new Game(playerSessions.get(i).GameId,playerSessions.get(i).gameDate,0,false,playerSessions1);
                unfinishedgames.add(g);
                playerSessions.clear();
            }
            return unfinishedgames;


    }

}





