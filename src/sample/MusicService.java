package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sample.datamodel.GetMusic;
import sample.datamodel.Music;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Map;

public class MusicService extends Service<ObservableList<Music>> {
    private Music music;
    private String title;
    private String artist;
    private Double duration;
    private String fileStringURI;
    private Double stopTimeSeconds;
    private DecimalFormat df = new DecimalFormat("#.00");
    private int i;

    @Override
    protected Task<ObservableList<Music>> createTask() {
            return new Task<ObservableList<Music>>() {
                @Override
                protected ObservableList<Music> call() throws Exception {
                    ObservableList<Music> musics = FXCollections.observableArrayList();
                    for (i = 0; i < GetMusic.getMusicList().size(); i++){
                        File currentFile = new File(GetMusic.getMusicList().get(i).toString());
                        URI uri = currentFile.toURI();
                        Media media = new Media(uri.toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setOnReady(new Runnable() {
                            @Override
                            public void run() {
//                            for (Map.Entry<String,Object> entry : media.getMetadata().entrySet()){
//                                if(entry.getKey().toLowerCase().contains("title")){
//                                    title = entry.getValue().toString();
//                                }
//                                //System.out.println(entry.getKey() + " =" + entry.getValue());
//                            }
                                String temptitle = "";
                                String tempArtist = "";
                                if( media.getMetadata().get("title") != null){
                                    temptitle = media.getMetadata().get("title").toString().trim();
                                }

                                if(temptitle.isBlank() || temptitle.isEmpty()){
                                    title = currentFile.toPath().getFileName().toString();
                                } else{
                                    title = temptitle;
                                }
//                                if(title.isEmpty()){
////                                    for (Map.Entry<String,Object> entry : media.getMetadata().entrySet()){
////                                        System.out.println(entry.getKey() + " =" + entry.getValue());
////                                    }
//                                }

                                if(media.getMetadata().get("artist") != null){
                                    tempArtist = media.getMetadata().get("artist").toString();
                                    artist = media.getMetadata().get("artist").toString();
                                } else if(media.getMetadata().get("album artist") != null){
                                    tempArtist = media.getMetadata().get("album artist").toString();
                                }

                                if(tempArtist.isEmpty() || tempArtist.isBlank()){
                                    artist = "Unknown Artist";
                                } else{
                                    artist = tempArtist;
                                }
                                fileStringURI = uri.toString();

                                duration = Double.parseDouble(df.format(media.getDuration().toMinutes()));
                                stopTimeSeconds = media.getDuration().toSeconds();
                                music = new Music(title,artist,duration,fileStringURI,stopTimeSeconds);
                                musics.add(music);
                            }
                        });
                        updateMessage("Updating Library......");
                        updateProgress(i+1, GetMusic.getMusicList().size());
                    }
                    return musics;
                }
            };
    }
}
