package com.example.signwindows;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    private AnchorPane signinRoot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signinRoot.setOpacity(0);
        toSignInView();
    }
    @FXML
    private void toSignUpHandler (ActionEvent event){
        gotoSignUp();
    }
    private void toSignInView() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(signinRoot);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    @FXML
    private void gotoSignUp (){
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(signinRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    loadSignUpScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        transition.play();
    }

    private void loadSignUpScene() throws IOException {
        try {
            Parent signupView;
            signupView = (AnchorPane) FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            Scene newscene = new Scene(signupView);
            Stage curStage = (Stage) signinRoot.getScene().getWindow();
            curStage.setScene(newscene);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}