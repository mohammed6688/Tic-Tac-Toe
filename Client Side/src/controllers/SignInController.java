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
import java.util.prefs.Preferences;

public class SignInController implements Initializable {
    public Button BackBtn;
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
    public String response;
    public static Player currentPlayer;
    Preferences prefs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signinRoot.setOpacity(0);
        toSignInView();

        prefs = Preferences.userNodeForPackage(SignInController.class);

//        boolean signedIn = prefs.getBoolean("signedIn", false);
//        if (signedIn){
//            String email = prefs.get("email", "");
//            String password = prefs.get("password", "");
//            String message = "SignIn " + email + " " + password;
//            response = ServerChannel.signIn(message);
//        }
//
//        try {
//            SignIn();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void toSignUpHandler(ActionEvent event) {
        gotoSignUp();
    }

    @FXML
    private void toSignInHandler(ActionEvent event) throws IOException, Exception {
        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            makeAlertDialog("Empty Fields", "ALERT", "some fileds may be empty");

        } else {
            prefs.putBoolean("signedIn",true);
            prefs.put("email",email.getText());
            prefs.put("password",password.getText());

            String message = "SignIn " + email.getText() + " " + password.getText();
            response = ServerChannel.signIn(message);

            SignIn();
        }

    }
    private void SignIn() throws IOException {
        System.out.println(response);
        System.out.println(response.contains("Logged in successfully "));

        if (response.contains("LoggedInSuccessfully")) {
            createPlayerObj();
            getOnlinePlayersLayout();
        } else if (response.contains("already signed in")) {
            makeAlertDialog("WARNING", "ALERT", "THIS ACCOUNT ALREADY SIGNED IN FROM ANOTHER DEVICE");
        } else {
            makeAlertDialog("WARNING", "WRONG CREDENTIAL", "Email or Password may be wrong ");
        }
    }

    private void getOnlinePlayersLayout() throws IOException {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(150));
        transition.setNode(signinRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("../layouts/TwoPlayers.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage window = (Stage) signIn.getScene().getWindow();
            //grab your root here
            root.setOnMousePressed(event1 -> {
                xOffset = event1.getSceneX();
                yOffset = event1.getSceneY();
            });

            //move around here
            root.setOnMouseDragged(event1 -> {
                window.setX(event1.getScreenX() - xOffset);
                window.setY(event1.getScreenY() - yOffset);
            });
            window.setTitle("Multi-Players");
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            window.setScene(scene);
            window.show();

            window.setOnCloseRequest((event1) -> {
                String message = "logout " + SignInController.currentPlayer.getId();
                ServerChannel.logOut(message);
                System.exit(1);
            });
        });
        transition.play();
    }

    private void toSignInView() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(signinRoot);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();

    }

    @FXML
    private void gotoSignUp() {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(signinRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(event -> {
            try {
                loadSignUpScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        transition.play();
    }

    private void loadSignUpScene() throws IOException {
        try {
            Parent signupView;
            signupView = (AnchorPane) FXMLLoader.load(getClass().getResource("../layouts/SignupScene.fxml"));
            Scene newscene = new Scene(signupView);
            Stage curStage = (Stage) signinRoot.getScene().getWindow();
            curStage.setScene(newscene);
            curStage.setOnCloseRequest((event) -> {
                ServerChannel.closeConnection();
                System.exit(1);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    private Player createPlayerObj() {
        StringTokenizer token = new StringTokenizer(response, " ");
        token.nextToken();
        currentPlayer = new Player(Integer.parseInt(token.nextToken()),
                token.nextToken(),
                token.nextToken()
                , token.nextToken(),
                Boolean.parseBoolean(token.nextToken()),
                Boolean.parseBoolean(token.nextToken()),
                Integer.parseInt(token.nextToken()));
        System.out.println(currentPlayer.getId());
        return currentPlayer;
    }

    public void BackToMain(ActionEvent actionEvent) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(signinRoot);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("../layouts/GameMainFXML.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage window = (Stage) BackBtn.getScene().getWindow();
                window.setTitle("Home");
                Scene scene = new Scene(root);
                scene.setFill(Color.TRANSPARENT);
                window.setScene(scene);
                window.show();
            }
        });
        transition.play();
    }
}