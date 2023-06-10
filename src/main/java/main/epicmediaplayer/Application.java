package main.epicmediaplayer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        AnchorPane root = fxmlLoader.load();

        // Load the styles.css file
        root.getStylesheets().add(Application.class.getResource("styles.css").toExternalForm());

        Scene scene = new Scene(root, 700, 525);
        stage.setTitle("EPIC MEDIA PLAYER!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
