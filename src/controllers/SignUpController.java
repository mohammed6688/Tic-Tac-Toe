package controllers;

import Client.ServerChannel;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private AnchorPane signupRoot;

    @FXML
    private PasswordField confirmpasswordTF;

    @FXML
    private TextField emailTF;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField usernameTF;

    private DialogPane dialog;
    private String response;

    @FXML
    private void backToSignIn(ActionEvent event) {
        gotoSignIn();
    }

    @FXML
    private void registerbuttonHandler(ActionEvent event) {
        if (usernameTF.getText().isEmpty() || emailTF.getText().isEmpty() || passwordTF.getText().isEmpty() || confirmpasswordTF.getText().isEmpty()) {

            makeAlertDialog("Empty Fields", "ALERT", "some fileds may be empty");
        } else if (!passwordTF.getText().equals(confirmpasswordTF.getText())) {

            makeAlertDialog("Password Conflict", "ALERT", "confirm password must match the password ");
        } else {
            String message = "SignUp " + usernameTF.getText() + " " + emailTF.getText() + " " + passwordTF.getText();
            response = ServerChannel.signUP(message);
            finishSignUp();

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signupRoot.setOpacity(0);
        toSignUpView();
    }

    private void toSignUpView() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(signupRoot);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    private void finishSignUp() {
        if (response.contains("SignedUpSuccessfully")) {
            makeAlertDialog("Congratulation!!", "New Account Registered Successfully",
                    "THANK YOU FOR TRUSTING US");
            gotoSignIn();
        } else {

            makeAlertDialog("Conflict", "ALERT", "Email and UserName already may be stored");
        }
    }

    private void gotoSignIn() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
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

    private void makeAlertDialog(String title, String HeaderText, String Content) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        dialog = a.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("../style/style.css").toString());
        dialog.getStyleClass().add("dialog");

        a.setTitle(title);
        a.setHeaderText(HeaderText);
        a.setContentText(Content);
        a.show();
    }

    private void loadSignInScene() throws IOException {
        try {
            Parent signinView;
            signinView = (AnchorPane) FXMLLoader.load(getClass().getResource("../layouts/SignInScene.fxml"));
            Scene newscene = new Scene(signinView);
            Stage curStage = (Stage) signupRoot.getScene().getWindow();
            curStage.setScene(newscene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}