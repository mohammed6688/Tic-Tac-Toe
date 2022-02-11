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

public class SelectLevelController implements Initializable {

    @FXML
    public AnchorPane rootAnchor;
    @FXML
    Button backtolevel;

    @FXML
    Button singlePlayer, multiPlayers, BackBtn, easyLevel, hardLevel;
    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootAnchor.setOpacity(0);
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(rootAnchor);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();

    }

    public void easyLevelBtnHandling() throws Exception {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(rootAnchor);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../layouts/GameBoard.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) easyLevel.getScene().getWindow();
            //grab your root here

            window.setTitle("Easy Level");
            window.setMinWidth(1000);
            window.setMinHeight(600);

            Scene scene = new Scene(root);
            //set transparent
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();

            window.setOnCloseRequest((e) -> {
                System.exit(1);
            });
        });
        transition.play();

    }

    public void hardLevelBtnHandling() throws Exception {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(rootAnchor);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../layouts/GameBoardHard.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) hardLevel.getScene().getWindow();
            //grab your root here

            window.setTitle("Hard Level");
            window.setMinWidth(1000);
            window.setMinHeight(600);

            Scene scene = new Scene(root);
            //set transparent
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();

            window.setOnCloseRequest((e) -> {
                System.exit(1);
            });
        });
        transition.play();

    }
    public void BackToMain() throws Exception {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(rootAnchor);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../layouts/GameMainFXML.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) BackBtn.getScene().getWindow();
            window.setTitle("Home");
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();
        });
        transition.play();
    }
}
