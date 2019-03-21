package HelperClasses;

import Database.Objects.Worker;
import InventoryController.IController;
import InventoryController.StudentCheckIn;
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.UnaryOperator;

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

    public void newStage(String fxml, Node node, Worker worker){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            node.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            // NEEDED?
            //mainMenuScene.getChildren().clear();
        }
        catch(IOException invoke){
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
        catch (NullPointerException e){
            StudentCheckIn.logger.error("Checkout page timer");
        }
    }

    public String getStudentID(List<String> list){
        StringBuilder studentID = new StringBuilder();
        for (int i =0; i<list.size(); i++){
            studentID.append(list.get(i));
        }
        return studentID.toString();
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

    public void requiredInputValidator(JFXTextField textField){
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        textField.getValidators().addAll(requiredFieldValidator);
        requiredFieldValidator.setMessage("This field is required");
        requiredFieldValidator.autosize();
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    textField.validate();
                }
            }
        });
    }

    public void acceptIntegerOnly(JFXTextField textField){
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    public void acceptIntegerOnly(JFXPasswordField passwordField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        passwordField.setTextFormatter(textFormatter);
    }

    public void errorAlert(String errorText){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error");
        errorAlert.setContentText(errorText);
        errorAlert.initStyle(StageStyle.UTILITY);

        Platform.runLater(errorAlert :: showAndWait);
    }


}

