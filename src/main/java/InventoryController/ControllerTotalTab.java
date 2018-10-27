package InventoryController;

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

    @FXML
    private TextField searchTotal;

    @FXML
    public TableView<Part> tableView;

    @FXML
    private TableColumn<Part,String> partName, serialNumber, manufacturer, price, vendor, location,
            barcode, fault, partID;

    private static final ObservableList<Part> data
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

        Part part1 = new Part("Raspberry Pi", "3453I214", "Pi Inc.", 35.99, "MSOE", "OUT", "J26734", false, 0);
        Part part2 = new Part("HDMI Cable", "H2J4364", "Sony", 4.99, "MSOE", "IN", "A43453", false, 1);
        data.add(part1);
        data.add(part2);

        tableView.getItems().setAll(this.data);
    }

    @FXML
    public void search(){
        System.out.println(searchTotal.getText());
    }
}
