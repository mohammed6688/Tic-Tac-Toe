/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package controllers;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 *
 * @author moham
 */
public class EasyLevelController implements Initializable {

    private String player="X";
    private Button buttonPressed;
    private boolean winner = false;

    private boolean display = false;
    private Preferences prefs ;
    private int score = 0;
    private boolean computerWin = false ;

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

        prefs = Preferences.userNodeForPackage(EasyLevelController.class);
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
            Logger.getLogger(EasyLevelController.class.getName()).log(Level.SEVERE,null,ex);
        }
    }   

    public void buttonPressed(ActionEvent e)
    {
        if(!winner)
        {
            buttonPressed = (Button) e.getSource();
            if(buttonPressed.getText().equals(""))
            {
                buttonPressed.setText(player);
                if (player == "X")
                {
                    player="O";
                }
                else
                {
                    player="X";
                }
                checkState();
                if(!winner)
                {
                    computerTurn();
                    checkState();
                }
            }
            else
            {
                if(isFullGrid())
                {
                    txtWinner.setText("It's a Draw");
                }
            }
        }
    }

//    private void drawLine(Button b1, Button b2)
//    {
//        Bounds bound1 = b1.localToScene(b1.getBoundsInLocal());
//        Bounds bound2 = b2.localToScene(b2.getBoundsInLocal());
//        double x1,y1,x2,y2;
//        x1 = (bound1.getMinX() + bound1.getMinX())/2;
//        y1 = (bound1.getMinY() + bound1.getMaxY())/2;
//        x2 = (bound2.getMinX() + bound2.getMinX())/2;
//        y2 = (bound2.getMinY() + bound2.getMaxY())/2;
//        Line line = new Line(x1,y1,x2,y2);
//        Platform.runLater(()->{
//            anchorpane.getChildren().add(line);
//        });
//    }

    private void computerTurn()
    {
        Random r;
        Button[] btns = {btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9};
        Button myBtn;
        do{
            r = new Random();
            int i = r.nextInt(9);
            myBtn = btns[i];
            if (isFullGrid())
            {
                break;
            }
        }while (!myBtn.getText().equals(""));

        myBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                buttonPressed = (Button) e.getSource();
                if (buttonPressed.getText().equals(""))
                {
                    buttonPressed.setText(""+player);
                    if (player == "X")
                    {
                        player="O";
                    }
                    else
                    {
                        player="X";
                    }
                }
                else
                {
                    if(isFullGrid() && !winner)
                    {
                        txtWinner.setText("It's a Draw");
                    }
                }
            }
        });
        myBtn.fire();
    }

    private  boolean isFullGrid()
    {
        if(!btn1.getText().equals("") && !btn2.getText().equals("") && !btn3.getText().equals("")
                && !btn4.getText().equals("") && !btn5.getText().equals("") && !btn6.getText().equals("")
                && !btn7.getText().equals("") && !btn8.getText().equals("") && !btn9.getText().equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void makeGridEmpty()
    {
        btn1.setText("");
        btn2.setText("");
        btn3.setText("");
        btn4.setText("");
        btn5.setText("");
        btn6.setText("");
        btn7.setText("");
        btn8.setText("");
        btn9.setText("");
    }

    private boolean checkRows()
    {
        if(btn1.getText().equals(btn2.getText()) && btn2.getText().equals(btn3.getText()) && !btn1.getText().equals(""))
        {
            //drawLine(btn1,btn3);
            if (btn1.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score +=10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin=true;
            }
            winner=true;
        }
        else if(btn4.getText().equals(btn5.getText()) && btn5.getText().equals(btn6.getText()) && !btn4.getText().equals(""))
        {
            //drawLine(btn4,btn6);
            if (btn4.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner=true;
        }
        else if(btn7.getText().equals(btn8.getText()) && btn8.getText().equals(btn9.getText()) && !btn7.getText().equals(""))
        {
            //drawLine(btn7,btn9);
            if (btn7.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner=true;
        }
        else
        {
            return false;
        }
        return winner;
    }

    private boolean checkColumns()
    {

        if(btn1.getText().equals(btn4.getText()) && btn4.getText().equals(btn7.getText()) && !btn1.getText().equals(""))
        {
            //drawLine(btn1,btn7);
            if(btn1.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner = true;
        }
        else if(btn2.getText().equals(btn5.getText()) && btn5.getText().equals(btn8.getText()) && !btn2.getText().equals(""))
        {
            //drawLine(btn2,btn8);
            if(btn2.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner = true;
        }
        else if(btn3.getText().equals(btn6.getText()) && btn6.getText().equals(btn9.getText()) && !btn3.getText().equals(""))
        {
            //drawLine(btn3,btn9);
            if(btn3.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner = true;
        }
        else
        {
            return false;
        }
        return winner;
    }

    private boolean checkDiagonal()
    {

        if(btn1.getText().equals(btn5.getText()) && btn5.getText().equals(btn9.getText()) && !btn1.getText().equals(""))
        {
            //drawLine(btn1,btn9);
            if(btn1.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner = true;
        }
        else if(btn3.getText().equals(btn5.getText()) && btn5.getText().equals(btn7.getText()) && !btn3.getText().equals(""))
        {
            //drawLine(btn3,btn7);
            if(btn3.getText().equals("X"))
            {
                txtWinner.setText("You won!");
                display = true;
                score += 10;
            }
            else
            {
                txtWinner.setText("Computer won!");
                computerWin = true;
            }
            winner = true;
        }
        else
        {
            return false;
        }
        return winner;
    }

    private void checkState()
    {
        checkColumns();
        checkRows();
        checkDiagonal();
        if(display){
            System.out.println("Synch");
            prefs.putInt("score",score);
            labScore.setText(""+ score);
        }else if(computerWin){
            System.out.println("Computer wins");
        }

    }

    public void RematchBtnHandling() throws Exception
    {
        alert = new Alert(AlertType.WARNING);
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
            Parent root = FXMLLoader.load(getClass().getResource("../layouts/GameBoard.fxml"));
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
        else
        {
            //don't do anything
        }

    }

    public void BackToChoiceLevel () throws Exception {

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
        System.exit(1);
    }

    public void BackToMain(ActionEvent actionEvent) {

    }
}