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

        primaryStage.setTitle("Server");
        primaryStage.setMinWidth(10);
        primaryStage.setMinHeight(650);

        Scene scene = new Scene(root);
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
