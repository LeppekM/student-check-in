package InventoryController;

import Database.Part;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerHistoryTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableColumn<Part,String> student, partName, serialNumber, location, quantity, date;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTable();
    }

    private void populateTable() {

        student.setCellValueFactory(new PropertyValueFactory("student"));
        partName.setCellValueFactory(new PropertyValueFactory("partName"));
        serialNumber.setCellValueFactory(new PropertyValueFactory("serialNumber"));
        location.setCellValueFactory(new PropertyValueFactory("location"));
        quantity.setCellValueFactory(new PropertyValueFactory("quantity"));
        date.setCellValueFactory(new PropertyValueFactory("date"));
    }

    public void search() {

    }
}
