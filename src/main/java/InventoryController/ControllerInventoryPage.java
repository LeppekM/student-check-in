package InventoryController;

import Database.*;
import Database.Objects.Part;
import Database.Objects.Worker;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ControllerInventoryPage extends ControllerMenu implements Initializable {

    @FXML
    private AnchorPane inventoryScene;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab totalTab, historyTab, checkedOutTab, overdueTab, faultsTab;

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

        back.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993;");
        //});
    }

    public void addWorker(Worker worker) {
        System.out.println("HERE");
        if (this.worker == null) {
            this.worker = worker;
        }
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
                double price = rs.getDouble("price");
                String vendor = rs.getString("vendorID");
                String manufacturer = rs.getString("manufacturer");
                String location = rs.getString("location");
                String barcode = rs.getString("barcode");
                boolean fault = (rs.getInt("isFaulty") == 1) ? true : false;
                int partID = rs.getInt("partID");
                int isDeleted = rs.getInt("isDeleted");
                int isCheckedOut = rs.getInt("isCheckedOut");
//                String faultDesc = rs.getString("faultDesc");
                Part part = new Part(partName, serialNumber, manufacturer, price, vendor, location, barcode, fault, partID, isDeleted);
                part.setCheckedOut(isCheckedOut);
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
     * This method brings up the FXML page for showing the info about the selected part
     * @param part - The part that was selected
     * @param type - The type of part, determines what information is shown
     * @author Matthew Karcz
     */
    public void showInfoPage(Part part, String type){
        Stage stage = new Stage();
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/ShowPart.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            Parent root = loader.load();
            ((ControllerShowPart) loader.getController()).initPart(part, type);
            Scene scene = new Scene(root, 400, 400);
            stage.setTitle("Part Information");
            stage.initOwner(inventoryScene.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.getIcons().add(new Image("images/msoe.png"));
            stage.showAndWait();
        } catch (IOException e) {
            StudentCheckIn.logger.error("IOException: Loading Show Part.");
            e.printStackTrace();
        }
    }

    /**
     *Clears the current scene and loads the main menu. If no menu stage was found, sends an alert to user.
     * @author Matthew Karcz
     */
    @FXML
    public void goBack(){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("fxml/Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            inventoryScene.getChildren().clear();
            inventoryScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            StudentCheckIn.logger.error("IOException: No valid stage was found to load");
            alert.showAndWait();
        }
    }
}
