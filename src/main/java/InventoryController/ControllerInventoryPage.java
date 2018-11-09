package InventoryController;

import Database.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
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
    private Button back;

    protected static Database database;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    //Takes a raw statement and a data list as parameters, then returns the data list populated with the appropriate
    //parts based on the statement where clause;
    //param rawStatement = The statement to select parts.
    //param data = List of part objects meant to be populated and used to fill a TableView
    public ObservableList<Part> selectParts(String rawStatement, ObservableList<Part> data){
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
                boolean fault = (rs.getInt("faultQuantity") == 1) ? true : false;
                int partID = rs.getInt("partID");
                boolean isDeleted = (rs.getInt("isDeleted") == 0) ? false : true;
                Part part = new Part(partName, serialNumber, manufacturer, price/100, vendor, location, barcode, fault, partID, isDeleted);
                data.add(part);
            }
        } catch (SQLException e) {
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

    @FXML
    public void goBack(){
        try {
            URL myFxmlURL = ClassLoader.getSystemResource("Menu.fxml");
            FXMLLoader loader = new FXMLLoader(myFxmlURL);
            inventoryScene.getChildren().clear();
            inventoryScene.getScene().setRoot(loader.load(myFxmlURL));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error, no valid stage was found to load.");
            alert.showAndWait();
        }
    }
}
