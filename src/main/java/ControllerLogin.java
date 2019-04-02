import Database.Database;
import HelperClasses.ImageViewPane;
import Database.ObjectClasses.Worker;
import InventoryController.ControllerMenu;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {

    @FXML
    private StackPane loginScene;

    private ImageViewPane msoeBackgroundImage;

    @FXML
    private JFXTextField emailInputLoginPage;

    @FXML
    private JFXPasswordField passwordInputLoginPage;

    @FXML
    private JFXButton loginButtonLoginPage;

    @FXML
    private Label invalidLoginCredentialsError;

    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        Image image = new Image("images/msoeBackgroundImage.png");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        msoeBackgroundImage = new ImageViewPane(imageView);
        msoeBackgroundImage.setPrefWidth(591);
        msoeBackgroundImage.setPrefHeight(789);
        msoeBackgroundImage.setOpacity(0.55);
        loginScene.getChildren().add(msoeBackgroundImage);
        msoeBackgroundImage.toBack();
        emailInputLoginPage.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        passwordInputLoginPage.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        loginButtonLoginPage.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
    }

    public void login() {
        try {
            Worker worker = findWorker(emailInputLoginPage.getText());
            if (worker != null) {
                if (worker.getPass().equals(passwordInputLoginPage.getText())) {
                    FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/Menu.fxml"));
                    Pane mainMenuPane = loader.load();
                    ControllerMenu controller = loader.getController();
                    controller.initWorker(worker);
                    loginScene.getScene().setRoot(mainMenuPane);
                } else {
                    invalidLoginCredentialsError.setVisible(true);
                }
            } else {
                invalidLoginCredentialsError.setVisible(true);
            }
        }
        catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    private Worker findWorker(String email) {
        Worker worker = database.getWorker(email);
        return worker;
    }

}