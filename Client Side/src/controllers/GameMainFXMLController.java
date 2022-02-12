/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channel;
import java.sql.Connection;
import java.util.ResourceBundle;

import Client.ServerChannel;
import helper.AskDialog;
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


/**
 * @author moham
 */
public class GameMainFXMLController implements Initializable {

    public AnchorPane GameMainRootAnchor;
    @FXML
    Button backtolevel;
    @FXML
    Button singlePlayer, multiPlayers, BackBtn, easyLevel, hardLevel;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        GameMainRootAnchor.setOpacity(0);
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(GameMainRootAnchor);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    public void singlePlayerBtnHandling() throws Exception {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(GameMainRootAnchor);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/layouts/SinglePlayer.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) singlePlayer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();
            window.setTitle("Choice Level");
        });
        transition.play();
    }



    public void multiPlayersBtnHandling() throws Exception {
        //   Parent root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));

        if (ServerChannel.startChannel()) {
            FadeTransition transition = new FadeTransition();
            transition.setDuration(Duration.millis(500));
            transition.setNode(GameMainRootAnchor);
            transition.setFromValue(1);
            transition.setToValue(0);
            transition.setOnFinished(event -> {
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("/layouts/SignInScene.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage window = (Stage) multiPlayers.getScene().getWindow();

                window.setTitle("Multi-Players");
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                window.setScene(scene);
                window.show();

                window.setOnCloseRequest((e) -> {
                    ServerChannel.closeConnection();
                    System.exit(1);
                });
            });
            transition.play();

        } else {
            AskDialog dialog = new AskDialog();
            dialog.serverIssueAlert("There Is Connection Error");
        }

    }



}