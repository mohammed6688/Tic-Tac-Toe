package controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class leaderBoard implements Initializable {
    public VBox root;
    public Label secondPlayer;
    public Label firstPlayer;
    public Label thirdPlayer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setOpacity(0);
        fadEffect();
        Preferences prefs = Preferences.userNodeForPackage(winnerController.class);
        String first =prefs.get("first","");
        String second =prefs.get("second","");
        String third =prefs.get("third","");
        firstPlayer.setText(first);
        secondPlayer.setText(second);
        thirdPlayer.setText(third);
    }

    public void back(ActionEvent actionEvent) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(root);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            try {
                Parent root2 = FXMLLoader.load(getClass().getResource("/layouts/TwoPlayers.fxml"));
                Stage window = (Stage) root.getScene().getWindow();
                window.setTitle("Home");
                Scene scene = new Scene(root2);
                scene.setFill(Color.TRANSPARENT);
                window.setScene(scene);
                window.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        transition.play();
    }

    private void fadEffect() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(root);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();

    }
}
