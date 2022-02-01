//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        this.connect();
    }

    public GameDAO getDatabase() throws SQLException {
        if (instanceData == null) {
            instanceData = new GameDAO();
        }

        return instanceData;
    }

    private void connect() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javaGame", "postgres", "mina2508");
    }

    public synchronized List<Player> getOnlinePlayers() {
        try {
            new String("select * from player where isonline = true");
            PreparedStatement stmt = this.con.prepareStatement("select * from player where isonline = true", 1005, 1008);
            ResultSet rs = stmt.executeQuery();
            ArrayList onlinePlayers = new ArrayList();

            while(rs.next()) {
                onlinePlayers.add(new Player(rs.getInt("playerid"), rs.getString("username"), rs.getString("email"), rs.getString("userpassword"), rs.getBoolean("isonline"), rs.getBoolean("isingame")));
            }

            return onlinePlayers;
        } catch (SQLException var5) {
            System.out.println("error in online players");
            return null;
        }
    }

    public List<Player> getLeaderBoard() {
        try {
            String queryString = new String("select id , username ,score from player ORDER BY score DESC ");
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            ArrayList leaderBoard = new ArrayList();

            while(rs.next()) {
                leaderBoard.add(new Player(rs.getInt("playerid"), rs.getString("username"), rs.getString("score"), rs.getString("userpassword"), rs.getBoolean("isonline"), rs.getBoolean("isingame")));
            }

            return leaderBoard;
        } catch (SQLException var5) {
            System.out.println("error in online leaderboard");
            return null;
        }
    }

    public void updateScore(int player_id, int score, List<Player> players) {
        try {
            PreparedStatement preparedStatement = this.con.prepareStatement("UPDATE player SET score=? WHERE id=?");
            preparedStatement.setInt(1, score);
            preparedStatement.setInt(2, player_id);
            int isUpdated = preparedStatement.executeUpdate();
            if (isUpdated > 0) {
                Player p = (Player)players.get(player_id);
                p.setScore(score);

                for(int i = 0; i < players.size(); ++i) {
                    Player playerFound = (Player)players.get(i);
                    if (playerFound.getId() == player_id) {
                        players.set(i, p);
                    }
                }
            }
        } catch (SQLException var9) {
            System.out.println("error in online leaderboard");
        }

    }

    public void updatingGame(PlayerSession firstPlayerSession, PlayerSession secondPlayerSession) throws SQLException {
        String query = new String("update playersession set cell00=? , cell01=? ,cell02=? , cell10=? , cell11=? , cell12=? , cell20=? , cell21=? ,cell22=? where playerId=? and gameId=?");
        PreparedStatement pst = this.con.prepareStatement(query);
        pst.setBoolean(1, firstPlayerSession.c00);
        pst.setBoolean(2, firstPlayerSession.c01);
        pst.setBoolean(3, firstPlayerSession.c02);
        pst.setBoolean(4, firstPlayerSession.c10);
        pst.setBoolean(5, firstPlayerSession.c11);
        pst.setBoolean(6, firstPlayerSession.c12);
        pst.setBoolean(7, firstPlayerSession.c20);
        pst.setBoolean(8, firstPlayerSession.c21);
        pst.setBoolean(9, firstPlayerSession.c22);
        pst.setInt(10, firstPlayerSession.playerId);
        pst.setInt(11, firstPlayerSession.GameId);
        pst.addBatch();
        pst.setBoolean(1, secondPlayerSession.c00);
        pst.setBoolean(2, secondPlayerSession.c01);
        pst.setBoolean(3, secondPlayerSession.c02);
        pst.setBoolean(4, secondPlayerSession.c10);
        pst.setBoolean(5, secondPlayerSession.c11);
        pst.setBoolean(6, secondPlayerSession.c12);
        pst.setBoolean(7, secondPlayerSession.c20);
        pst.setBoolean(8, secondPlayerSession.c21);
        pst.setBoolean(9, secondPlayerSession.c22);
        pst.setInt(10, secondPlayerSession.playerId);
        pst.setInt(11, secondPlayerSession.GameId);
        pst.addBatch();
        pst.executeBatch();
        System.out.println("done");
    }

    public String checkSignUp(String userName, String password, String email) throws SQLException {
        try {
            PreparedStatement stmt = this.con.prepareStatement("insert into player (userName,userPassword,email,isonline,isingame,score) values(?,?,?,true,false,0)");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setString(3, email);
            int var5 = stmt.executeUpdate();
            return "SignedUp Successfully";
        } catch (SQLException var6) {
            return "username or email already exists";
        }
    }

    public String checkSignIn(String email, String password) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("select * from player where email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            Player player;
            for(player = null; rs.next(); player = new Player(rs.getInt("id"), rs.getString("username"), rs.getString("email"), rs.getString("userpassword"), rs.getBoolean("isonline"), rs.getBoolean("isingame"))) {
            }

            System.out.println(email + " " + player.password);
            return player != null && password.equals(player.password) ? "Logged in successfully" : "Email or Password is not correct";
        } catch (SQLException var6) {
            System.out.println(var6);
            return "Connection issue, please try again later";
        }
    }

    public void logOut(int PlayerId) {
        String query = new String("Update player set isonline=false ,isingame=false where PlayerId=?");

        try {
            PreparedStatement pst = this.con.prepareStatement(query);
            Throwable var4 = null;

            try {
                pst.setInt(1, PlayerId);
                pst.executeUpdate();
            } catch (Throwable var14) {
                var4 = var14;
                throw var14;
            } finally {
                if (pst != null) {
                    if (var4 != null) {
                        try {
                            pst.close();
                        } catch (Throwable var13) {
                            var4.addSuppressed(var13);
                        }
                    } else {
                        pst.close();
                    }
                }

            }
        } catch (SQLException var16) {
            System.out.println(var16);
        }

    }

    public Player getEmailData(String email) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("select * from player where email =?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            Player player;
            for(player = null; rs.next(); player = new Player(rs.getInt("playerid"), rs.getString("username"), rs.getString("email"), rs.getString("userpassword"), rs.getBoolean("isonline"), rs.getBoolean("isingame"))) {
            }

            if (player != null) {
                return player;
            }
        } catch (SQLException var5) {
            System.out.println("error in getEmailData");
        }

        return null;
    }

    public Game createGameSession(String MainPlayer, String SecondaryPlayer) throws SQLException {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            PreparedStatement stmt = this.con.prepareStatement("insert into game (gamedate,gamestatus) values(?,?) ", 1);
            stmt.setString(1, String.valueOf(now));
            stmt.setBoolean(2, false);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            Game game;
            for(game = null; rs.next(); game = new Game(rs.getInt("gameid"), rs.getString("gamedate"), rs.getInt("gamewinnerid"), rs.getBoolean("gamestatus"), (ArrayList)null)) {
            }

            this.createPlayerSession(MainPlayer, SecondaryPlayer, game);
            return game;
        } catch (SQLException var8) {
            System.out.println(var8);
            return null;
        }
    }

    private int createPlayerSession(String mainPlayer, String secondaryPlayer, Game game) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("insert into playersession (playerId,GameId) values(?,?) ");
            stmt.setString(1, mainPlayer);
            stmt.setInt(2, game.gameId);
            stmt.executeUpdate();
            PreparedStatement stmt2 = this.con.prepareStatement("insert into playersession (playerId,GameId) values(?,?) ");
            stmt2.setString(1, secondaryPlayer);
            stmt2.setInt(2, game.gameId);
            stmt2.executeUpdate();
            this.updateInGame(mainPlayer, secondaryPlayer, true);
            return 1;
        } catch (SQLException var6) {
            System.out.println(var6);
            return -1;
        }
    }

    private void updateInGame(String mainPlayer, String secondaryPlayer, boolean state) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("update player set isingame = ? where id = ? ");
            stmt.setBoolean(1, state);
            stmt.setString(2, mainPlayer);
            stmt.executeUpdate();
            PreparedStatement stmt2 = this.con.prepareStatement("update player set isingame = ? where id = ? ");
            stmt2.setBoolean(1, state);
            stmt2.setString(2, secondaryPlayer);
            stmt2.executeUpdate();
        } catch (SQLException var6) {
            System.out.println(var6);
        }

    }

    public boolean updateStatus(String email, boolean status) throws SQLException {
        try {
            PreparedStatement stmt = this.con.prepareStatement("update player set isonline= ? where email =?");
            stmt.setBoolean(1, status);
            stmt.setString(2, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException var4) {
            System.out.println(var4);
            return false;
        }
    }

    public void updateGameSession(int gameId, boolean b) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("update game set gamestatus = ? where gameid = ? ");
            stmt.setBoolean(1, b);
            stmt.setString(2, String.valueOf(gameId));
            stmt.executeUpdate();
        } catch (SQLException var4) {
            System.out.println(var4);
        }

    }

    public ArrayList<PlayerSession> getUnFinishedGames(int playerId) throws SQLException {
        String queryString = new String("select ps.PlayerId,p.UserName,ps.sign,g.gameid,GameDate,cell00,cell01,cell02,cell10,cell11,cell12,cell20,cell21,cell22 from playersession ps ,player p,game g\n where  ps.playerid=p.playerid  and ps.gameid=g.GameId and ps.gameid=Any(select gameid from playersession where playerid=" + playerId + ") and g.gamestatus=false  order by GameDate DESC");
        PreparedStatement pst = this.con.prepareStatement(queryString);
        ResultSet rs = pst.executeQuery();
        ArrayList playerSessions = new ArrayList();

        while(rs.next()) {
            PlayerSession playerSes = new PlayerSession(rs.getInt(1), rs.getInt(4), rs.getInt(3), rs.getBoolean(6), rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9), rs.getBoolean(10), rs.getBoolean(11), rs.getBoolean(12), rs.getBoolean(13), rs.getBoolean(14), (Game)null, (Player)null);
            playerSessions.add(playerSes);
        }

        return playerSessions;
    }
}
