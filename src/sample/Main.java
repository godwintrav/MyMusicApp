package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.datamodel.GetMusic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("GTrav Music");
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();

    }


    public static void main(String[] args) {
        Thread getSongs = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Path dirPath = Paths.get("C:\\Users\\user\\Music");
                    Files.walkFileTree(dirPath, new GetMusic());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        getSongs.start();
        launch(args);

    }
}
