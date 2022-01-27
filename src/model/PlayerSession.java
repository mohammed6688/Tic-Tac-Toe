package model;

public class PlayerSession {

    int playerId;
    int GameId;
    int sign;
    boolean c00, c01, c02,
            c10,c11,c12,
            c20,c21,c22;
    Game game;
    Player player;


    public PlayerSession(int playerId, int gameId,int sign, boolean c01, boolean c02, boolean c03, boolean c10, boolean c11, boolean c12, boolean c20, boolean c21, boolean c22, Game game, Player player) {
        this.playerId = playerId;
        GameId = gameId;
        this.c00 = c01;
        this.c01 = c02;
        this.c02 = c03;
        this.c10 = c10;
        this.c11 = c11;
        this.c12 = c12;
        this.c20 = c20;
        this.c21 = c21;
        this.c22 = c22;
        this.game = game;
        this.player = player;
    }
    public PlayerSession(int playerId, int gameId,int sign) {
        this.playerId = playerId;
        GameId = gameId;
        this.sign=sign;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public boolean isC00() {
        return c00;
    }

    public void setC00(boolean c00) {
        this.c00 = c00;
    }

    public boolean isC01() {
        return c01;
    }

    public void setC01(boolean c01) {
        this.c01 = c01;
    }

    public boolean isC02() {
        return c02;
    }

    public void setC02(boolean c02) {
        this.c02 = c02;
    }

    public boolean isC10() {
        return c10;
    }

    public void setC10(boolean c10) {
        this.c10 = c10;
    }

    public boolean isC11() {
        return c11;
    }

    public void setC11(boolean c11) {
        this.c11 = c11;
    }

    public boolean isC12() {
        return c12;
    }

    public void setC12(boolean c12) {
        this.c12 = c12;
    }

    public boolean isC20() {
        return c20;
    }

    public void setC20(boolean c20) {
        this.c20 = c20;
    }

    public boolean isC21() {
        return c21;
    }

    public void setC21(boolean c21) {
        this.c21 = c21;
    }

    public boolean isC22() {
        return c22;
    }

    public void setC22(boolean c22) {
        this.c22 = c22;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCell(int cellRow ,int cellColumn,boolean cellValue){
        switch (cellRow){
            case 0:
                switch (cellColumn){
                    case 0:
                        setC00(cellValue);
                        break;
                    case 1:
                        setC01(cellValue);
                        break;
                    case 2:
                        setC02(cellValue);
                        break;
                }
                break;
            case 1:
                switch (cellColumn){
                    case 0:
                        setC10(cellValue);
                        break;
                    case 1:
                        setC11(cellValue);
                        break;
                    case 2:
                        setC12(cellValue);
                        break;
                }
                break;
            case 2:
                switch (cellColumn){
                    case 0:
                        setC20(cellValue);
                        break;
                    case 1:
                        setC21(cellValue);
                        break;
                    case 2:
                        setC22(cellValue);
                        break;
                }
                break;
        }
    }
}
