package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sun.rmi.runtime.Log;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }


    public void openFile(javafx.event.ActionEvent actionEvent) {
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

    public void play(ActionEvent actionEvent) {
            pathSaveImage = txt_path_image.getText().toString();
            createFile(pathSaveImage);
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(0));
            mediaPlayer.play();
            mediaPlayer.currentTimeProperty().addListener(observable -> {
                System.out.println(mediaPlayer.getCurrentTime());
                takeSnapShot();
            });

        } else {
            if (fileCurrent.size() > 0) {
                media = new Media(new File(fileCurrent.get(0)).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.seek(new Duration(0));
                mediaPlayer.currentTimeProperty().addListener(observable -> {
                    System.out.println(mediaPlayer.getCurrentTime());
                    takeSnapShot();
                });
                mv.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Bạn Phải chọn video");
                alert.show();


            }
        }

    }

    public void takeImage(ActionEvent actionEvent) {

        takeSnapShot();

    }

    public void pause(ActionEvent actionEvent) {
        mediaPlayer.pause();
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void takeSnapShot() {
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

            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png",
                    new File(pathSaveImage + "/" + time + ".png"));
            listView.getItems().add(time + ".png");
            imageCurrent.add(pathSaveImage + "/" + time + ".png");
        } catch (Exception s) {
            System.out.println(s);
        }


    }

    public void onclick(MouseEvent mouseEvent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (fileCurrent.size() > 0) {
            int indexCurrent = listFile.getSelectionModel().getSelectedIndex();
            if(indexCurrent<0){
                return;
            }
            media = new Media(new File(fileCurrent.get(indexCurrent)).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mv.setMediaPlayer(mediaPlayer);
        }

    }

    public void showImage(MouseEvent mouseEvent) {


        if (imageCurrent.size() > 0) {
            int indexCurrent = listView.getSelectionModel().getSelectedIndex();
            Image image = new Image(new File(imageCurrent.get(indexCurrent)).toURI().toString());
            img.setImage(image);
        }

    }

    public void choosePathImage() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File file = fileChooser.showDialog(null);
        if(file!=null){
            pathSaveImage = file.getAbsolutePath();
            txt_path_image.setText(pathSaveImage);
        }else {
            JOptionPane.showMessageDialog(null,"Bạn Chưa chọn thư mục lưu ảnh");
        }

    }

    public void createFile(String path) {
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
