package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import javafx.util.Duration;
import sample.datamodel.GetMusic;
import sample.datamodel.Music;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Predicate;

public class Controller {
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TableView<Music> tableView;
    @FXML
    private TableColumn<Music, String> titleColumn;
    @FXML
    private TableColumn<Music, String> artistColumn;
    @FXML
    private TableColumn<Music, Double> durationColumn;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label songTitle;
    @FXML
    private Label songArtist;
    @FXML
    private ToggleButton repeatBtn;
    @FXML
    private ToggleButton shuffleBtn;
    @FXML
    private Slider volume;
    @FXML
    private TextField search;

    private Predicate<Music> searchList;
    private Service<ObservableList<Music>> service;
    private MediaPlayer mediaPlayer;
    private Media media;
    private SortedList<Music> sortedList;
    private FilteredList<Music> musicFilteredList;


        public void initialize(){
            titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
            artistColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
            durationColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
            titleColumn.setResizable(false);
            artistColumn.setResizable(false);
            durationColumn.setResizable(false);
            service = new MusicService();
            progressBar.progressProperty().bind(service.progressProperty());
            progressLabel.textProperty().bind(service.messageProperty());
            //tableView.itemsProperty().bind(service.valueProperty());
            progressBar.visibleProperty().bind(service.runningProperty());
            progressLabel.visibleProperty().bind(service.runningProperty());
            service.start();
            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    sortedList = new SortedList<>(service.getValue(), new Comparator<Music>() {
                        @Override
                        public int compare(Music o1, Music o2) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        }
                    });
                    tableView.setItems(sortedList);
                    tableView.getSelectionModel().selectFirst();
                    Music music = tableView.getSelectionModel().getSelectedItem();
                    playButton.setDisable(false);
                    pauseButton.setDisable(false);
                    prevButton.setDisable(false);
                    nextButton.setDisable(false);
                    timeSlider.setDisable(false);
                    volume.setVisible(true);
                    timeSlider.setValue(0.0);
                    search.setVisible(true);
                    setPlay(music);
                    volume.setValue(mediaPlayer.getVolume() * 100);
                }
            });
            tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            titleColumn.setCellFactory(new Callback<TableColumn<Music, String>, TableCell<Music, String>>() {
                @Override
                public TableCell<Music, String> call(TableColumn<Music, String> musicStringTableColumn) {
                    TableCell<Music, String> cell = new TableCell<Music, String>(){
                        @Override
                        protected void updateItem(String s, boolean empty) {
                            super.updateItem(s, empty);
                            if(empty){
                                setText(null);
                            } else{
                                if(s.isBlank()){
                                    setText("Unknown Title");
                                } else{
                                    setText(s);
                                }
                                setAlignment(Pos.CENTER_LEFT);
                                setPadding(new Insets(0,0,0,20));
                            }
                        }
                    };
                    return cell;
                }
            });
            artistColumn.setCellFactory(new Callback<TableColumn<Music, String>, TableCell<Music, String>>() {
                @Override
                public TableCell<Music, String> call(TableColumn<Music, String> musicStringTableColumn) {
                    TableCell<Music,String> cell = new TableCell<Music, String>(){
                        @Override
                        protected void updateItem(String s, boolean empty) {
                            super.updateItem(s, empty);
                            if(empty){
                                setText(null);
                            } else{
                                setText(s);
                                setAlignment(Pos.CENTER_LEFT);
                                setPadding(new Insets(0,0,0,25));
                            }
                        }
                    };
                    return cell;
                }
            });

            durationColumn.setCellFactory(new Callback<TableColumn<Music, Double>, TableCell<Music, Double>>() {
                @Override
                public TableCell<Music, Double> call(TableColumn<Music, Double> musicDoubleTableColumn) {
                    TableCell<Music, Double> cell = new TableCell<>(){
                        @Override
                        protected void updateItem(Double duration, boolean empty) {
                            super.updateItem(duration, empty);
                            if(empty){
                                setText(null);
                            } else {
                                String dura = String.valueOf(duration);

                                setText(dura.replace(".",":"));
                                setAlignment(Pos.CENTER);
                                //setPadding(new Insets(0,0,0,25));
                            }
                        }
                    };
                    return cell;
                }
            });
            tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getClickCount() == 2){
                        playBtnPressed();
                    }
                }
            });
            volume.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volume.getValue() / 100);
                }
            });
//            Thread getSongs = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Path dirPath = Paths.get("C:\\Users\\user\\Downloads");
//                        Files.walkFileTree(dirPath, new GetMusic());
//                    } catch (IOException e){
//                        e.printStackTrace();
//                    }
//                    System.out.println(GetMusic.getMusicList().toString());
//                }
//            });
//            getSongs.start();
        }

        @FXML
        public void playBtnPressed(){
            Music music = tableView.getSelectionModel().getSelectedItem();
            if(mediaPlayer.getStatus() == MediaPlayer.Status.READY){
                System.out.println("media is ready");
                setPlay(music);
                play();
            } else if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                System.out.println("media is playing");
                mediaPlayer.stop();
                setPlay(music);
                play();
            } else if(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED){
                play();
            } else{
                System.out.println(mediaPlayer.getStatus());
            }
        }

        public void play(){
            mediaPlayer.play();
        }

        public void setPlay(Music music){
            media = new Media(music.getFileStringURI());
            mediaPlayer = new MediaPlayer(media);
            timeSlider.setMin(0.0);
            timeSlider.setMax(music.getStopTimeSeconds());
            songTitle.setText(music.getTitle());
            songArtist.setText(music.getArtist());
            timeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if(timeSlider.isPressed()){
                        mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                    }
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldduration, Duration currentDuration) {
                       timeSlider.setValue(currentDuration.toSeconds());
                }
            });

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    if(repeatBtn.isSelected()){
                        //setPlay(music);
                        playBtnPressed();
                    } else{
                        nextEvent();
                    }

                }
            });


        }
        @FXML
        public void pause(){
            if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                mediaPlayer.pause();
            }
        }

        @FXML
        public void nextEvent(){
            if(shuffleBtn.isSelected()){
                Random rand = new Random();
                int bound = tableView.getItems().size();
                int randNum = rand.nextInt(bound);
                tableView.getSelectionModel().select(randNum);
                tableView.scrollTo(randNum);
                //Music music = tableView.getSelectionModel().getSelectedItem();
                //setPlay(music);
                playBtnPressed();
            }else{
                int index = tableView.getSelectionModel().getSelectedIndex();
                if(index >= tableView.getItems().size()-1){
                    tableView.getSelectionModel().select(0);
                    tableView.scrollTo(0);
                }else{
                    tableView.getSelectionModel().select(index + 1);
                    tableView.scrollTo(index + 1);
                }
                playBtnPressed();
            }
        }
    @FXML
    public void prevEvent(){
        if(shuffleBtn.isSelected()){
            Random rand = new Random();
            int bound = tableView.getItems().size();
            int randNum = rand.nextInt(bound);
            tableView.getSelectionModel().select(randNum);
            tableView.scrollTo(randNum);
            //Music music = tableView.getSelectionModel().getSelectedItem();
            //setPlay(music);
            playBtnPressed();
        }else{
            int index = tableView.getSelectionModel().getSelectedIndex();
            if(index == 0){
                index = tableView.getItems().size() - 1;
                tableView.getSelectionModel().select(index);
                tableView.scrollTo(index);
            }else{
                tableView.getSelectionModel().select(index - 1);
                tableView.scrollTo(index - 1);
            }
            playBtnPressed();
        }
    }

    @FXML
    public void searchMusic() {
            String txtSearch = search.getText().trim();
            if(!txtSearch.isEmpty()){
                searchList = new Predicate<Music>() {
                    @Override
                    public boolean test(Music music) {
                        return music.getTitle().toLowerCase().contains(txtSearch.toLowerCase());
                    }
                };
                musicFilteredList = new FilteredList<>(sortedList,searchList);
                tableView.setItems(musicFilteredList);
            } else {
                tableView.setItems(sortedList);
            }
    }
}
