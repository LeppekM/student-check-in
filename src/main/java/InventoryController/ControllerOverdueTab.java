package InventoryController;

import Database.Database;
import Database.OverdueItems;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerOverdueTab extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView<OverdueItems> overdueItems;

    @FXML
    TableColumn<OverdueItems, String> partID, serial, date;

    @FXML
    TableColumn<OverdueItems, Integer> studentID, price;

    private ObservableList<OverdueItems> data;
    private Database database;

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new Database();
        data = database.getOverdue();
        populteTable();
    }

    /**
     * This method populates the gui based off of the data in the Observable list
     *
     * @author Bailey Terry
     */
    private void populteTable() {
        studentID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        partID.setCellValueFactory(new PropertyValueFactory<>("part"));
        serial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        overdueItems.setItems(data);
    }
}
