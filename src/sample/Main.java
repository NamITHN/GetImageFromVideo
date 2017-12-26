package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("GetImageFromVideo");
        primaryStage.setScene(new Scene(root, 500, 550));
        primaryStage.show();
        javafx.scene.control.ListView listView=new javafx.scene.control.ListView();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
