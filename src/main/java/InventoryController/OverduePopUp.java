package InventoryController;

import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.text.DecimalFormat;

public class OverduePopUp extends ControllerOverdueTab {

    @FXML
    private JFXTextField nameField, email, serialNumber, partName, dueDate, fee;

    public void populate(OverdueItem overdueItems){
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        nameField.setText(overdueItems.getName().get());
        email.setText(overdueItems.getEmail().get());
        serialNumber.setText(overdueItems.getSerial().get());
        partName.setText(overdueItems.getPart().get());
        dueDate.setText(overdueItems.getDate().get());
        overdueItems.setPrice(overdueItems.getPrice().get().replaceAll("\\$", ""));
        overdueItems.setPrice(overdueItems.getPrice().get().replaceAll(",", ""));
//        overdueItems.setPrice(overdueItems.getPrice().get().trim());
        fee.setText("$" + df.format(Double.parseDouble(overdueItems.getPrice().get())));
    }
}
