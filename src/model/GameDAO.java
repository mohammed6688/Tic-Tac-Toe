package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void getOnlinePlayers(){

    }
    public void getLeaderBoard(){

    }
    public void handleGameRequest(){

    }
    public void updatingGame(){

    }


}
