module com.example.signwindows {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.signwindows to javafx.fxml;
    exports com.example.signwindows;
}