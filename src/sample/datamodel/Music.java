package sample.datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Music {
    private SimpleStringProperty title = new SimpleStringProperty("");
    private SimpleStringProperty artist = new SimpleStringProperty("");
    private SimpleDoubleProperty duration = new SimpleDoubleProperty(0);
    private SimpleStringProperty fileStringURI = new SimpleStringProperty("");
    private SimpleDoubleProperty stopTimeSeconds = new SimpleDoubleProperty(0);
    private File fileName;
    private Media media;
    private MediaPlayer mediaPlayer;
    private URI uri;

    public Music(String title, String artist, Double duration, String fileStringURI, Double stopTimeSeconds) {
        this.title.set(title);
        this.artist.set(artist);
        this.duration.set(duration);
        this.fileStringURI.set(fileStringURI);
        this.stopTimeSeconds.set(stopTimeSeconds);
    }

//    public Music(Path file) {
//        fileName = new File(file.toString());
//        uri = fileName.toURI();
//        fileStringURI.set(uri.toString());
//        media = new Media(fileStringURI.get());
//        mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setOnReady(new Runnable() {
//            @Override
//            public void run() {
//                if(media.getMetadata().get("title") == null){
//                    title.set(file.getFileName().toString());
//                } else{
//                    title.set(media.getMetadata().get("title").toString());
//                }
//
//                if(media.getMetadata().get("artist") != null){
//                    artist.set(media.getMetadata().get("artist").toString());
//                } else if(media.getMetadata().get("album artist") != null){
//                    artist.set(media.getMetadata().get("album artist").toString());
//                } else{
//                    artist.set("Unknown Artist");
//                }
//                System.out.println(artist.get());
//                duration.set(media.getDuration().toMinutes());
//                //remeber to check how to round off to 2 decimal places
//                stopTimeSeconds.set(media.getDuration().toSeconds());
//
//
//
//            }
//        });
//
//
//    }


    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public String getArtist() {
        return artist.get();
    }

    public SimpleStringProperty artistProperty() {
        return artist;
    }

    public double getDuration() {
        return duration.get();
    }

    public SimpleDoubleProperty durationProperty() {
        return duration;
    }

    public String getFileStringURI() {
        return fileStringURI.get();
    }

    public SimpleStringProperty fileStringURIProperty() {
        return fileStringURI;
    }

    public double getStopTimeSeconds() {
        return stopTimeSeconds.get();
    }

    public SimpleDoubleProperty stopTimeSecondsProperty() {
        return stopTimeSeconds;
    }

    @Override
    public String toString() {
        return "\n title :" + getTitle() +
                "\n artist : " + getArtist() +
                "\n duration : " + getDuration() +
                "\n File String : " + getFileStringURI() +
                " \n stopTime : " + getStopTimeSeconds();
    }
}
