package model;

import java.util.ArrayList;

public class Player {
    int id;
    String username;
    String email;
    String password;
    boolean status;
    boolean inGame;
    ArrayList<PlayerSession> obj;


    public Player(int id, String username, String email, String password, boolean status, boolean inGame, ArrayList<PlayerSession> obj) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.inGame = inGame;
        this.obj = obj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public ArrayList<PlayerSession> getObj() {
        return obj;
    }

    public void setObj(ArrayList<PlayerSession> obj) {
        this.obj = obj;
    }
}

