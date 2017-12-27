package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    private MediaView mv;
    @FXML
    private ListView listView;
    @FXML
    private ListView listFile;
    @FXML
    private ImageView img;
    @FXML
    private TextField txt_path_image;

    private List<String> fileCurrent = new ArrayList<>();
    private List<String> imageCurrent = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private Media media;
    private String pathSaveImage = null;
    private boolean isPlaying = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        pathSaveImage = txt_path_image.getText();
        createFile(pathSaveImage);
    }


    public void openFile() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select a File (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().addAll(filter);
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            for (File file : files) {
                listFile.getItems().add(file.getName());
                fileCurrent.add(file.getAbsolutePath());
            }

        }
    }

    public void play() {
        if (isPlaying) {
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.setOnPlaying(new Runnable() {
                @Override
                public void run() {
                    isPlaying = true;
                }
            });
            mediaPlayer.setOnPaused(new Runnable() {
                @Override
                public void run() {
                    isPlaying = false;
                }
            });
            mediaPlayer.setOnStopped(new Runnable() {
                @Override
                public void run() {
                    isPlaying = false;
                }
            });
            if (mediaPlayer.getCurrentTime().toMillis() == mediaPlayer.getTotalDuration().toMillis())
                mediaPlayer.seek(new Duration(0));
            mediaPlayer.play();
            //mediaPlayer.setRate(0.5);


            mediaPlayer.currentTimeProperty().addListener(observable -> {
                System.out.println(mediaPlayer.getCurrentTime());
                Platform.runLater(this::takeSnapShot);
            });


        } else {
            if (fileCurrent.size() > 0) {
                media = new Media(new File(fileCurrent.get(0)).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnPlaying(new Runnable() {
                    @Override
                    public void run() {
                        isPlaying = true;
                    }
                });
                mediaPlayer.setOnPaused(new Runnable() {
                    @Override
                    public void run() {
                        isPlaying = false;
                    }
                });
                mediaPlayer.setOnStopped(new Runnable() {
                    @Override
                    public void run() {
                        isPlaying = false;
                    }
                });
                if (mediaPlayer.getCurrentTime().toMillis() == mediaPlayer.getTotalDuration().toMillis())
                    mediaPlayer.seek(new Duration(0));
                mv.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();

                mediaPlayer.currentTimeProperty().addListener(observable -> {
                    System.out.println(mediaPlayer.getCurrentTime());
                    Platform.runLater(this::takeSnapShot);
                });


            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Bạn Phải chọn video");
                alert.show();


            }
        }

    }


    private void takeSnapShot() {
        Calendar calendar = Calendar.getInstance();
        Long time = calendar.getTimeInMillis();
        int width = mediaPlayer.getMedia().getWidth();
        int height = mediaPlayer.getMedia().getHeight();
        WritableImage wim = new WritableImage(width, height);
        MediaView mv = new MediaView();
        mv.setFitWidth(width);
        mv.setFitHeight(height);
        mv.setMediaPlayer(mediaPlayer);
        mv.snapshot(null, wim);
        try {
            new Thread(() ->
            {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png",
                            new File(pathSaveImage + "/" + time + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            listView.getItems().add(time + ".png");
            imageCurrent.add(pathSaveImage + "/" + time + ".png");
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    public void onclick() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //mediaPlayer.dispose();
        }
        if (fileCurrent.size() > 0) {
            int indexCurrent = listFile.getSelectionModel().getSelectedIndex();
            if (indexCurrent < 0) {
                return;
            }
            media = new Media(new File(fileCurrent.get(indexCurrent)).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
          //  if (mv.getMediaPlayer() == null)
            try {
                mv.setMediaPlayer(mediaPlayer);
            }catch (Exception e){

            }

        }

    }

    public void showImage() {


        if (imageCurrent.size() > 0) {
            int indexCurrent = listView.getSelectionModel().getSelectedIndex();
            Image image = new Image(new File(imageCurrent.get(indexCurrent)).toURI().toString());
            img.setImage(image);
        }

    }

    public void choosePathImage() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File file = fileChooser.showDialog(null);
        if (file != null) {
            pathSaveImage = file.getAbsolutePath();
            txt_path_image.setText(pathSaveImage);
        } else {
            JOptionPane.showMessageDialog(null, "Bạn Chưa chọn thư mục lưu ảnh");
        }

    }

    private void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
