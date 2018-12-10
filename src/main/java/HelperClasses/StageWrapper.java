package HelperClasses;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;

public class StageWrapper {

    public void newStage(String fxml, Node node) {
        try {
            URL myFxmlURL = ClassLoader.getSystemResource(fxml);
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            node.getScene().setRoot(loader.load(myFxmlURL));

        } catch (IOException invoke) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    public void slidingAlert(String title, String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Stage owner = new Stage(StageStyle.TRANSPARENT);
                    StackPane root = new StackPane();
                    root.setStyle("-fx-background-color: TRANSPARENT");
                    Scene scene = new Scene(root, 1, 1);
                    owner.setScene(scene);
                    owner.setWidth(1);
                    owner.setHeight(1);
                    owner.toBack();
                    owner.show();
                    Notifications.create().title(title).text(content).hideAfter(new Duration(5000)).show();
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    delay.setOnFinished(event -> owner.close());
                    delay.play();
                });
            }
        }).start();
    }


}

