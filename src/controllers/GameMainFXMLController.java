/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;

import java.net.URL;
import java.nio.channels.Channel;
import java.sql.Connection;
import java.util.ResourceBundle;

import Client.ServerChannel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * @author moham
 */
public class GameMainFXMLController implements Initializable {


    @FXML
    Button backtolevel;
    @FXML
    Button singlePlayer, multiPlayers, BackBtn, easyLevel;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void singlePlayerBtnHandling() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/layouts/SinglePlayer.fxml"));
        Stage window = (Stage) singlePlayer.getScene().getWindow();
        //grab your root here
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        root.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });
        window.setTitle("Choice Level");
        window.setMinWidth(1000);
        window.setMinHeight(600);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });
    }


    public void multiPlayersBtnHandling() throws Exception {
        //   Parent root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));
        if (ServerChannel.startChannel()) {
            System.out.print("sha8alla");
            Parent root = FXMLLoader.load(getClass().getResource("/layouts/SignInScene.fxml"));
            Stage window = (Stage) multiPlayers.getScene().getWindow();
            //grab your root here
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
        }
    }

    public void easyLevelBtnHandling() throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameBoard.fxml"));
        Stage window = (Stage) easyLevel.getScene().getWindow();
        //grab your root here
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        root.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });
        window.setTitle("Easy Level");
        window.setMinWidth(1000);
        window.setMinHeight(600);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });
    }

    public void BackToMain() throws Exception {
        String message = "logout " + SignInController.currentPlayer.getId();
        ServerChannel.logOut(message);
        Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameMainFXML.fxml"));
        Stage window = (Stage) BackBtn.getScene().getWindow();
        //grab your root here
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        //move around here
        root.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });
        window.setTitle("Home");
        window.setMinWidth(1000);
        window.setMinHeight(600);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });
    }


    public void ExitBtnHandling() throws Exception {
        System.exit(1);
    }

}