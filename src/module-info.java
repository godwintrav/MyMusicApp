module MyMusicApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.xml;
    requires javafx.base;
    requires javafx.media;
    requires javafx.graphics;

    opens sample;
    opens sample.datamodel;
}