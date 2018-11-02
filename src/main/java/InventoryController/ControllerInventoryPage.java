package InventoryController;

import Database.Database;
import Database.Part;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

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
    private Button back, add, remove;

    protected static Database database;
//    public static Connection connection;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
//        this.connection = database.getConnection();
//        database.connect();
    }

    @FXML
    public void addPart(){
        //Called when the "Add" button is clicked
    }

    @FXML
    public void editPart(Part part){
        //Called when a part is double clicked in a table.
        //@param part the part that was double clicked
    }

    @FXML
    public void removePart(){
        //Called when the "Remove" button is clicked
        ControllerTotalTab totalTab = new ControllerTotalTab();
        if(totalTab.tableView.getSelectionModel().getSelectedItems().size() == 1){
            database.deleteItem(totalTab.tableView.getSelectionModel().getSelectedItem().getPartID());
        }
    }

    public ObservableList<Part> selectParts(String tab, ObservableList<Part> data){
        Statement currentStatement = null;
        try {
            String rawStatement = "SELECT * from parts";
            Database database2 = new Database();
            Connection connection2 = database2.getConnection();
            currentStatement = connection2.createStatement();
            ResultSet rs = currentStatement.executeQuery(rawStatement);
            while (rs.next()) {
                String serialNumber = rs.getString("serialNumber");
                String partName = rs.getString("partName");
                double price = rs.getDouble("price");
                String vendor = rs.getString("vendor");
                String manufacturer = rs.getString("manufacturer");
                String location = rs.getString("location");
                String barcode = rs.getString("barcode");
                boolean fault = (rs.getInt("fault") == 1) ? true : false;
                int partID = rs.getInt("partID");
                boolean isDeleted = rs.getBoolean("isDeleted");
                Part part = new Part(partName, serialNumber, manufacturer, price, vendor, location, barcode, fault, partID, isDeleted);
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
