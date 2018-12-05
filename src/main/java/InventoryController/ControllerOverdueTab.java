package InventoryController;

import Database.Database;
import Database.OverdueItems;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;

import java.net.URL;
import java.text.DecimalFormat;
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
        Label emptytableLabel = new Label("No parts found.");
        emptytableLabel.setFont(new Font(18));
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        overdueItems.setPlaceholder(emptytableLabel);
        database = new Database();
        data = database.getOverdue();
        for (int i = 0; i < data.size(); i++){
            double p = Double.parseDouble(data.get(i).getPrice());
            data.get(i).setPrice("$" + df.format(p));
        }
        populteTable();
    }

    /**
     * This method populates the gui based off of the data in the Observable list
     *
     * @author Bailey Terry
     */
    public void populteTable() {
        studentID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        partID.setCellValueFactory(new PropertyValueFactory<>("part"));
        serial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        overdueItems.setItems(data);
    }
}
