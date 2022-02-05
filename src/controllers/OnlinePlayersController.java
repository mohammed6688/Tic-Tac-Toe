package controllers;

import helper.AskDialog;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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

    static Socket socket;
    static DataInputStream dis;
    static PrintStream ps;
    public AnchorPane mainRoot;

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
        if (connection()) {
            ps.println("playerlist mohamed@gmail.com");

            thread = new Thread(() -> {
                while (true) {
                    onlinePlayers.clear();
                    do {
                        try {
                            String data = dis.readLine();
                            if (data.equals("null")) {
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
                                    System.out.println("default");
                                    readOnlineList(data);
                            }
                        } catch (IOException ex) {
                            close();
                        }
                    } while (true);
                    setOnlinePlayers();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        thread.stop();
                    }

                }
            });

            thread.start();
        }
    }

    private void startGame() throws IOException {
        Platform.runLater(() -> {
            if(alert.isShowing())
                alert.close();
        });
        String OpponentUsername = dis.readLine();
        System.out.println("player 2 accepted");
        //showGame(true,OpponentUsername);
    }

    private void receivedRequest() throws IOException {
        String opponentData = dis.readLine();
        System.out.println("received request");
        StringTokenizer token = new StringTokenizer(opponentData, " ");
        String opponentMail = token.nextToken();
        Player opponentPlayer=null;
        for (Player player:onlinePlayers){
            if (player.getEmail().equals(opponentMail)){
                opponentPlayer=player;
            }
        }
        if (opponentPlayer!=null) {
            Player finalOpponentPlayer = opponentPlayer;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("received request run");

                    ButtonType Yes = new ButtonType("Yes");
                    ButtonType No = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert = new Alert(Alert.AlertType.NONE);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText(finalOpponentPlayer.getUsername() + " wants to Challenge you, Are you Okay with that ?");
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

                        //ps.println("accept " + MainController.hash.get("email") + " " + MainController.hash.get("username") + " " + opponentMail);
                        //showGame(false, opponentUsername);
                    } else {
                        System.out.println("no first request");
                        ps.println("decline " + opponentMail);
                    }
                    delay.play();
                }

            });
        }
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
                        ps.println("request " + x.getEmail() + " " + "mohamed@gmail.com");

                        for (Player player : onlinePlayers) {
                            if (player.getEmail().equals(x.getEmail()) || player.getEmail().equals("mohamed@gmail.com")) {
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
        System.out.println(UserName);
        boolean isOnline = Boolean.parseBoolean(token.nextToken());
        boolean isInGame = Boolean.parseBoolean(token.nextToken());
        onlinePlayers.add(new Player(
                playerId,
                UserName,
                email,
                password,
                isOnline,
                isInGame
        ));
    }

    public boolean connection() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("127.0.0.1", 5005);

                dis = new DataInputStream(socket.getInputStream());
                ps = new PrintStream(socket.getOutputStream());
            }

            return true;
        } catch (IOException ex) {
            try {
                System.out.println("closing socket in main controller");
                if (socket != null) {
                    socket.close();
                    dis.close();
                    ps.close();
                }
            } catch (IOException ex1) {
                System.out.println("error in closing socket");
            }
            return false;
        }
    }

    public void BackToMain() throws Exception {
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

    private void close() {
        System.out.println("Server Colsed");

        Platform.runLater(() -> {
            AskDialog serverIssueAlert  = new AskDialog();
            serverIssueAlert.serverIssueAlert("There is issue in connection game page will be closed");
//            ButtonBack backtoLoginPage = new ButtonBack("/view/sample.fxml");
//            backtoLoginPage.navigateToAnotherPage(emailtxt);
        });
        thread.stop();
    }
}
