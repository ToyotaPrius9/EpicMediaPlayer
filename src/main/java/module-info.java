module main.epicmediaplayer {
    requires javafx.controls;

    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens main.epicmediaplayer to javafx.fxml;
    exports main.epicmediaplayer;
}