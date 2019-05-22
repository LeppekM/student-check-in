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


/**
 * Starts the program
 */
public class StudentCheckIn extends Application  {
    private static final String logLocation = System.getProperty("user.dir");
    public final static Logger logger = LogManager.getLogger(StudentCheckIn.class.getName());

    static {
        Log4J.enableForClass(StudentCheckIn.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.setProperty("current.date", dateFormat.format(new Date()));
    }

    /**
     * Basic constructor
     */
    public StudentCheckIn(){}

    /**
     * Loads the window with the login page
     * @param primaryStage stage that the window is loaded on
     * @throws Exception general exception caught
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL myFxmlURL = ClassLoader.getSystemResource("fxml/Login.fxml");
        FXMLLoader loader = new FXMLLoader(myFxmlURL);
        Parent root = loader.load(myFxmlURL);
        Scene scene = new Scene(root, 1150, 800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(750);
        primaryStage.setTitle("Parts Inventory");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("images/msoe.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

