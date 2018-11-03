package InventoryController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class StudentCheckIn extends Application  {
    public StudentCheckIn(){

    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL myFxmlURL = ClassLoader.getSystemResource("Menu.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 800, 620);
        primaryStage.setTitle("Barcode Scanner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

