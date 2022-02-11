package controllers;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class winnerController implements Initializable {
    @FXML
    private Button BackBtn;
    @FXML
    public AnchorPane mainRoot;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

}
