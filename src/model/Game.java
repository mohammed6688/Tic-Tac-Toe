package model;

import java.util.ArrayList;

public class Game {
    int gameId;
    String gameDate;
    int gameWinnerId;
    boolean gameStatus;
    ArrayList<PlayerSession> obj;

    public Game(int gameId, String gameDate, int gameWinnerId, boolean gameStatus, ArrayList<PlayerSession> obj) {
        this.gameId = gameId;
        this.gameDate = gameDate;
        this.gameWinnerId = gameWinnerId;
        this.gameStatus = gameStatus;
        this.obj = obj;
    }


    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public int getGameWinnerId() {
        return gameWinnerId;
    }

    public void setGameWinnerId(int gameWinnerId) {
        this.gameWinnerId = gameWinnerId;
    }

    public boolean isGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(boolean gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ArrayList<PlayerSession> getObj() {
        return obj;
    }

    public void setObj(ArrayList<PlayerSession> obj) {
        this.obj = obj;
    }
}
