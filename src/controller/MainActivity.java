package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.GameDAO;
import model.Player;
import model.PlayerSession;
import model.Server;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainActivity implements Initializable {

    public VBox playersStates;
    public Button startButton;
    public Button stopButton;
    public ImageView serverState;
    GameDAO database;
    List<Player> onlinePlayers;
    ImageView view;
    private VBox vbox = new VBox();

    @FXML
    public ScrollPane scrollPane;
    Server server = new Server();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverState.setImage(new Image(getClass().getResourceAsStream("/resources/power-off.png")));
        try {
            database=new GameDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    onlinePlayers = database.getOnlinePlayers();
                    listOnlinePlayers();
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                    }

                }
            }
        });
        th.start();




    }


    public void startClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("yes");
        serverState.setImage(new Image(getClass().getResourceAsStream("/resources/power-on.png")));
        if (!server.checkServer()) {
            server.initializeServer();
        }
    }

    public void stopClicked(ActionEvent actionEvent) {
        System.out.println("no");
        serverState.setImage(new Image(getClass().getResourceAsStream("/resources/power-off.png")));
        if (server.checkServer()) {
            server.stopServer();
        }
    }

    private void listOnlinePlayers(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                try {
                scrollPane.getScene().getStylesheets().add(getClass().getResource("/css/fullpackstyling.css").toString());
                scrollPane.setContent(null);
                vbox.getChildren().clear();
                vbox.getStyleClass().add("onlineVbox");

                HBox hBox;

                for(Player x : onlinePlayers){
                    view = new ImageView(new Image(getClass().getResourceAsStream("/resources/avatar.png")));
                    view.setFitHeight(30);
                    view.setPreserveRatio(true);

                    Button button = new Button(x.getUsername(),view);
                    button.setAlignment(Pos.BOTTOM_LEFT);
                    Circle circle = new Circle(5);
                    circle.getStyleClass().add("circle");
                    if (x.isInGame()){
                        circle.setFill(Paint.valueOf("#E29E00"));
                    }else {
                        circle.setFill(Paint.valueOf("#15ff00"));
                    }
                    hBox=new HBox(button,circle);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setSpacing(5);

                    button.getStyleClass().add("button1");
                    button.setId(""+x.getEmail());
                    if(x.isInGame()){
                        button.setDisable(true);
                    }

                    /*button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            MainController.ps.println("request###"+button.getId()+"###"+emailtxt.getText()+"###"+usernametxt.getText()+"###"+scoretxt.getText());
                            // pop up waiting for response from server
                            ButtonType Yes = new ButtonType("Ok"); // can use an Alert, Dialog, or PopupWindow as needed...
                            alert = new Alert(Alert.AlertType.NONE);
                            alert.setTitle("Information Dialog");
                            alert.setHeaderText("Please Wait The Opponent to respond..");
                            alert.getDialogPane().getButtonTypes().addAll(Yes);

                            DialogPane dialogPane = alert.getDialogPane();
                            dialogPane.getStylesheets().add(
                                    getClass().getResource("/css/fullpackstyling.css").toExternalForm());
                            dialogPane.getStyleClass().add("infoDialog");

                            // hide popup after 3 seconds:
                            PauseTransition delay = new PauseTransition(Duration.seconds(15));
                            delay.setOnFinished(e -> alert.hide());

                            alert.show();
                            delay.play();
                        }
                    });*/
                    vbox.getChildren().add(hBox);
                    scrollPane.setContent(vbox);
                }
                onlinePlayers.clear();
            }
        });
    }
}
