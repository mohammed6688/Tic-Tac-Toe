package com.example.signwindows;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private AnchorPane signupRoot ;

    @FXML
    private TextField confirmpasswordTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField passwordTF;

    @FXML
    private TextField usernameTF;


    @FXML
    private void backToSignIn (ActionEvent event){
        gotoSignIn();
    }

    @FXML
    void registerbuttonHandler(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signupRoot.setOpacity(0);
        toSignUpView();
    }

    private void toSignUpView() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(signupRoot);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    private void gotoSignIn(){
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(signupRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    loadSignInScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        transition.play();
    }

    private void loadSignInScene() throws IOException {
        try {
            Parent signinView;
            signinView = (AnchorPane) FXMLLoader.load(getClass().getResource("SignInScene.fxml"));
            Scene newscene = new Scene(signinView);
            Stage curStage = (Stage) signupRoot.getScene().getWindow();
            curStage.setScene(newscene);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}