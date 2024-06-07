package InventoryController;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerInventoryPage extends ControllerMenu implements IController, Initializable {

    @FXML
    private StackPane inventoryScene;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab totalTab, historyTab, checkedOutTab, overdueTab;

    @FXML
    private ControllerTotalTab totalTabPageController;

    @FXML
    private ControllerHistoryTab historyTabPageController;

    @FXML
    private ControllerCheckedOutTab checkedOutTabPageController;

    @FXML
    private ControllerOverdueTab overdueTabPageController;


    @FXML
    private Button back;

    protected static Database database = new Database();
    private Worker worker;
    ExportToExcel export = new ExportToExcel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                // if the user was on the total tab
                if (newTab == historyTab) {
                    updateHistoryTab();
                } else if (newTab == checkedOutTab) {
                    updateCheckedOutTab();
                } else if (newTab == overdueTab) {
                    updateOverdueTab();
                }
            }
        });


        back.getStylesheets().add("/css/CheckButton.css");

        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth(tabPane.getWidth() / 5.05);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 5.05);
        });

    }

    private void updateHistoryTab() {
        historyTabPageController.populateTable();
    }

    private void updateCheckedOutTab() {
        checkedOutTabPageController.populateTable();
    }

    private void updateOverdueTab() {
        overdueTabPageController.populateTable();
    }


    /**
     * Used to keep track of which worker is currently logged in by passing the worker into
     * each necessary class
     *
     * @param worker the currently logged in worker
     */
    @Override
    public void initWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
            totalTabPageController.initWorker(worker);
            historyTabPageController.initWorker(worker);
        }
    }

    /**
     * Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     *
     * @author Matthew Karcz
     */
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            Parent root = loader.load();
            IController controller = loader.<IController>getController();
            controller.initWorker(worker);
            inventoryScene.getScene().setRoot(root);
            ((IController) loader.getController()).initWorker(worker);
            inventoryScene.getChildren().clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            StudentCheckIn.logger.error("IOException: No valid stage was found to load");
            alert.showAndWait();
        }
    }
}
