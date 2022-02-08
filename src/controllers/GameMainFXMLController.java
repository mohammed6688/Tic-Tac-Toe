/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import Client.ServerChannel;
import helper.CustomDialog;
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
    Button backtolevel;
    @FXML
     Button singlePlayer, multiPlayers , BackBtn , easyLevel ,hardLevel;
    private double xOffset = 0;
    private double yOffset = 0;
    Preferences prefs;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        prefs = Preferences.userNodeForPackage(GameMainFXMLController.class);
    }   

 public void singlePlayerBtnHandling () throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/layouts/SinglePlayer.fxml"));
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

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.show();

        window.setOnCloseRequest((event) -> {
            System.exit(1);
        });       
    }



    public void easyLevelBtnHandling() throws Exception {

        if (prefs.nodeExists("/controllers")) {
            String s = prefs.get("username", "");
            System.out.println(s.length());
            if (s.length() == 0) {
                CustomDialog cd = new CustomDialog();
                Boolean isCancled = cd.displayDialog("Enter Your Name");
                prefs.put("username", cd.getName());
                if (!isCancled) {

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
            }
            else
            {
             //don't do anything
            }
        }
    }

    public void hardLevelBtnHandling() throws Exception {

        if (prefs.nodeExists("/controllers")) {
            String s = prefs.get("username", "");
            System.out.println(s.length());
            if (s.length() == 0) {
                CustomDialog cd = new CustomDialog();
                Boolean isCancled = cd.displayDialog("Enter Your Name");
                prefs.put("username", cd.getName());
                if (!isCancled) {

                    Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameBoardHard.fxml"));
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
                    window.setTitle("Hard Level");
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
            }
            else
            {
                //don't do anything
            }
        }
    }

 public void multiPlayersBtnHandling() throws Exception {


     if (ServerChannel.startChannel()) {
         Parent root = FXMLLoader.load(getClass().getResource("/layouts/SignInScene.fxml"));
         Stage window = (Stage) multiPlayers.getScene().getWindow();
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

         Scene scene = new Scene(root);
         //set transparent
         scene.setFill(Color.TRANSPARENT);
         window.setScene(scene);
         window.show();

         window.setOnCloseRequest((event) -> {
             ServerChannel.closeConnection();
             System.exit(1);
         });

     } else {
         System.out.print("msh sha8alla");

     }

    }


      public void BackToMain() throws Exception {

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


    public void ExitBtnHandling() throws Exception {
        System.exit(1);
    }
}