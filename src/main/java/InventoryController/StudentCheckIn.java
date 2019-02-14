package InventoryController;

import Logging.Log4J;
import Logging.LogEntry;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class StudentCheckIn extends Application  {
    private static final String logLocation = System.getProperty("user.dir");
    final static Logger logger = LogManager.getLogger(StudentCheckIn.class.getName());

    static {
        Log4J.enableForClass(StudentCheckIn.class);
    }

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
//        setupLogging();
        logger.debug("This is a test debug message");
        launch(args);
    }

    public static void setupLogging(){
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String logName = date.format(formatter);

        //Check if the log already exists, if not make a new one
        File f = new File(logName);
        int num = 1;
        while(f.exists() && !f.isDirectory() && num<4) {
            logName = logName + num;
            num++;
            f = new File(logName);
        }

        String logOutput = logLocation + "/" + logName + ".log";

        try {
            PrintStream out = new PrintStream(new FileOutputStream(logOutput));
            System.setOut(out);
        }catch(FileNotFoundException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Unable to write to the log. Errors will not be reported.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}

