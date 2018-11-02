package InventoryController;

import Database.OverdueItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerOverdueTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    private TableView<OverdueItems> overdueItems;

    @FXML
    TableColumn<OverdueItems, String> partID, serial, date;

    @FXML
    TableColumn<OverdueItems, Integer> studentID, price;

    final ObservableList<OverdueItems> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data.add(new OverdueItems(560785,"hdmi cord","67v3","2018/3/15",40));
        data.add(new OverdueItems(560785,"aux cord","68v3","2018/3/15",20));
//        overdueItems.getColumns().add(studentID);
//        overdueItems.getColumns().add(partID);
//        overdueItems.getColumns().add(serial);
//        overdueItems.getColumns().add(date);
//        overdueItems.getColumns().add(price);
        populteTable(data);
    }

    private void populteTable(ObservableList<OverdueItems> d){
        studentID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        partID.setCellValueFactory(new PropertyValueFactory<>("part"));
        serial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        for (OverdueItems i: d) {
            overdueItems.getItems().add(i);
        }
    }
}
