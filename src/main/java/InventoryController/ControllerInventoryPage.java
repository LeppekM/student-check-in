package InventoryController;

import Database.*;
import Database.ObjectClasses.Part;
import Database.ObjectClasses.Worker;
import HelperClasses.ExportToExcel;
import HelperClasses.StageWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ControllerInventoryPage extends ControllerMenu implements IController, Initializable {

    @FXML
    private StackPane inventoryScene;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab totalTab, historyTab, checkedOutTab, overdueTab, faultsTab;

    @FXML
    private ControllerTotalTab totalTabPageController;

    @FXML
    private ControllerHistoryTab historyTabPageController;

    @FXML
    private ControllerCheckedOutTab checkedOutTabPageController;

    @FXML
    private ControllerOverdueTab overdueTabPageController;

    @FXML
    private ControllerFaultyTab faultyTabPageController;

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
                } else if (newTab == faultsTab) {
                    updateFaultsTab();
                }
            }
        });

        //back.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993;");
        back.getStylesheets().add("/css/CheckButton.css");

        tabPane.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tabPane.setTabMinWidth(tabPane.getWidth() / 5.05);
            tabPane.setTabMaxWidth(tabPane.getWidth() / 5.05);
        });
        //});
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

    private void updateFaultsTab() {
        faultyTabPageController.populateTable();
    }

    /** Takes a raw statement and a data list as parameters, then returns the data list populated with the appropriate
     * parts based on the statement where clause;
     * @param rawStatement = The statement to select parts.
     * @param data = List of part objects meant to be populated and used to fill a TableView
     * @returns The list of parts filled with the parts based on what was requested in the raw statement.
     * @author Matthew Karcz
     */
    public static ObservableList<Part> selectParts(String rawStatement, ObservableList<Part> data) {
        StudentCheckIn.logger.info(rawStatement);
        Statement currentStatement = null;
        try {
            Connection connection = database.getConnection();
            currentStatement = connection.createStatement();
            ResultSet rs = currentStatement.executeQuery(rawStatement);
            while (rs.next()) {
                String serialNumber = rs.getString("serialNumber");
                String partName = rs.getString("partName");
                //double price = rs.getDouble("price");
                //String vendor = rs.getString("vendorID");
                //String manufacturer = rs.getString("manufacturer");
                String location = rs.getString("location");
                long barcode = rs.getLong("barcode");
                boolean fault = rs.getInt("isFaulty") == 1;
                int partID = rs.getInt("partID");
                //int isCheckedOut = rs.getInt("isCheckedOut");
//                String faultDesc = rs.getString("faultDesc");
                Part part = new Part(partName, serialNumber, location, barcode, fault, partID);
               // part.setCheckedOut(isCheckedOut);
                data.add(part);
            }
        } catch (SQLException e) {
            StudentCheckIn.logger.error("Could not retrieve the list of students");
            e.printStackTrace();
        } finally {
            if (currentStatement != null) {
                try {
                    currentStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            currentStatement = null;
        }
        return data;
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
            totalTabPageController.initWorker(worker);
            historyTabPageController.initWorker(worker);
            faultyTabPageController.initWorker(worker);
        }
    }

    /**
     *Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     * @author Matthew Karcz
     */
    @FXML
    public void goBack(){
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
