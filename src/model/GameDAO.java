package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "Java Game";
    public static final String USER = "postgres";
    public static final String PASS = "1502654";

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
            String queryString = new String("select * from player where status = true");
            PreparedStatement stmt = con.prepareStatement("select * from player where status = true",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
            List<Player> onlinePlayers = new ArrayList<>();
            System.out.println(onlinePlayers.size());
            while (rs.next()) {
                onlinePlayers.add(new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame")));
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
                leaderBoard.add(new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("score"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame")));
            }
            return leaderBoard;
        } catch (SQLException ex) {
            System.out.println("error in online leaderboard");
        }

        return null;

    }

    //////////////////////// updatescore////////////////////
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

    public void handleGameRequest() {

    }

    public void updatingGame() {

    }

    public String checkSignIn(String email, String password) {
        try {
            PreparedStatement stmt = con.prepareStatement("select * from player where email = ?");
            stmt.setString(1, "mohammed668800@gmail.com");
            ResultSet rs = stmt.executeQuery();
            Player player = null;
            while (rs.next()) {
                player = new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame"));
            }
            System.out.println(email + " " + player.password);

            if (player != null && password.equals(player.password)) {
                return "Logged in successfully";
            } else {
                return "Password is not correct";
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            return "Connection issue, please try again later";
        }
    }

    public Player getEmailData(String email) {
        try {
            PreparedStatement stmt = con.prepareStatement("select * from player where email =?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            Player player = null;
            while (rs.next()) {
                player = new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame"));
            }

            if (player != null) {
                return player;
            }
        } catch (SQLException ex) {
            System.out.println("error in getEmailData");
        }
        return null;
    }

    public int createGameSession(String MainPlayer, String SecondaryPlayer) throws SQLException {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            PreparedStatement stmt = con.prepareStatement("insert into game (gameDate,gameStatus) values(?,?) ",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, String.valueOf(now));
            stmt.setBoolean(2, false);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            Game game = null;

            while (rs.next()) {
                game = new Game(rs.getInt("gameId"),
                        rs.getString("gameDate"),
                        rs.getInt("gameWinnerId"),
                        rs.getBoolean("gameStatus"),
                        null);
            }

            return createPlayerSession(MainPlayer, SecondaryPlayer, game);

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return -1;
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

            PreparedStatement stmt = con.prepareStatement("update player set ingame = ? where id = ? ");
            stmt.setBoolean(1, state);
            stmt.setString(2, mainPlayer);
            stmt.executeUpdate();

            PreparedStatement stmt2 = con.prepareStatement("update player set ingame = ? where id = ? ");
            stmt2.setBoolean(1, state);
            stmt2.setString(2, secondaryPlayer);
            stmt2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public boolean updateStatus(String email, boolean status) throws SQLException {
        try {
            PreparedStatement stmt = con.prepareStatement("update player set status= ? where email =?");
            stmt.setBoolean(1, status);
            stmt.setString(2, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);

        }
        return false;
    }
}
