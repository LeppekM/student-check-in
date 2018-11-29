package InventoryController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        Scene scene = new Scene(root, 789, 620);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Barcode Scanner");
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(ControllerMenu.class.getResource("MenuStyle.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

