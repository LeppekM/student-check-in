import Database.Objects.Worker;
import InventoryController.ControllerMenu;
import javafx.embed.swing.SwingFXUtils;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("images/msoeBackgroundImage.png");
        this.msoeBackgroundImage.setImage(image);
    }

    public void login() {
        try {
            Worker worker = loginWorker(usernameInputLoginPage.getText(), passwordInputLoginPage.getText());
            if (worker != null) {
                //Controller controller = new Controller(worker, loginScene);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
                Pane mainMenuPane = loader.load();
                ControllerMenu controller = loader.getController();
                controller.initWorker(worker);

                loginScene.getScene().setRoot(mainMenuPane);
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

    private Worker loginWorker(String email, String password) {
        Worker worker = null;

        return worker;
    }

}