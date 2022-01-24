package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameDAO {

    public static final String DB_URL="jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME="java test";
    public static final String USER="postgres";
    public static final String PASS="123456";

    private Connection con;
    private Statement stmt;

    private void connect() throws SQLException {
        con = DriverManager.getConnection(DB_URL+DB_NAME,USER,PASS);
        stmt= con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY) ;
    }

    public List <Player> getOnlinePlayers(){
        try {
            String queryString= new String("select * from player where status = true");
            ResultSet rs= stmt.executeQuery(queryString) ;
            List <Player> onlinePlayers= new ArrayList<>();
            while(rs.next()){
                onlinePlayers.add(new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame")));
            }
            return onlinePlayers;
        }catch (SQLException ex) {
            System.out.println("error in online players");
        }

        return null;
    }

    public void getLeaderBoard(){

    }

    public void handleGameRequest(){

    }

    public void updatingGame(){

    }

    public String checkSignIn(String email, String password){
        try {
            String queryString= new String("select * from player where email ="+email);
            ResultSet rs= stmt.executeQuery(queryString);
            Player player = null;
            while(rs.next()){
                player=new Player(rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("status"),
                rs.getBoolean("inGame"));
            }

            if(player!=null && password.equals(player.password)){
                return "Logged in successfully";
            }else {
                return "Password is not correct";
            }
        } catch (SQLException ex) {
            return "Connection issue, please try again later";
        }
    }

    public Player getEmailData(String email){
        try {
            String queryString= new String("select * from player where email ="+email);
            ResultSet rs= stmt.executeQuery(queryString);
            Player player = null;
            while(rs.next()){
                player=new Player(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("inGame"));
            }

            if(player!=null){
                return player;
            }
        } catch (SQLException ex) {
            System.out.println("error in getEmailData");
        }
        return null;
    }

}
