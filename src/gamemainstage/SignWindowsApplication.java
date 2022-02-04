package gamemainstage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignWindowsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../layouts/SignInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 760, 550);
        stage.setTitle("Tic-Tac-Toc");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);}
}