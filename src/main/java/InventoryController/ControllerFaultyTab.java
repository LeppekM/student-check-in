package InventoryController;

import Database.Database;
import Database.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerFaultyTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    private TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        populateTable();
    }

    /*
     * Sets the values for each table column, empties the current table, then calls selectParts to populate it.
     */
    @FXML
    private void populateTable() {
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        manufacturer.setCellValueFactory(new PropertyValueFactory("manufacturer"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        vendor.setCellValueFactory(new PropertyValueFactory("vendor"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        barcode.setCellValueFactory(new PropertyValueFactory("barcode"));
        fault.setCellValueFactory(new PropertyValueFactory("fault"));
        partID.setCellValueFactory(new PropertyValueFactory("partID"));

        this.data.clear();
        this.tableView.getItems().clear();

        this.data = selectParts("SELECT * from parts WHERE isDeleted = 0 AND faultQuantity = 1 ORDER BY partID", this.data);

        this.tableView.getItems().setAll(this.data);
    }

//    @FXML
//    public void search(){
//        System.out.println(searchTotal.getText());
//    }
}
