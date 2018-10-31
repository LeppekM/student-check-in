package InventoryController;

import Database.OverdueItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ControllerOverdueTab  extends ControllerInventoryPage implements Initializable {

    @FXML
    TableView<OverdueItems> overdueItems;

    @FXML
    TableColumn<OverdueItems, String> partID, serial;

    @FXML
    TableColumn<OverdueItems, Integer> studentID, fee;

    @FXML
    TableColumn<OverdueItems, Date> date;

    final ObservableList<OverdueItems> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data.add(new OverdueItems(560785,"hdmi cord","67v3","2018/3/15",40));
        data.add(new OverdueItems(560785,"aux cord","68v3","2018/3/15",20));
        populteTable(data);
    }

    private void populteTable(ObservableList<OverdueItems> d){
        studentID.setCellValueFactory(new PropertyValueFactory<>("student id"));
        partID.setCellValueFactory(new PropertyValueFactory<>("part id"));
        serial.setCellValueFactory(new PropertyValueFactory<>("serial number"));
        date.setCellValueFactory(new PropertyValueFactory<>("original due date"));
        fee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        overdueItems.setItems(d);
    }
}
