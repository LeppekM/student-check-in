package InventoryController;

import Database.Database;
import Database.Part;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerTotalTab  extends ControllerInventoryPage implements Initializable {

//    @FXML
//    private TextField searchTotal;

    @FXML
    public TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;

    protected static Database database;

    private static ObservableList<Part> data
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
        tableView.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Part rowData = row.getItem();
                    editPart(rowData);
                }
            });
            return row;
        });
    }

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
        tableView.getItems().clear();

        //this.data = selectParts("total", this.data);

        tableView.getItems().setAll(this.data);
    }

//    @FXML
//    public void search(){
//        System.out.println(searchTotal.getText());
//    }
}
