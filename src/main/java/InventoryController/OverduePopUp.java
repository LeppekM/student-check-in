package InventoryController;

import Database.Database;
import Database.OverdueItems;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OverduePopUp implements Initializable {

    @FXML
    private JFXTextField name, email, serialNumber, partName, dueDate, fee;

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
    }

    public void populate(Object overdueItems){
        OverdueItems item = ((OverdueItems) overdueItems);
        name.setText(item.getName());
        email.setText(item.getEmail());
        serialNumber.setText(item.getSerial());
        partName.setText(item.getPart());
        dueDate.setText(item.getDate());
        fee.setText(item.getPrice());
    }
}
