package InventoryController;

import Database.ObjectClasses.Worker;
import HelperClasses.ImageViewPane;
import HelperClasses.StageUtils;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    private final StageUtils stageUtils = StageUtils.getInstance();

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
        msoeBackgroundImage.setOpacity(0.68);
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
        stageUtils.newStage("/fxml/TableScreen.fxml", mainMenuScene, worker);
    }

    @FXML
    private void openMangeStudents() {
        stageUtils.newStage("/fxml/manageStudents.fxml", mainMenuScene, worker);
    }

    @FXML
    public void openCheckItemsPage(){
        stageUtils.newStage("/fxml/CheckOutPage.fxml", mainMenuScene, worker);
    }

    private void openManageWorkers() {
        if (worker != null) {
            if (worker.isAdmin() || worker.canEditWorkers()) {
                stageUtils.newStage("/fxml/manageWorkers.fxml", mainMenuScene, worker);
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

}