package controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Player;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class winnerController implements Initializable {
    public StackPane winnerPan;
    public Label winnerName;
    @FXML
    private Button BackBtn;
    @FXML
    public AnchorPane mainRoot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setWinner();
    }

    public void BackToMain() throws Exception {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(mainRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));
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

    public void setWinner() {
        Preferences prefs = Preferences.userNodeForPackage(winnerController.class);
        String draw =prefs.get("userState","");
        String winner =prefs.get("winner","");
        System.out.println("user state: "+draw);
        switch (draw){
            case "winner":
            case "loser":
                Platform.runLater(()->{
                    winnerName.setText(winner);
                });
                break;
            case "draw":
                Platform.runLater(()->{
                    winnerName.setText("its Draw No Winner!!");
                });
                break;
            default:
                Platform.runLater(()->{
                    winnerName.setText("its withDraw you can complete game at any time");
                });
                break;
        }
    }
}
