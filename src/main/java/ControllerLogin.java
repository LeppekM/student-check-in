import Database.Database;
import Database.Objects.Worker;
import InventoryController.ControllerMenu;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {

    @FXML
    private StackPane loginScene;

    @FXML
    private ImageView msoeBackgroundImage;

    @FXML
    private TextField usernameInputLoginPage, passwordInputLoginPage;

    @FXML
    private Label invalidLoginCredentialsError;

    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        Image image = new Image("images/msoeBackgroundImage.png");
        this.msoeBackgroundImage.setImage(image);
    }

    public void login() {
        try {
            Worker worker = findWorker(usernameInputLoginPage.getText());
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