package InventoryController;

import Logging.Log4J;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StudentCheckIn extends Application  {
    private static final String logLocation = System.getProperty("user.dir");
    public final static Logger logger = LogManager.getLogger(StudentCheckIn.class.getName());

    static {
        Log4J.enableForClass(StudentCheckIn.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.setProperty("current.date", dateFormat.format(new Date()));
    }

    public StudentCheckIn(){}

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
//        FXMLLoader loader = new FXMLLoader(myFxmlURL);
//        Parent root = loader.load(myFxmlURL);
//        Scene scene = new Scene(root, 789, 620);
//        primaryStage.setResizable(false);
//        primaryStage.setTitle("Barcode Scanner");
//        primaryStage.setScene(scene);
//        primaryStage.getIcons().add(new Image("images/msoe.png"));
////        scene.getStylesheets().add(ControllerMenu.class.getResource("MenuStyle.css").toExternalForm());
//        primaryStage.show();
//    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL myFxmlURL = ClassLoader.getSystemResource("fxml/Login.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 789, 620);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Barcode Scanner");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("images/msoe.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

