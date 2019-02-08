package InventoryController;

import Logging.LogEntry;
import Logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class StudentCheckIn extends Application  {
    private static final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();

    public StudentCheckIn(){}

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 789, 620);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Barcode Scanner");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("images/msoe.png"));
//        scene.getStylesheets().add(ControllerMenu.class.getResource("MenuStyle.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        Logger logger = new Logger(queue);
        logger.start();
    }

    /**
     * This method takes a LogEntry object and offers it to the Logger thread queue, to be printed.
     * @param data - LogEntry object to be sent to the Logger and printed to the log
     * @author Matt K
     */
    public static void sendToLogger(String data, String origin){
        LogEntry entry = new LogEntry(LocalDateTime.now(), data, origin);
        queue.offer(entry);
    }
}

