package InventoryController;

import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class OverduePopUp extends ControllerOverdueTab implements Initializable {

    @FXML
    private JFXTextField name, email, serialNumber, partName, dueDate, fee;

    /**
     * This method puts all overdue items into the list for populating the gui table
     *
     * @param location
     * @param resources
     * @author Bailey Terry
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        database = new Database();
    }

    public void populate(Object overdueItems){
        OverdueItem item = ((OverdueItem) overdueItems);
        name.setText(item.getName());
        email.setText(item.getEmail());
        serialNumber.setText(item.getSerial());
        partName.setText(item.getPart());
        dueDate.setText(item.getDate());
        fee.setText(item.getPrice());
    }
}
