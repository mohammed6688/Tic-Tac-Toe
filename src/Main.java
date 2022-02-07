import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.GameDAO;
import model.PlayerHandler;
import model.PlayerSession;
import model.Server;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application  {

    private double xOffset,yOffset;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/MainActivity.fxml"));

//        PlayerHandler playerHandler=new PlayerHandler();
//        playerHandler.createPlayerSessions(3,4,2,1,2);

        //DB.logOut(1);

//        //grab your root here
//        root.setOnMousePressed(event -> {
//            xOffset = event.getSceneX();
//            yOffset = event.getSceneY();
//        });
//
//        //move around here
//        root.setOnMouseDragged(event -> {
//            primaryStage.setX(event.getScreenX() - xOffset);
//            primaryStage.setY(event.getScreenY() - yOffset);
//        });
        primaryStage.setTitle("Server");
        primaryStage.setMinWidth(10);
        primaryStage.setMinHeight(650);
        //MainController.enableWatchGameButton(true);

        Scene scene = new Scene(root);
        //set transparent
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest((event) -> {
            System.exit(1);
        });


    }

    public static void main(String[] args) {
        launch(args);
    }
}