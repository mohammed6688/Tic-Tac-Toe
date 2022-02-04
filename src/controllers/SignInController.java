package controllers;

import Client.ServerChannel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    private AnchorPane signinRoot;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Button signIn;
    private double xOffset = 0;
    private double yOffset = 0;
    public static String response;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signinRoot.setOpacity(0);
        toSignInView();

    }
    @FXML
    private void toSignUpHandler (ActionEvent event){
        gotoSignUp();
    }
    @FXML
    private void toSignInHandler (ActionEvent event) throws IOException ,Exception {
        String message="SignIn "+email.getText()+" "+password.getText();
       response= ServerChannel.signIn(message);
        System.out.println(response);
        System.out.println(response.contains("Logged in successfully "));

        if(response.contains("LoggedInSuccessfully"))
      {
          getOnlinePlayersLayout();

      }
      else if(response=="CantUpdateStatus")
      {

      }
      else if(response=="EmailOrPasswordIsIncorrect")
      {

      }
      else if(response=="ConnectionIssue,PleaseTryAgainLater")
      {

      }
    }
private void getOnlinePlayersLayout() throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));
    Stage window = (Stage)signIn.getScene().getWindow();
    //grab your root here
    root.setOnMousePressed( event1 -> {
        xOffset = event1.getSceneX();
        yOffset = event1.getSceneY();
    });

    //move around here
    root.setOnMouseDragged(event1 -> {
        window.setX(event1.getScreenX() - xOffset);
        window.setY(event1.getScreenY() - yOffset);
    });
    window.setTitle("Multi-Players");
    window.setMinWidth(1000);
    window.setMinHeight(600);

    Scene scene = new Scene(root);
    //set transparent
    scene.setFill(Color.TRANSPARENT);
    window.setScene(scene);
    window.show();

    window.setOnCloseRequest((event1) -> {
        System.exit(1);
    });
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
            signupView = (AnchorPane) FXMLLoader.load(getClass().getResource("../layouts/hello-view.fxml"));
            Scene newscene = new Scene(signupView);
            Stage curStage = (Stage) signinRoot.getScene().getWindow();
            curStage.setScene(newscene);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}