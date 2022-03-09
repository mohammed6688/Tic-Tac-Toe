package controllers;

import Client.ServerChannel;
import helper.AskDialog;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Player;
import model.PlayerSession;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

public class OnlinePlayersController implements Initializable {

    public AnchorPane mainRoot;
    @FXML
    public Button BackBtn2;
    @FXML
    public AnchorPane gamePan;
    @FXML
    public StackPane onlinePan;
    public AnchorPane rootAnchor;
    public Label playerName;
    public Label playerScore;
    public Label labUserName;
    public Label labScore;


    @FXML
    private Button btn1;
    @FXML
    private Button btn2;
    @FXML
    private Button btn3;
    @FXML
    private Button btn4;
    @FXML
    private Button btn5;
    @FXML
    private Button btn6;
    @FXML
    private Button btn7;
    @FXML
    private Button btn8;
    @FXML
    private Button btn9;

    List<Player> onlinePlayers = new ArrayList<>();
    List<Player> leaderBoard = new ArrayList<>();
    @FXML
    Button BackBtn;
    @FXML
    public ScrollPane scrollPane;
    Button buttonPressed;

    private HBox hbox = new HBox();
    ImageView view;
    Thread thread;
    Alert alert;
    HashMap<String, Player> game = new HashMap();
    boolean myTurn, opponentTurn, gameState = false;
    private String myTic, oppTic;
    public String opponentUsername;
    private Preferences pref;
    private Boolean isrecord = false;

    private int currentScore;
    private int opponentScore;

    private Boolean display = false;
    static public Player opponentPlayer;
    private HashMap<String, Button> btn;
    ArrayList<PlayerSession> unFinishedList = new ArrayList<>();
    private Player clickedPlayer;
    int[][] gameBoard = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    private boolean isTie;
    private boolean secondaryPlayer = false;
    public boolean winner;
    Preferences prefs = Preferences.userNodeForPackage(OnlinePlayersController.class);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootAnchor.setOpacity(0);
        fadEffect();
        playerName.setText(SignInController.currentPlayer.getUsername());
        playerScore.setText(String.valueOf(SignInController.currentPlayer.getScore()));
        buttonPressed = btn1;
        btn = new HashMap();
        btn.put("btn1", btn1);
        btn.put("btn2", btn2);
        btn.put("btn3", btn3);
        btn.put("btn4", btn4);
        btn.put("btn5", btn5);
        btn.put("btn6", btn6);
        btn.put("btn7", btn7);
        btn.put("btn8", btn8);
        btn.put("btn9", btn9);

        ServerChannel.ps.println("playerlist " + SignInController.currentPlayer.getEmail());

        thread = new Thread(() -> {
            while (true) {
                onlinePlayers.clear();
                while (true) {
                    try {
                        String data = ServerChannel.dis.readLine();
                        if (data.equals("null")) {
                            //System.out.println("data is null");
                            break;
                        }
                        switch (data) {
                            case "requestPlaying":
                                System.out.println("request received " + data);
                                receivedRequest();
                                break;
                            case "decline":
                                popUpRefuse();
                                break;
                            case "gameOn":
                                startGame(false);
                                break;
                            case "gameTic":
                                opponentTurn();
                                break;
                            case "finalgameTic":
                                winner=false;
                                prefs.put("userState","loser");
                                prefs.put("winner",opponentUsername);
                                opponentTurn();
                                //moveToWinner();
                                break;
                            case "leaderBoard":
                                gotoLeaderBoard(data);
                                break;
                            case "withdraw":
                                System.out.println("withdraw");
                                prefs.put("userState","withdraw");
//                                Platform.runLater(() -> {
//                                    AskDialog serverIssueAlert = new AskDialog();
//                                    serverIssueAlert.serverIssueAlert("You opponent has withdrawed, you are the winner!!!");
//                                });
                                moveToWinner();
                                break;
                            case "close":
                                close();
                            default:
                                readOnlineList(data);
                        }
                    } catch (IOException ex) {
                        close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("error" + e.toString());
                    }
                }
                if (scrollPane != null) {
                    setOnlinePlayers();
                }
                try {
                    Thread.sleep(300);
                    //System.out.println("thread sleep");
                } catch (InterruptedException ex) {
                    System.out.println("thread closed");
                    thread.stop();
                }

            }
        });
        thread.start();
    }

    private void getUnFinishedGamesList() throws IOException {
        if (ServerChannel.dis.readLine().equals("noUnfinished")) {
            ServerChannel.ps.println("request " + clickedPlayer.getEmail() + " " + SignInController.currentPlayer.getEmail());

            for (Player player : onlinePlayers) {
                if (player.getEmail().equals(clickedPlayer.getEmail()) || player.getEmail().equals(SignInController.currentPlayer.getEmail())) {
                    game.put(player.getEmail(), player);
                }
            }

            // pop up waiting for response from server
            ButtonType Yes = new ButtonType("Ok"); // can use an Alert, Dialog, or PopupWindow as needed...
            alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Wait The Opponent to respond..");
            alert.getDialogPane().getButtonTypes().addAll(Yes);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/style/GameStyle.css").toExternalForm());
            dialogPane.getStyleClass().add("infoDialog");

            // hide popup after 3 seconds:
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(e -> alert.hide());

            alert.show();
            delay.play();
        } else {
            for (int i = 0; i < 2; i++) {
                String data = ServerChannel.dis.readLine();
                StringTokenizer token1 = new StringTokenizer(data, " ");
                PlayerSession playerSession1 = new PlayerSession(Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        token1.nextToken(),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        null,
                        null);
                unFinishedList.add(playerSession1);
            }


            startGame(true);
            System.out.println("list size: " + unFinishedList.size());
        }

    }

    private void startGame(boolean state) throws IOException {
        String OpponentUsername = ServerChannel.dis.readLine();
        String OpponentMail = ServerChannel.dis.readLine();
        String OpponentId = ServerChannel.dis.readLine();
        String OpponentScore = ServerChannel.dis.readLine();
        String flag = ServerChannel.dis.readLine();

        opponentUsername=OpponentUsername;

        Platform.runLater(() -> {
            labUserName.setText(OpponentUsername);
            labScore.setText(OpponentScore);
        });

        Platform.runLater(()->{
            if (alert.isShowing())
                alert.close();
        });


        System.out.println("flag is : " + flag);
        if (flag.equals("true")) {
            //thread.stop();
            for (int i = 0; i < 2; i++) {
                String data = ServerChannel.dis.readLine();
                System.out.println(data);
                StringTokenizer token1 = new StringTokenizer(data, " ");
                PlayerSession playerSession1 = new PlayerSession(Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        token1.nextToken(),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        null,
                        null);
                unFinishedList.add(playerSession1);
            }
            //thread.start();
            System.out.println("list size: " + unFinishedList.size());
            showGame(true, true);

        } else if (flag.equals("false")) {
            opponentPlayer = new Player(Integer.parseInt(OpponentId),
                    OpponentUsername,
                    OpponentMail,
                    "",
                    true,
                    true,
                    Integer.parseInt(OpponentScore));

            //String OpponentUsername = "";
            System.out.println("player 2 accepted");
            showGame(true, false);
        }

    }

    private void showGame(boolean state, boolean unFinishedMode) throws IOException {

        if (unFinishedMode) {
            myTurn = state;
            gameState = true;
            opponentTurn = !state;
            setCells();
        } else {
            System.out.println("my state: " + state);
            myTurn = state;
            opponentTurn = !state;
            gameState = true;
            if (state) {
                myTic = "X";
                oppTic = "O";
            } else {
                myTic = "O";
                oppTic = "X";
            }
            System.out.println("my tic" + myTic);
        }
        Platform.runLater(() -> {
            gamePan.setVisible(true);
            onlinePan.setVisible(false);

//            scoretxt.setText(name);
//            scrollpane.setDisable(true);
//            currentScore = Integer.parseInt(MainController.hash.get("score"));
//            player1lbl.setText(""+currentScore);
//            player2lbl.setText(""+opponentScore);
        });
    }

    private void setCells() {
        for (PlayerSession playerSession : unFinishedList) {
            if (SignInController.currentPlayer.getId() == playerSession.getPlayerId()) {
                System.out.println("my sign is " + playerSession.getSign());
                if (playerSession.getSign() == 1) {
                    myTic = "X";
                    oppTic = "O";
                } else if (playerSession.getSign() == 2) {
                    myTic = "O";
                    oppTic = "X";
                }
            }

            saveCells(playerSession);
            setBtns();
        }
    }

    private void saveCells(PlayerSession playerSession) {
        if (playerSession.isC00()) {
            if (playerSession.getSign() == 1) {
                saveCell("c00", "X");
            } else {
                saveCell("c00", "O");
            }
        }
        if (playerSession.isC01()) {
            if (playerSession.getSign() == 1) {
                saveCell("c01", "X");
            } else {
                saveCell("c01", "O");
            }
        }
        if (playerSession.isC02()) {
            if (playerSession.getSign() == 1) {
                saveCell("c02", "X");
            } else {
                saveCell("c02", "O");
            }
        }

        if (playerSession.isC10()) {
            if (playerSession.getSign() == 1) {
                saveCell("c10", "X");
            } else {
                saveCell("c10", "O");
            }
        }
        if (playerSession.isC11()) {
            if (playerSession.getSign() == 1) {
                saveCell("c11", "X");
            } else {
                saveCell("c11", "O");
            }
        }
        if (playerSession.isC12()) {
            if (playerSession.getSign() == 1) {
                saveCell("c12", "X");
            } else {
                saveCell("c12", "O");
            }
        }

        if (playerSession.isC20()) {
            if (playerSession.getSign() == 1) {
                saveCell("c20", "X");
            } else {
                saveCell("c20", "O");
            }
        }
        if (playerSession.isC21()) {
            if (playerSession.getSign() == 1) {
                saveCell("c21", "X");
            } else {
                saveCell("c21", "O");
            }
        }
        if (playerSession.isC22()) {
            if (playerSession.getSign() == 1) {
                saveCell("c22", "X");
            } else {
                saveCell("c22", "O");
            }
        }

    }

    private void setBtns() {
        Platform.runLater(() -> {


            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (gameBoard[i][j] != 0) {
                        if (gameBoard[i][j] == 1) {
                            switch (i) {
                                case 0:
                                    switch (j) {
                                        case 0:
                                            btn1.setText("X");
                                            break;
                                        case 1:
                                            btn2.setText("X");
                                            break;
                                        case 2:
                                            btn3.setText("X");
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (j) {
                                        case 0:
                                            btn4.setText("X");
                                            break;
                                        case 1:
                                            btn5.setText("X");
                                            break;
                                        case 2:
                                            btn6.setText("X");
                                            break;
                                    }
                                    break;
                                case 2:
                                    switch (j) {
                                        case 0:
                                            btn7.setText("X");
                                            break;
                                        case 1:
                                            btn8.setText("X");
                                            break;
                                        case 2:
                                            btn9.setText("X");
                                            break;
                                    }
                                    break;
                            }
                        } else if (gameBoard[i][j] == 2) {
                            switch (i) {
                                case 0:
                                    switch (j) {
                                        case 0:
                                            btn1.setText("O");
                                            break;
                                        case 1:
                                            btn2.setText("O");
                                            break;
                                        case 2:
                                            btn3.setText("O");
                                            break;
                                    }
                                    break;
                                case 1:
                                    switch (j) {
                                        case 0:
                                            btn4.setText("O");
                                            break;
                                        case 1:
                                            btn5.setText("O");
                                            break;
                                        case 2:
                                            btn6.setText("O");
                                            break;
                                    }
                                    break;
                                case 2:
                                    switch (j) {
                                        case 0:
                                            btn7.setText("O");

                                            break;
                                        case 1:
                                            btn8.setText("O");
                                            break;
                                        case 2:
                                            btn9.setText("O");
                                            break;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void saveCell(String cellNum, String sign) {
        int flag;
        if (sign.equals("X")) {
            flag = 1;
        } else {
            flag = 2;
        }
        switch (cellNum) {
            case "c00":
                gameBoard[0][0] = flag;
                break;
            case "c01":
                gameBoard[0][1] = flag;
                break;
            case "c02":
                gameBoard[0][2] = flag;
                break;
            case "c10":
                gameBoard[1][0] = flag;
                break;
            case "c11":
                gameBoard[1][1] = flag;
                break;
            case "c12":
                gameBoard[1][2] = flag;
                break;
            case "c20":
                gameBoard[2][0] = flag;
                break;
            case "c21":
                gameBoard[2][1] = flag;
                break;
            case "c22":
                gameBoard[2][2] = flag;
                break;
            default:
                break;
        }
    }

    private void receivedRequest() throws IOException {
        secondaryPlayer = true;
        String opponentMail = ServerChannel.dis.readLine();
        String opponentName = ServerChannel.dis.readLine();
        String opponentId = ServerChannel.dis.readLine();
        String opponentScore = ServerChannel.dis.readLine();
        String flag = ServerChannel.dis.readLine();
        opponentUsername=opponentName;

        Platform.runLater(() -> {
            labUserName.setText(opponentName);
            labScore.setText(opponentScore);
        });

        if (flag.equals("true")) {
            for (int i = 0; i < 2; i++) {
                String data = ServerChannel.dis.readLine();
                StringTokenizer token1 = new StringTokenizer(data, " ");
                PlayerSession playerSession1 = new PlayerSession(Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        Integer.parseInt(token1.nextToken()),
                        token1.nextToken(),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        Boolean.parseBoolean(token1.nextToken()),
                        null,
                        null);
                unFinishedList.add(playerSession1);
            }
        }
        opponentPlayer = new Player(Integer.parseInt(opponentId),
                opponentName,
                opponentMail,
                "",
                true,
                false,
                Integer.parseInt(opponentScore));


        System.out.println("opponent " + opponentName);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("received request run");

                ButtonType Yes = new ButtonType("Yes");
                ButtonType No = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Confirmation");
                alert.setHeaderText(opponentName + " wants to Challenge you, Are you Okay with that ?");
                alert.getDialogPane().getButtonTypes().addAll(Yes, No);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/style/GameStyle.css").toExternalForm());
                dialogPane.getStyleClass().add("infoDialog");

                PauseTransition delay = new PauseTransition(Duration.seconds(10));
                delay.setOnFinished(e -> alert.hide());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == Yes) { // accept to play
                    System.out.println("game on");

                    ServerChannel.ps.println("accept " + opponentMail + " " + SignInController.currentPlayer.getEmail());
                    try {
                        if (flag.equals("true")) {
                            showGame(false, true);
                        } else {
                            showGame(false, false);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("no first request");
                    ServerChannel.ps.println("decline " + opponentMail);
                }
                delay.play();
            }

        });
    }

    private void popUpRefuse() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (alert.isShowing())
                    alert.close();
                ButtonType Yes = new ButtonType("Ok");
                alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Your Opponent Refused to Challenge you!");
                alert.getDialogPane().getButtonTypes().addAll(Yes);
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/style/GameStyle.css").toExternalForm());
                dialogPane.getStyleClass().add("infoDialog");
                alert.showAndWait();
            }
        });
    }

    private void setOnlinePlayers() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                scrollPane.getScene().getStylesheets().add(getClass().getResource("/style/GameStyle.css").toString());
                scrollPane.setContent(null);

                hbox.getChildren().clear();
                hbox.setSpacing(80);

                VBox vBox;
                for (Player x : onlinePlayers) {
                    view = new ImageView(new Image(getClass().getResourceAsStream("/Resources/user2.png")));
                    view.setFitHeight(120);
                    view.setPreserveRatio(true);

                    Circle circle = new Circle(5);
                    circle.getStyleClass().add("circle");
                    if (x.isInGame()) {
                        circle.setFill(Paint.valueOf("#E29E00"));
                    } else {
                        circle.setFill(Paint.valueOf("#15ff00"));
                    }

                    Button button = new Button(x.getUsername(), circle);
                    button.setAlignment(Pos.BOTTOM_LEFT);
                    vBox = new VBox(view, button);
                    vBox.setAlignment(Pos.CENTER);
                    vBox.setSpacing(20);
                    vBox.getStyleClass().add("onlineHbox");

                    button.getStyleClass().add("button1");
                    button.setId("" + x.getEmail());
                    if (x.isInGame()) {
                        button.setDisable(true);
                    }


                    button.setOnAction(event -> {
                        System.out.println(x.getUsername());
                        clickedPlayer = x;
                        //ServerChannel.ps.println("checkUnFinished " + x.getId() + " " + SignInController.currentPlayer.getId());
                        ServerChannel.ps.println("request " + clickedPlayer.getEmail() + " " + SignInController.currentPlayer.getEmail());

                        for (Player player : onlinePlayers) {
                            if (player.getEmail().equals(clickedPlayer.getEmail()) || player.getEmail().equals(SignInController.currentPlayer.getEmail())) {
                                game.put(player.getEmail(), player);
                            }
                        }

                        // pop up waiting for response from server
                        ButtonType Yes = new ButtonType("Ok"); // can use an Alert, Dialog, or PopupWindow as needed...
                        alert = new Alert(Alert.AlertType.NONE);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText("Please Wait The Opponent to respond..");
                        alert.getDialogPane().getButtonTypes().addAll(Yes);

                        DialogPane dialogPane = alert.getDialogPane();
                        dialogPane.getStylesheets().add(
                                getClass().getResource("/style/GameStyle.css").toExternalForm());
                        dialogPane.getStyleClass().add("infoDialog");

                        // hide popup after 3 seconds:
                        PauseTransition delay = new PauseTransition(Duration.seconds(5));
                        delay.setOnFinished(e -> alert.hide());

                        alert.show();
                        delay.play();
                    });
                    hbox.getChildren().add(vBox);
                    Group group = new Group(hbox);

                    StackPane imageHolder = new StackPane(group);
                    imageHolder.setAlignment(Pos.CENTER);
                    scrollPane.setContent(imageHolder);

                }
                onlinePlayers.clear();
            }
        });
    }

    private void readOnlineList(String state) {
        StringTokenizer token = new StringTokenizer(state, " ");
        int playerId = Integer.parseInt(token.nextToken());
        String UserName = token.nextToken();
        String email = token.nextToken();
        String password = token.nextToken();
        boolean isOnline = Boolean.parseBoolean(token.nextToken());
        boolean isInGame = Boolean.parseBoolean(token.nextToken());
        int score = Integer.parseInt(token.nextToken());
        if (!email.equals(SignInController.currentPlayer.getEmail())) {
            onlinePlayers.add(new Player(
                    playerId,
                    UserName,
                    email,
                    password,
                    isOnline,
                    isInGame,
                    score
            ));
        }
    }

    private void gotoLeaderBoard(String state) throws IOException {
        System.out.println("in leader");

        String oppPressed = ServerChannel.dis.readLine();

        StringTokenizer token = new StringTokenizer(oppPressed, " ");
        String first = token.nextToken();
        String second = token.nextToken();
        String third = token.nextToken();
        prefs.put("first",first);
        prefs.put("second",second);
        prefs.put("third",third);
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(rootAnchor);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/layouts/leaderBoard(res).fxml"));
                Stage window = (Stage) BackBtn.getScene().getWindow();
                Platform.runLater(() -> {
                    Scene scene = new Scene(root);
                    window.setTitle("leaderBoard");
                    scene.setFill(Color.TRANSPARENT);
                    window.setScene(scene);
                    window.show();
                });

                window.setOnCloseRequest((event1) -> {
                    String message = "logout " + SignInController.currentPlayer.getId();
                    ServerChannel.logOut(message);
                    System.exit(1);
                });
                thread.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        transition.play();
    }

    public void logOut() {
        String message = "logout " + SignInController.currentPlayer.getId();
        if (ServerChannel.logOut(message)) {
            FadeTransition transition = new FadeTransition();
            transition.setDuration(Duration.millis(500));
            transition.setNode(gamePan);
            transition.setFromValue(1);
            transition.setToValue(0);
            transition.setOnFinished(event -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/layouts/GameMainFXML.fxml"));
                    Stage window = (Stage) BackBtn.getScene().getWindow();
                    window.setTitle("Home");
                    Scene scene = new Scene(root);
                    scene.setFill(Color.TRANSPARENT);
                    window.setScene(scene);
                    window.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            transition.play();
        }
    }

    public void BackToMainGame() throws Exception {
        prefs.put("userState","draw");
        ServerChannel.ps.println("withdraw " + SignInController.currentPlayer.getId() + " " + myTic);
        moveToWinner();
    }

    private void startTransition() {

        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(gamePan);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            FadeTransition transition2 = new FadeTransition();
            transition2.setDuration(Duration.millis(500));
            transition2.setNode(gamePan);
            transition2.setFromValue(0);
            transition2.setToValue(1);
            transition2.play();
            transition2.setOnFinished(e -> {
                gamePan.setVisible(false);
                onlinePan.setVisible(true);
            });
        });
        transition.play();


    }

    private void opponentTurn() {
        try {
            String oppPressed = ServerChannel.dis.readLine();
            System.out.println(oppPressed);
            Button btnOpp = btn.get(oppPressed);
            btnOpp.setOnAction(event -> {
                Button button = (Button) event.getSource();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        button.setText(oppTic);
                        System.out.println("myTic " + oppTic);

                        checkState();
                    }
                });
            });
            btnOpp.fire();
            myTurn = true;
            opponentTurn = false;
            //stateanc.setStyle("-fx-background-color: #008000");


        } catch (IOException ex) {
            System.out.println("error " + ex);
        }
    }

    private void checkRows() {
        if (btn1.getText().equals(btn2.getText()) && btn2.getText().equals(btn3.getText()) && !btn1.getText().equals("")) {
            gameState = false;
            if (btn1.getText().equals(myTic)) {
                display = true;
                updateScore();
            } else {
                System.out.println("opp win");
            }
        } else if (btn4.getText().equals(btn5.getText()) && btn5.getText().equals(btn6.getText()) && !btn4.getText().equals("")) {
            gameState = false;
            if (btn4.getText().equals(myTic)) {
                display = true;
                updateScore();
            } else {
                System.out.println("opp won!");
            }
        } else if (btn7.getText().equals(btn8.getText()) && btn8.getText().equals(btn9.getText()) && !btn7.getText().equals("")) {
            gameState = false;
            if (btn7.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
        }
    }

    private void checkColumns() {
        if (btn1.getText().equals(btn4.getText()) && btn4.getText().equals(btn7.getText()) && !btn1.getText().equals("")) {
            if (btn1.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
            gameState = false;
        } else if (btn2.getText().equals(btn5.getText()) && btn5.getText().equals(btn8.getText()) && !btn2.getText().equals("")) {
            if (btn2.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
            gameState = false;
        } else if (btn3.getText().equals(btn6.getText()) && btn6.getText().equals(btn9.getText()) && !btn3.getText().equals("")) {
            if (btn3.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
            gameState = false;
        }
    }

    private void checkDiagonal() {
        if (btn1.getText().equals(btn5.getText()) && btn5.getText().equals(btn9.getText()) && !btn1.getText().equals("")) {
            if (btn1.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
            gameState = false;
        } else if (btn3.getText().equals(btn5.getText()) && btn5.getText().equals(btn7.getText()) && !btn3.getText().equals("")) {
            if (btn3.getText().equals(myTic)) {
                display = true;
                updateScore();
            }
            gameState = false;
        }
    }

    private boolean isFullGrid() {
        if (!btn1.getText().equals("") && !btn2.getText().equals("") && !btn3.getText().equals("") && !btn4.getText().equals("")
                && !btn5.getText().equals("") && !btn6.getText().equals("") && !btn7.getText().equals("")
                && !btn8.getText().equals("") && !btn9.getText().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkState() {
        System.out.println("checking state");
        checkColumns();
        checkRows();
        checkDiagonal();

        if (!gameState) {
            Platform.runLater(() -> {
                if (display) {
                    isTie = false;
                    winner=true;
                    prefs.put("userState","winner");
                    prefs.put("winner",SignInController.currentPlayer.getUsername());
                    try {
                        displayVideo("winner");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //AskDialog  serverIssueAlert  = new AskDialog();
                    //serverIssueAlert.serverIssueAlert("Congrats !! , your score right now is :"+ MainController.hash.get("score"));

                } else {
                    isTie = false;
                    winner=false;
                    prefs.put("userState","loser");
                    prefs.put("winner",opponentUsername);
                    try {
                        displayVideo("lose");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //AskDialog  serverIssueAlert  = new AskDialog();
                    //serverIssueAlert.serverIssueAlert("Oh, Hardluck next time..");
                }
                gamePan.setVisible(false);
                onlinePan.setVisible(true);
            });
//            try {
//                thread.stop();
//                moveToWinner();
//            } catch (IOException e) {
//                System.out.println("msharady");
//            }
            return true; // ended game

        } else if (isFullGrid()) {
            isTie = true;
            winner=false;
            prefs.put("userState","draw");
//            Platform.runLater(() -> {
//                AskDialog serverIssueAlert = new AskDialog();
//                serverIssueAlert.serverIssueAlert("It's a draw !!");
//            });
            if (secondaryPlayer) {
                try {
                    thread.stop();
                    moveToWinner();
                } catch (IOException ev) {
                    System.out.println("msharady");
                }
            }

            return true;
        }
        return false;
    }

    private void moveToWinner() throws IOException {
        //System.out.print("movvvvvved");
        Parent root = FXMLLoader.load(getClass().getResource("/layouts/winnerlayout.fxml"));

        Stage window = (Stage) BackBtn.getScene().getWindow();
        Platform.runLater(() -> {
            Scene scene = new Scene(root);
            window.setTitle("winner_loser");
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();
        });

        window.setOnCloseRequest((event1) -> {
            String message = "logout " + SignInController.currentPlayer.getId();
            ServerChannel.logOut(message);
            System.exit(1);
        });
        thread.stop();
    }

    private void displayVideo(String type) throws IOException {
        if (type.equals("winner")) {
            System.out.println("you won");
            prefs.put("userState","winner");
            prefs.put("winner",SignInController.currentPlayer.getUsername());
//            AskDialog askDialog = new AskDialog();
//            askDialog.serverIssueAlert("yoy win :)");
        } else {
//            AskDialog askDialog = new AskDialog();
//            askDialog.serverIssueAlert("you loss :(");
            System.out.println("you loss");
            prefs.put("userState","loser");
            prefs.put("winner",opponentUsername);
//            ButtonBack displayVideo = new ButtonBack("/view/VideoWindow.fxml");
//            displayVideo.displayVideo("opps","opps!!");
        }
        moveToWinner();
    }

    private void updateScore() {
        ServerChannel.ps.println("updateScore " + SignInController.currentPlayer.getId());
        SignInController.currentPlayer.setScore(SignInController.currentPlayer.getScore() + 10);
        Platform.runLater(() -> {
            playerScore.setText(String.valueOf(SignInController.currentPlayer.getScore()));
        });
    }

    @FXML
    public void buttonPressed(ActionEvent e) {
        if (gameState && myTurn) {
            buttonPressed = (Button) e.getSource();
            if (buttonPressed.getText().equals("")) {
                buttonPressed.setText(myTic);
                System.out.println("My Turn " + myTic);

                myTurn = false;
                opponentTurn = true;
//                if(myTurn && myTic.equals("X")){
//                    stateanc.setStyle("-fx-background-color: #008000");
//                }else{
//                    stateanc.setStyle("-fx-background-color: #FA2C56");
//                }
                System.out.println("I pressed " + buttonPressed.getId());

                if (checkState() && !isTie) {
                    ServerChannel.ps.println("finishgameTic false " + SignInController.currentPlayer.getId() + " " + getCellNumber(buttonPressed.getId()) + " " + buttonPressed.getId() + " " + myTic);
                    try {
                        //thread.stop();
                        moveToWinner();
                    } catch (IOException ev) {
                        System.out.println("msharady");
                    }
                } else if (checkState() && isTie) {
                    ServerChannel.ps.println("finishgameTic true " + SignInController.currentPlayer.getId() + " " + getCellNumber(buttonPressed.getId()) + " " + buttonPressed.getId() + " " + myTic);
                    try {
                        //thread.stop();
                        moveToWinner();
                    } catch (IOException ev) {
                        System.out.println("msharady");
                    }
                } else {
                    ServerChannel.ps.println("gameTic " + SignInController.currentPlayer.getId() + " " + getCellNumber(buttonPressed.getId()) + " " + buttonPressed.getId() + " " + myTic);
                }
            }
        }
    }

    private String getCellNumber(String id) {
        switch (id) {
            case "btn1":
                return "c00";
            case "btn2":
                return "c01";

            case "btn3":
                return "c02";

            case "btn4":
                return "c10";

            case "btn5":
                return "c11";

            case "btn6":
                return "c12";

            case "btn7":
                return "c20";

            case "btn8":
                return "c21";

            case "btn9":
                return "c22";

            default:
                return "";
        }
    }

    private void close() {
        System.out.println("Server Closed");

        Platform.runLater(() -> {
            AskDialog serverIssueAlert = new AskDialog();
            serverIssueAlert.serverIssueAlert("There is issue in connection game page will be closed");

        });
        thread.stop();
    }

    private void fadEffect() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(rootAnchor);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();

    }

    public void ExitBtnHandling(ActionEvent actionEvent) {

    }

    public void leaderBoard(ActionEvent actionEvent) throws IOException {
        ServerChannel.ps.println("leaderboard ");
    }
}