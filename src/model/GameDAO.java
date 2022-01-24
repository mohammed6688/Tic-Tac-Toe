package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameDAO {
    public static final String DB_URL="jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME="javaGame";
    public static final String USER="postgres";
    public static final String PASS="mina2508";
    private Connection con;
    private Statement stmt;
     PreparedStatement pst;
    public void connect() throws SQLException {
        con = DriverManager.getConnection(DB_URL+DB_NAME,USER,PASS);
        stmt= con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY) ;

    }
    public List <Player> getOnlinePlayers(){
        try {
            String queryString= new String("select * from player where status = true");
            ResultSet rs= stmt.executeQuery(queryString) ;
            List <Player> onlinePlayers= new ArrayList<>();
            while(rs.next()){
                onlinePlayers.add(new Player(rs.getInt("PlayerId"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("UserPassword"),
                        rs.getBoolean("IsOnline"),
                        rs.getBoolean("IsInGame")));
            }
            return onlinePlayers;
        }catch (SQLException ex) {
            System.out.println("error in online players");
        }

        return null;
    }

    public void getLeaderBoard(){

    }
        public void startGame(int firstPlayerId,int secondPlayerId) throws SQLException{
            String queryString=new String("insert into game (GameDate) values (CURRENT_DATE)");
         stmt=con.createStatement();
         stmt.executeUpdate(queryString,Statement.RETURN_GENERATED_KEYS);
         ResultSet rs=stmt.getGeneratedKeys();
         rs.next();
         int gameId=rs.getInt(1);
         queryString="insert into playersession (PlayerId,GameID,GameStatus) values (?,?,false)";
         pst=con.prepareStatement(queryString);
         pst.setInt(1,firstPlayerId);
         pst.setInt(2,gameId);
         pst.addBatch();
         pst.setInt(1,secondPlayerId);
         pst.setInt(2,gameId);
         pst.addBatch();
         pst.executeBatch();

        }
    public void handleGameRequest(int playerIdRequesting ,int playerIdRequested)throws SQLException{

        String queryString= new String("Select * from player where id=?");
        pst=con.prepareStatement(queryString);
        pst.setInt(1,playerIdRequesting);
        pst.addBatch();
        pst.setInt(1,playerIdRequested);
        pst.addBatch();
        pst.executeBatch();
      ResultSet rs=  pst.getResultSet();
      Vector<Player> players=new Vector<Player>();
        Player p;
      while(rs.next())
      {
           p=new Player(rs.getInt("PlayerId"),rs.getString("UserName"));
           players.add(p);
      }


    }

    public void updatingGame(int playerPickedId,int playerLostId,int GameId,int cellRow,int cellColumn) throws SQLException {
        Integer cR=new Integer(cellRow);
        Integer cC=new Integer(cellColumn);
        String queryString= new String("update playersession set Cell"+cR.toString()+cC.toString()
                +"=? where playerid=? and gameid="+GameId);
        pst=con.prepareStatement(queryString);
        pst.setBoolean(1,true);
        pst.setInt(2,playerPickedId);
        pst.addBatch();
        pst.setBoolean(1,false);
        pst.setInt(2,playerLostId);
        pst.addBatch();
        pst.executeBatch();
        System.out.println("yahhhh");

    }

    public String checkSignIn(String email, String password){
        try {
            String queryString= new String("select * from player where email ="+email);
            ResultSet rs= stmt.executeQuery(queryString);
            Player player = null;
            while(rs.next()){
                player=new Player(rs.getInt("PlayerId"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("UserPassword"),
                rs.getBoolean("IsOnline"),
                rs.getBoolean("IsInGame"));
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
                player=new Player(rs.getInt("PlayerId"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("UserPassword"),
                        rs.getBoolean("IsOnline"),
                        rs.getBoolean("IsInGame"));
            }

            if(player!=null){
                return player;
            }
        } catch (SQLException ex) {
            System.out.println("error in getEmailData");
        }
        return null;
    }
    public Vector<PlayerSession> getUnFinishedGames(int playerId) throws SQLException {
        String queryString= new String("select ps.PlayerId,p.UserName,g.gameid ,GameDate ,cell00,cell01,cell02,cell10,cell11,cell12,cell20,cell21,cell22 from playersession ps ,player p,game g \n" +
                "where ps.gameid=Any(select gameid from playersession where playerid="+playerId+")\n" +
                "and gamestatus=false and p.isonline=true and p.isingame=false  " +
                "and ps.playerid=p.playerid  and ps.gameid=g.GameId order by GameDate DESC");
        ResultSet rs=stmt.executeQuery(queryString);
        Vector<PlayerSession> playerSessions=new Vector<PlayerSession>() ;
        PlayerSession playerSes;
        while(rs.next())
        {
                playerSes=new PlayerSession(rs.getInt(1),rs.getInt(3), rs.getBoolean(5),
                        rs.getBoolean(6),rs.getBoolean(7),rs.getBoolean(8),
                        rs.getBoolean(9),rs.getBoolean(10),rs.getBoolean(11),rs.getBoolean(12),
                rs.getBoolean(13));
            playerSessions.add(playerSes);
        }
        return playerSessions;
    }

}
