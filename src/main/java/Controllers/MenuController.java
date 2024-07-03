package Controllers;

import Database.ObjectClasses.Worker;
import HelperClasses.StageUtils;
import Tables.TableScreen;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Acts as the controller for the main menu, the hub for directing the user between the following main screens:
 * Login (logs the current worker out)
 * Checkout/in
 * Inventory
 * Manage Students
 * Manage Employees (if the user is a worker with permissions)
 */
public class MenuController implements IController, Initializable {

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
        LoginController.setupBackgroundImage(pane);
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
        stageUtils.newStage("/fxml/TableScreen.fxml", mainMenuScene, worker, TableScreen.COMPLETE_INVENTORY);
    }

    @FXML
    private void openMangeStudents() {
        stageUtils.newStage("/fxml/TableScreen.fxml", mainMenuScene, worker, TableScreen.STUDENTS);
    }

    @FXML
    public void openCheckItemsPage(){
        stageUtils.newStage("/fxml/CheckOutPage.fxml", mainMenuScene, worker, null);
    }

    private void openManageWorkers() {
        if (worker != null) {
            if (worker.isAdmin() || worker.canEditWorkers()) {
                stageUtils.newStage("/fxml/TableScreen.fxml", mainMenuScene, worker, TableScreen.WORKERS);
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
            stageUtils.errorAlert("Error, no valid stage was found to load.");
            invoke.printStackTrace();
        }
    }

}