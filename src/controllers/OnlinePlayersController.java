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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class OnlinePlayersController implements Initializable {

    public AnchorPane mainRoot;
    @FXML
    public Button BackBtn2;

    @FXML
    public AnchorPane gamePan;

    @FXML
    public AnchorPane onlinePan;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        //String OpponentUsername = ServerChannel.dis.readLine();
        String OpponentUsername = "";
        System.out.println("player 2 accepted");
        showGame(OpponentUsername);
    }

    private void showGame(String opponentUsername) throws IOException {

        Platform.runLater(() -> {
            gamePan.setVisible(true);
            onlinePan.setVisible(false);

//                statelbl.setText(myTic);
//                if(myTurn && myTic.equals("X")){
//                    stateanc.setStyle("-fx-background-color: #008000");
//                }else{
//                    stateanc.setStyle("-fx-background-color: #FA2C56");
//                }
//
//                scoretxt.setText(name);
//                scrollpane.setDisable(true);
//                currentScore = Integer.parseInt(MainController.hash.get("score"));
//                player1lbl.setText(""+currentScore);
//                player2lbl.setText(""+opponentScore);
        });
    }

    private void receivedRequest() throws IOException {

        String opponentMail =ServerChannel.dis.readLine();
        String opponentName =ServerChannel.dis.readLine();
        String opponentId =ServerChannel.dis.readLine();
        String opponentScore =ServerChannel.dis.readLine();

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
                        showGame(opponentName);
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
