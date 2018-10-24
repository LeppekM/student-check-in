package gui;

import database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StudentCheckin extends Application  {
    public StudentCheckin(){

    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Barcode Scanner");
        primaryStage.setScene(scene);
        primaryStage.show();

//        Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
//
//        Scene scene = new Scene(root, 800, 600);
//
//        primaryStage.setTitle("Barcode Scanner");
//        primaryStage.setScene(scene);
//        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

