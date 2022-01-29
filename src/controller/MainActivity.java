package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.GameDAO;
import model.Player;
import model.Server;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MainActivity implements Initializable {

    public VBox playersStates;
    public Button startButton;
    public Button stopButton;
    GameDAO database;
    List<Player> onlinePlayers;
    ImageView view;
    private VBox vbox = new VBox();

    @FXML
    public ScrollPane scrollPane;
    Server server = new Server();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            database=new GameDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onlinePlayers = database.getOnlinePlayers();
        listOnlinePlayers();
    }


    public void startClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("yes");
        if (!server.checkServer()) {
            server.initializeServer();
        }
    }

    public void stopClicked(ActionEvent actionEvent) {
        System.out.println("no");
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

                for(Player x : onlinePlayers){
                    System.out.println("inside for loop");
                    view = new ImageView(/*new Image(getClass().getResourceAsStream("resources/avatar.png"))*/);
                    view.setFitHeight(30);
                    view.setPreserveRatio(true);

                    Button button = new Button(x.getUsername(),view);
                    button.setAlignment(Pos.BOTTOM_LEFT);

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
                    vbox.getChildren().add(button);
                    scrollPane.setContent(vbox);
                }
                onlinePlayers.clear();

            }
        });
    }
}
