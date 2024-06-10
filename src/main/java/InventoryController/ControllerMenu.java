package InventoryController;

import Database.ObjectClasses.Worker;
import HelperClasses.ImageViewPane;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Acts as the controller for the main menu.
 */
public class ControllerMenu implements IController, Initializable {

    @FXML
    private VBox mainMenuScene;

    @FXML
    private StackPane pane;

    @FXML
    private JFXButton manageWorkers;

    private Worker worker;

    /**
     * Initializes the buttons and sets the MSOE logo as the background image
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.worker = null;
        manageWorkers.setDisable(true);
        manageWorkers.setText("Manage\nWorkers");
        manageWorkers.setOnAction(event -> openManageWorkers());
        Image image = new Image("images/msoeBackgroundImage.png");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        ImageViewPane msoeBackgroundImage = new ImageViewPane(imageView);
        msoeBackgroundImage.setPrefWidth(591);
        msoeBackgroundImage.setPrefHeight(789);
        msoeBackgroundImage.setOpacity(0.55);
        pane.getChildren().add(msoeBackgroundImage);
        msoeBackgroundImage.toBack();
    }

    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
            if (this.worker != null && (this.worker.isAdmin() || this.worker.canEditWorkers())) {
                manageWorkers.setDisable(false);
            }
        }
    }

    @FXML
    private void openInventory(){
        newStage("/fxml/InventoryPage.fxml");
    }

    @FXML
    private void openMangeStudents() {
        newStage("/fxml/manageStudents.fxml");
    }

    @FXML
    public void openCheckItemsPage(){
        newStage("/fxml/CheckOutPage.fxml");
    }

    private void openManageWorkers() {
        if (worker != null) {
            if (worker.isAdmin() || worker.canEditWorkers()) {
                newStage("/fxml/manageWorkers.fxml");
            }
        }
    }

    public void logout() {
        worker = null;
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("fxml/Login.fxml"));
            Pane loginPane = loader.load();
            mainMenuScene.getScene().setRoot(loginPane);
        } catch(IOException invoke){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

    public void newStage(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            mainMenuScene.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
        }
        catch(IOException invoke){
            StudentCheckIn.logger.error("No valid stage was found to load. This could likely be because of a database disconnect.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
            invoke.printStackTrace();
        }
    }

}