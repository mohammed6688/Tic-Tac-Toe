package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameDAO {

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "java test";
    public static final String USER = "postgres";
    public static final String PASS = "123456";

    private Connection con;
    private Statement stmt;

    private void connect() throws SQLException {
        con = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public List<Player> getOnlinePlayers() {
        try {
            String queryString = new String("select * from player where status = true");
            ResultSet rs = stmt.executeQuery(queryString);
            List<Player> onlinePlayers = new ArrayList<>();
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

    ///////////////////////////////////////////////////
    public List<Player> getLeaderBoard() {
        try {
            String queryString = new String("select id , username ,score from player ORDER BY score DESC ");
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
    ///////////////////////////////////////////
    /// update Score//////

    public static void updateScore(int player_id, int score) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE player SET score=? WHERE id=?");
            preparedStatement.setInt(1, score);
            preparedStatement.setInt(2, player_id);
            int isUpdated = preparedStatement.executeUpdate();
            if (isUpdated > 0) {
                Player p = player.get(player_id);
                p.setScore(score);
                player.replace(player_id, p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ////////////////////////////////////////////////////////

    public void register() {

    }

    public void sendMessage() {

    }

    public void handleGameRequest() {

    }

    public void updatingGame() {

    }

    public String checkSignIn(String email, String password) {
        try {
            String queryString = new String("select * from player where email =" + email);
            ResultSet rs = stmt.executeQuery(queryString);
            Player player = null;
            while (rs.next()) {
                player = new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame"));
            }

            if (player != null && password.equals(player.password)) {
                return "Logged in successfully";
            } else {
                return "Password is not correct";
            }
        } catch (SQLException ex) {
            return "Connection issue, please try again later";
        }
    }

    public Player getEmailData(String email) {
        try {
            String queryString = new String("select * from player where email =" + email);
            ResultSet rs = stmt.executeQuery(queryString);
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

}
