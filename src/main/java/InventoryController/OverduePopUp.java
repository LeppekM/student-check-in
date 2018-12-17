package InventoryController;

import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class OverduePopUp extends ControllerOverdueTab {

    @FXML
    private JFXTextField nameField, email, serialNumber, partName, dueDate, fee;

    public void populate(OverdueItem overdueItems){
        nameField.setText(overdueItems.getName().get());
        email.setText(overdueItems.getEmail().get());
        serialNumber.setText(overdueItems.getSerial().get());
        partName.setText(overdueItems.getPart().get());
        dueDate.setText(overdueItems.getDate().get());
        fee.setText(overdueItems.getPrice().get());
    }
}
