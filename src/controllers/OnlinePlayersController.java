package controllers;

import Client.ServerChannel;
import helper.AskDialog;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class OnlinePlayersController implements Initializable {

    public AnchorPane mainRoot;
    @FXML
    public Button BackBtn2;
    @FXML
    public AnchorPane gamePan;
    @FXML
    public AnchorPane onlinePan;

    @FXML
    private  Button btn1;
    @FXML
    private  Button btn2;
    @FXML
    private  Button btn3;
    @FXML
    private  Button btn4;
    @FXML
    private  Button btn5;
    @FXML
    private  Button btn6;
    @FXML
    private  Button btn7;
    @FXML
    private  Button btn8;
    @FXML
    private  Button btn9;

    List<Player> onlinePlayers = new ArrayList<>();
    @FXML
    Button BackBtn;
    @FXML
    public ScrollPane scrollPane;

    private HBox hbox = new HBox();
    ImageView view;
    Thread thread;
    Alert alert;
    HashMap<String, Player> game = new HashMap();
    boolean myTurn,opponentTurn,gameState=false;
    private String myTic,oppTic;
    private String opponentUsername ;
    private Preferences pref ;
    private Boolean isrecord = false;

    private int currentScore;
    private int opponentScore;

    private Boolean display = false;
    private Player opponentPlayer;
    private HashMap<String, Button> btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
               while (true){
                    try {
                        String data = ServerChannel.dis.readLine();
                        if (data.equals("null")){
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
                                startGame();
                                break;
                            case "gameTic":
                                opponentTurn();
                                break;
                            case "finalgameTic":
                                opponentTurn();
                                reset();
                                break;
                            case "endGame":
                                //endGame();
                                break;
                            case "withdraw":
                                System.out.println("withdraw");
                                updateScore();
                                ServerChannel.ps.println("available "+opponentPlayer.getEmail());
                                Platform.runLater(() -> {
                                    AskDialog  serverIssueAlert  = new AskDialog();
                                    serverIssueAlert.serverIssueAlert("You opponent has withdrawed, you are the winner!!!");
                                    thread.stop();

                                });
                                break;
                            case "close":
                                close();
                            default:
                                //System.out.println("default data "+data);
                                readOnlineList(data);
                        }
                    } catch (IOException ex) {
                        close();
                    }
                }
               if (scrollPane!=null) {
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

    private void startGame() throws IOException {
        Platform.runLater(() -> {
            if (alert.isShowing())
                alert.close();
        });
        String OpponentUsername = ServerChannel.dis.readLine();
        String OpponentMail = ServerChannel.dis.readLine();
        String OpponentId = ServerChannel.dis.readLine();
        String OpponentScore = ServerChannel.dis.readLine();

        opponentPlayer=new Player(Integer.parseInt(OpponentId),
                OpponentUsername,
                OpponentMail,
                "",
                true,
                false);

        //String OpponentUsername = "";
        System.out.println("player 2 accepted");
        showGame(true);
    }

    private void showGame(boolean state) throws IOException {

        Platform.runLater(() -> {
            gamePan.setVisible(true);
            onlinePan.setVisible(false);

//            scoretxt.setText(name);
//            scrollpane.setDisable(true);
//            currentScore = Integer.parseInt(MainController.hash.get("score"));
//            player1lbl.setText(""+currentScore);
//            player2lbl.setText(""+opponentScore);
        });

        System.out.println("my state: "+state);
        myTurn = state;
        opponentTurn = !state;
        gameState = true;
        if(state){
            myTic = "X";
            oppTic = "O";
        }else{
            myTic = "O";
            oppTic = "X";
        }
        System.out.println("my tic" +myTic);
    }

    private void receivedRequest() throws IOException {

        String opponentMail =ServerChannel.dis.readLine();
        String opponentName =ServerChannel.dis.readLine();
        String opponentId =ServerChannel.dis.readLine();
        String opponentScore =ServerChannel.dis.readLine();

        opponentPlayer=new Player(Integer.parseInt(opponentId),
                opponentName,
                opponentMail,
                "",
                true,
                false);


        System.out.println("opponent "+opponentName);
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
                        showGame(false);
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
                    view = new ImageView(new Image(getClass().getResourceAsStream("/Resources/user.png")));
                    view.setFitHeight(150);
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
                    vBox.setSpacing(30);
                    vBox.getStyleClass().add("onlineHbox");

                    button.getStyleClass().add("button1");
                    button.setId("" + x.getEmail());
                    if (x.isInGame()) {
                        button.setDisable(true);
                    }


                    button.setOnAction(event -> {
                        System.out.println(x.getUsername());
                        ServerChannel.ps.println("checkUnFinished " + x.getId() + " " + SignInController.currentPlayer.getId());

                        ServerChannel.ps.println("request " + x.getEmail() + " " + SignInController.currentPlayer.getEmail());

                        for (Player player : onlinePlayers) {
                            if (player.getEmail().equals(x.getEmail()) || player.getEmail().equals(SignInController.currentPlayer.getEmail())) {
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
        //System.out.println(UserName);
        boolean isOnline = Boolean.parseBoolean(token.nextToken());
        boolean isInGame = Boolean.parseBoolean(token.nextToken());
        if (!email.equals(SignInController.currentPlayer.getEmail())) {
            onlinePlayers.add(new Player(
                    playerId,
                    UserName,
                    email,
                    password,
                    isOnline,
                    isInGame
            ));
        }
    }

    public void BackToMain() throws Exception {
        if (gamePan.isVisible()){
            gamePan.setVisible(false);
            onlinePan.setVisible(true);
        }else {
            String message = "logout " + SignInController.currentPlayer.getId();
            if (ServerChannel.logOut(message)) {
                FadeTransition transition = new FadeTransition();
                transition.setDuration(Duration.millis(150));
                transition.setNode(mainRoot);
                transition.setFromValue(1);
                transition.setToValue(0);
                transition.setOnFinished(event -> {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameMainFXML.fxml"));
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
    }

    private void getUnFinishedGames() {
        String message="getUnfinishedGames "+SignInController.currentPlayer.getId();
        String response= ServerChannel.getUnFinishedGames(message);
        System.out.println(response);
    }

    private void opponentTurn(){
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
                        System.out.println("myTic "+ oppTic);

                        checkState();
                    }
                });
            });
            btnOpp.fire();
            myTurn= true;
            opponentTurn = false;
            //stateanc.setStyle("-fx-background-color: #008000");


        } catch (IOException ex) {
            System.out.println("error "+ex);
        }
    }

    private void checkRows(){
        if(btn1.getText().equals(btn2.getText()) && btn2.getText().equals(btn3.getText()) && !btn1.getText().equals("")){
            gameState = false;
            if(btn1.getText().equals(myTic)){
                display = true;
                updateScore();
            }else{
                System.out.println("opp win");
            }
        }
        else if(btn4.getText().equals(btn5.getText()) && btn5.getText().equals(btn6.getText()) && !btn4.getText().equals("")){
            gameState = false;
            if(btn4.getText().equals(myTic)){
                display = true;
                updateScore();
            }else{
                System.out.println("opp won!");
            }
        }
        else if(btn7.getText().equals(btn8.getText()) && btn8.getText().equals(btn9.getText()) && !btn7.getText().equals("")){
            gameState = false;
            if(btn7.getText().equals(myTic)){
                display = true;
                updateScore();
            }
        }
    }

    private void checkColumns(){
        if(btn1.getText().equals(btn4.getText()) && btn4.getText().equals(btn7.getText()) && !btn1.getText().equals("")){
            if(btn1.getText().equals(myTic)){
                display = true;
                updateScore();
            }
            gameState = false;
        }
        else if(btn2.getText().equals(btn5.getText()) && btn5.getText().equals(btn8.getText()) && !btn2.getText().equals("")){
            if(btn2.getText().equals(myTic)){
                display = true;
                updateScore();
            }
            gameState = false;
        }
        else if(btn3.getText().equals(btn6.getText()) && btn6.getText().equals(btn9.getText()) && !btn3.getText().equals("")){
            if(btn3.getText().equals(myTic)){
                display = true;
                updateScore();
            }
            gameState = false;
        }
    }

    private void checkDiagonal(){
        if(btn1.getText().equals(btn5.getText()) && btn5.getText().equals(btn9.getText()) && !btn1.getText().equals("")){
            if(btn1.getText().equals(myTic)){
                display = true;
                updateScore();
            }
            gameState = false;
        }
        else if(btn3.getText().equals(btn5.getText()) && btn5.getText().equals(btn7.getText()) && !btn3.getText().equals("")){
            if(btn3.getText().equals(myTic)){
                display = true;
                updateScore();
            }
            gameState = false;
        }
    }

    private boolean isFullGrid(){
        if(!btn1.getText().equals("") && !btn2.getText().equals("") && !btn3.getText().equals("") && !btn4.getText().equals("")
                && !btn5.getText().equals("") && !btn6.getText().equals("")&& !btn7.getText().equals("")
                && !btn8.getText().equals("") && !btn9.getText().equals("")){
            return true;
        }else{
            return false;
        }
    }

    private boolean checkState(){
        System.out.println("checking state");
        checkColumns();
        checkRows();
        checkDiagonal();

        if(!gameState){
            //ServerChannel.ps.println("updateGameState###"+ServerChannel.hash.get("email"));
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(display){
                        displayVideo("winner");
                        //AskDialog  serverIssueAlert  = new AskDialog();
                        //serverIssueAlert.serverIssueAlert("Congrats !! , your score right now is :"+ MainController.hash.get("score"));

                    }else{
                        displayVideo("lose");
                        //AskDialog  serverIssueAlert  = new AskDialog();
                        //serverIssueAlert.serverIssueAlert("Oh, Hardluck next time..");
                    }
                }
            });
            reset();
            return true; // ended game

        }else if(isFullGrid()){
            Platform.runLater(() -> {
                AskDialog  serverIssueAlert  = new AskDialog();
                serverIssueAlert.serverIssueAlert("It's a draw !!");
            });
            reset();
            return true;
        }
        return false;
    }

    private void reset(){
        //ServerChannel.ps.println("available###"+ServerChannel.hash.get("email"));
        thread.stop();
        Platform.runLater(() -> {
//            ButtonBack reload = new ButtonBack("/view/OnlinePlayer.fxml");
//            reload.navigateToAnotherPage(player1lbl);
        });
    }

    private void displayVideo(String type){
        if(type.equals("winner")){
            System.out.println("you won");
//            ButtonBack displayVideo = new ButtonBack("/view/VideoWindow.fxml");
//            displayVideo.displayVideo("winner","Congratulation");
        }else{
            System.out.println("you loss");
//            ButtonBack displayVideo = new ButtonBack("/view/VideoWindow.fxml");
//            displayVideo.displayVideo("opps","opps!!");
        }
    }

    private void updateScore(){
        /*Platform.runLater(() -> {
            try{
                currentScore += 10;
                ServerChannel.hash.put("score", ""+currentScore);
            } catch(NumberFormatException ex){

            }
            player1lbl.setText(""+currentScore);
            ServerChannel.ps.println("updateScore###"+ServerChannel.hash.get("email")+"###"+currentScore);
        });*/
    }

    @FXML
    public void buttonPressed(ActionEvent e){
        Button buttonPressed ;
        if(gameState && myTurn){
            buttonPressed = (Button) e.getSource();
            if(buttonPressed.getText().equals("")){
                buttonPressed.setText(myTic);
                System.out.println("My Turn " +myTic);

                myTurn = false;
                opponentTurn = true;
//                if(myTurn && myTic.equals("X")){
//                    stateanc.setStyle("-fx-background-color: #008000");
//                }else{
//                    stateanc.setStyle("-fx-background-color: #FA2C56");
//                }
                System.out.println("I pressed "+buttonPressed.getId());
                if(checkState()){
                    ServerChannel.ps.println("finishgameTic "+SignInController.currentPlayer.getId()+" "+ getCellNumber(buttonPressed.getId())+" "+buttonPressed.getId()+" "+myTic);
                }else{
                    ServerChannel.ps.println("gameTic "+ SignInController.currentPlayer.getId() +" "+ getCellNumber(buttonPressed.getId())+" "+buttonPressed.getId()+" "+myTic);
                }
            }
        }
    }

    private String getCellNumber(String id) {
        switch (id){
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
        System.out.println("Server Colsed");

//        Platform.runLater(() -> {
//            AskDialog serverIssueAlert = new AskDialog();
//            serverIssueAlert.serverIssueAlert("There is issue in connection game page will be closed");
//            ButtonBack backtoLoginPage = new ButtonBack("/view/sample.fxml");
//            backtoLoginPage.navigateToAnotherPage(emailtxt);
//        });
        thread.stop();
    }

    public void ExitBtnHandling(ActionEvent actionEvent) {

    }
}
