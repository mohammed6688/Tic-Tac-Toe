/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 *
 * @author moham
 */
public class GameMainFXMLController implements Initializable {


    @FXML
    Button BackBtn2;
    @FXML
     Button  singlePlayer, multiPlayers , BackBtn;
    private double xOffset = 0;
    private double yOffset = 0;    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }   

 public void singlePlayerBtnHandling () throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("../layouts/SinglePlayer.fxml"));
        Stage window = (Stage)singlePlayer.getScene().getWindow();
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
         //MainController.enableWatchGameButton(true);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });       
    }

 public void multiPlayersBtnHandling () throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));
        Stage window = (Stage)multiPlayers.getScene().getWindow();
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
        window.setTitle("Multi-Players");
        window.setMinWidth(1000);
        window.setMinHeight(600);
         //MainController.enableWatchGameButton(true);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });       
    }

      public void BackToMain () throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameMainFXML.fxml"));
        Stage window = (Stage)BackBtn.getScene().getWindow();
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
    public void BackToMain2() throws Exception {
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("GameMainFXML.fxml"));
        Stage window = (Stage)this.BackBtn2.getScene().getWindow();
        root.setOnMousePressed((event) -> {
            this.xOffset = event.getSceneX();
            this.yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((event) -> {
            window.setX(event.getScreenX() - this.xOffset);
            window.setY(event.getScreenY() - this.yOffset);
        });
        window.setTitle("Home");
        window.setMinWidth(1000.0D);
        window.setMinHeight(600.0D);
        Scene scene = new Scene(root);
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