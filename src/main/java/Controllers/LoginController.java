package Controllers;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.ImageViewPane;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This controller is in charge of the login screen, shown on program start
 */
public class LoginController implements Initializable {

    @FXML
    private StackPane loginScene;

    @FXML
    private JFXTextField emailInputLoginPage, rfid;

    @FXML
    private JFXPasswordField passwordInputLoginPage;

    @FXML
    private JFXButton loginButtonLoginPage, switchButton;

    @FXML
    private Label invalidLoginCredentialsError, rfidLabel, passLabel, emailLabel;

    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rfidFilter(rfid);
        database = Database.getInstance();
        setupBackgroundImage(loginScene);
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
        rfid.setOnKeyReleased(event ->{
            if (event.getCode() == KeyCode.ENTER){
                login();
            }
        });
    }

    /**
     * Sets up the MSOE logo as the background image of the passed StackPane
     * @param loginScene the scene that the background is being applied to
     */
    public static void setupBackgroundImage(StackPane loginScene) {
        Image image = new Image("images/msoeBackgroundImage.png");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        ImageViewPane msoeBackgroundImage = new ImageViewPane(imageView);
        msoeBackgroundImage.setPrefWidth(591);
        msoeBackgroundImage.setPrefHeight(789);
        msoeBackgroundImage.setOpacity(0.68);
        loginScene.getChildren().add(msoeBackgroundImage);
        msoeBackgroundImage.toBack();
    }

    private void rfidFilter(JFXTextField textField) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String id = textField.getText();
                if (textField.getText().contains("rfid:")) {
                    textField.setText(id.substring(5));
                }
            }
        });

    }

    public void login() {
        if (switchButton.getText().equals("Login Using RFID")) {
            try {
                Worker worker = findWorker(emailInputLoginPage.getText());
                if (worker != null) {
                    if (worker.getPass().equals(passwordInputLoginPage.getText())) {
                        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/Menu.fxml"));
                        Pane mainMenuPane = loader.load();
                        MenuController controller = loader.getController();
                        controller.initWorker(worker);
                        loginScene.getScene().setRoot(mainMenuPane);
                    } else {
                        invalidLoginCredentialsError.setVisible(true);
                    }
                } else {
                    invalidLoginCredentialsError.setVisible(true);
                }
            } catch (IOException invoke) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
                alert.showAndWait();
                invoke.printStackTrace();
            }
        } else {
            try {
                Worker worker = findWorkerByID(Integer.parseInt(rfid.getText()));
                if (worker != null) {
                    FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/Menu.fxml"));
                    Pane mainMenu = loader.load();
                    MenuController menuController = loader.getController();
                    menuController.initWorker(worker);
                    loginScene.getScene().setRoot(mainMenu);
                } else {
                    invalidLoginCredentialsError.setVisible(true);
                }
            } catch (IOException invoke) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
                alert.showAndWait();
                invoke.printStackTrace();
            }
        }
    }

    private Worker findWorker(String email) {
        return database.getWorker(email);
    }

    private Worker findWorkerByID(int rfid) {
        return database.getWorker(rfid);
    }

    /**
     * Switch the screen to the other method of logging in, from email/password to RFID
     */
    public void switchToOther() {
        if (switchButton.getText().equals("Login Using Email")) {
            emailLabel.setVisible(true);
            passLabel.setVisible(true);
            emailInputLoginPage.setVisible(true);
            passwordInputLoginPage.setVisible(true);
            rfid.setVisible(false);
            rfidLabel.setVisible(false);
            switchButton.setText("Login Using RFID");
            switchButton.setText("Login Using RFID");
        } else if (switchButton.getText().equals("Login Using RFID")) {
            invalidLoginCredentialsError.setVisible(false);
            rfid.setVisible(true);
            rfidLabel.setVisible(true);
            emailLabel.setVisible(false);
            passLabel.setVisible(false);
            emailInputLoginPage.setVisible(false);
            passwordInputLoginPage.setVisible(false);
            switchButton.setText("Login Using Email");
        }

    }
}