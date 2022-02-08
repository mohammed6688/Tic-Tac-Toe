/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import model.DifficultLevel.Move;
import static model.DifficultLevel.evaluate;
import static model.DifficultLevel.findBestMove;
import static model.DifficultLevel.isMoveLeft;


/**
 *
 * @author moham
 */
public class HardLevelController implements Initializable {

    private String player="X";
    private Button buttonPressed;
    private boolean winner = false;

    private boolean display = false;
    private Preferences prefs ;
    private int score = 0;
    int moveNum = 0;
    Move bestMove;
    private boolean computerWin = false ;
    public Button[][] board=new Button[3][3];
    private Alert alert;
    private DialogPane dialog;

    @FXML
    Button backtolevel;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private  Button btn1;
    @FXML
    private  Button btn2;
    @FXML
    private  Button btn3;
    @FXML
    private  Button btn4;
    @FXML
    private  Button btn5;
    @FXML
    private  Button btn6;
    @FXML
    private  Button btn7;
    @FXML
    private  Button btn8;
    @FXML
    private  Button btn9;

    @FXML
    private Label txtWinner;
    @FXML
    private Label labUserName;
    @FXML
    private Label labScore;
    @FXML
    private  Button rematch;

    @FXML
    private AnchorPane anchorpane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        prefs = Preferences.userNodeForPackage(HardLevelController.class); //may editting to EasyLevelController
        try {
            if (prefs.nodeExists("/controllers"))
            {
                String userName=prefs.get("username","Mohamed");
                score=prefs.getInt("score",0);

                if (userName.length() != 0)
                {
                    labUserName.setText(userName);
                }
                if(score != 0)
                {
                    labScore.setText(""+score);
                }
            }
        }
        catch (BackingStoreException ex) {
            Logger.getLogger(HardLevelController.class.getName()).log(Level.SEVERE,null,ex);
        }

        board[0][0] = btn1;
        board[0][1] = btn2;
        board[0][2] = btn3;
        board[1][0] = btn4;
        board[1][1] = btn5;
        board[1][2] = btn6;
        board[2][0] = btn7;
        board[2][1] = btn8;
        board[2][2] = btn9;

        for(Button[] btns: board)
        {
            for(Button btn: btns)
            {
                btn.addEventHandler(ActionEvent.ACTION,(ActionEvent event)->{

                    if(!winner)
                    {
                        btn.setText("X");
                        btn.setMouseTransparent(true);
                        if(moveNum+1 < 9)
                        {
                            bestMove=findBestMove(board);
                            board[bestMove.row][bestMove.col].setText("O");
                            board[bestMove.row][bestMove.col].setMouseTransparent(true);
                        }

                        moveNum+=2;

                        if (moveNum >= 5)
                        {
                            int result = evaluate(board);
                            if(result == 10 )
                            {
                                txtWinner.setText("Computer Won!");
                                winner=true;
                            }
                            else if (result == -10 )
                            {
                                score+=10;
                                prefs.putInt("Score",score);
                                labScore.setText(""+score);
                                txtWinner.setText("You Won!");
                                winner=true;
                            }
                            else if(isMoveLeft(board)== false)
                            {
                                txtWinner.setText("It's a Draw");
                                winner=true;
                            }
                        }
                    }
                });
            }
        }
    }

    public void RematchBtnHandling() throws Exception
    {
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Rematch");
        // Header Text: null
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure that you want to Rematch ?");

        ButtonType OkBtn = new ButtonType("OK");
        ButtonType CancelBtn = new ButtonType("Cancel");
        // Remove default ButtonTypes
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(OkBtn,CancelBtn);

        alert.getDialogPane().setPrefSize(750, 150);
        dialog=alert.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("../style/rematchAlert.css").toString());
        dialog.getStyleClass().addAll("dialog");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        if(option.isPresent() && option.get() == OkBtn)
        {
            Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameBoardHard.fxml"));
            Stage window = (Stage)rematch.getScene().getWindow();
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
        else
        {
            //don't do anything
        }

    }

    public void BackToChoiceLevel () throws Exception {

        Preferences prefs =Preferences.userNodeForPackage(GameMainFXMLController.class);
        prefs.remove("username");
        prefs.remove("score");

        Parent root = FXMLLoader.load(getClass().getResource("../layouts/SinglePlayer.fxml"));
        Stage window = (Stage)backtolevel.getScene().getWindow();
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

    public void ExitBtnHandling() throws Exception {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Rematch");
        // Header Text: null
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText("Are you sure that you want to Exit ?");

        ButtonType OkBtn = new ButtonType("Yes");
        ButtonType CancelBtn = new ButtonType("Cancel");
        // Remove default ButtonTypes
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(OkBtn,CancelBtn);

        alert.getDialogPane().setPrefSize(750, 150);
        dialog=alert.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("../style/rematchAlert.css").toString());
        dialog.getStyleClass().addAll("dialog");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        if(option.isPresent() && option.get() == OkBtn)
        {
            Preferences prefs =Preferences.userNodeForPackage(GameMainFXMLController.class);
            prefs.remove("username");
            prefs.remove("score");
            System.exit(1);
        }
    }

    public void BackToMain(ActionEvent actionEvent) {

    }
}