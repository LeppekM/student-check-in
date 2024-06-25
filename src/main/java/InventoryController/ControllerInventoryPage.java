package InventoryController;

import Database.Database;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import HelperClasses.StageUtils;
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
    private Button back;

    protected static Database database = Database.getInstance();
    private Worker worker;
    ExportToExcel export = new ExportToExcel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        back.getStylesheets().add("/css/CheckButton.css");

        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth(tabPane.getWidth() / 5.05);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 5.05);
        });

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
            this.worker = worker;        }
    }


    @FXML
    public void goBack() {
        StageUtils.getInstance().goBack(inventoryScene, worker);
    }
}
