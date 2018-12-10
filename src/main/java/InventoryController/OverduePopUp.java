package InventoryController;

import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class OverduePopUp extends ControllerOverdueTab {

    @FXML
    private JFXTextField nameField, email, serialNumber, partName, dueDate, fee;

    public void populate(OverdueItem overdueItems){
//        System.out.println(overdueItems.getName());
        nameField.setText(overdueItems.getName());
        email.setText(overdueItems.getEmail());
        serialNumber.setText(overdueItems.getSerial());
        partName.setText(overdueItems.getPart());
        dueDate.setText(overdueItems.getDate());
        fee.setText(overdueItems.getPrice());
    }
}
