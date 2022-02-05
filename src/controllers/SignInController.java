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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class SignInController implements Initializable {
    @FXML
    private AnchorPane signinRoot;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Button signIn;
    private DialogPane dialog;
    private double xOffset = 0;
    private double yOffset = 0;
    public  String response;
    public static Player currentPlayer;
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
        if(email.getText().isEmpty() || password.getText().isEmpty())
        {
            makeAlertDialog("Empty Fields","ALERT","some fileds may be empty");

        }else{


        //String message="SignIn "+email.getText()+" "+password.getText();
        String message="SignIn "+"mohamed@gmail.com"+" "+"123";
        response= ServerChannel.signIn(message);
        System.out.println(response);
        System.out.println(response.contains("Logged in successfully "));

        if(response.contains("LoggedInSuccessfully")) {
            createPlayerObj();
          getOnlinePlayersLayout();

        }
        else if(response.contains("already signed in")) {

            makeAlertDialog("WARNING","ALERT","THIS ACCOUNT ALREADY SIGNED IN FROM ANOTHER DEVICE");

        }
        else {


            makeAlertDialog("WARNING","WRONG CREDENTIAL","Email or Password may be wrong ");


        }
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
        String message="logout "+SignInController.currentPlayer.getId();
        ServerChannel.logOut(message);
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
            curStage.setOnCloseRequest((event) -> {
                ServerChannel.closeConnection();
                System.exit(1);
            });
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void makeAlertDialog(String title ,String HeaderText ,String Content)
    {
        Alert a = new Alert(Alert.AlertType.WARNING);
        dialog=a.getDialogPane();
        dialog.getStylesheets().add(getClass().getResource("../style/style.css").toString());
        dialog.getStyleClass().add("dialog");

        a.setTitle(title);
        a.setHeaderText(HeaderText);
        a.setContentText(Content);
        a.show();
    }
    private Player createPlayerObj()
    {
        StringTokenizer token=new StringTokenizer(response," ");
        token.nextToken();
        currentPlayer =new Player(Integer.parseInt(token.nextToken()),
                token.nextToken(),
                token.nextToken()
                ,token.nextToken(),
                Boolean.parseBoolean(token.nextToken()),
                Boolean.parseBoolean(token.nextToken()));
        System.out.println(currentPlayer.getId());
        return currentPlayer;
    }
}