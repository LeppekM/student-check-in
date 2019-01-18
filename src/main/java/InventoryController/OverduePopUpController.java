package InventoryController;

import Database.*;
import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.text.DecimalFormat;

public class OverduePopUpController extends ControllerOverdueTab {

    @FXML
    private JFXTextField nameField, email, serialNumber, partName, dueDate, fee;

    private Database database = new Database();

    public void populate(OverdueItem overdueItems, OverdueTabTableRow overdueTabTableRow){
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (overdueTabTableRow == null && overdueItems != null) {
            nameField.setText(overdueItems.getName().get());
            email.setText(overdueItems.getEmail().get());
            serialNumber.setText(overdueItems.getSerial().get());
            partName.setText(overdueItems.getPart().get());
            dueDate.setText(overdueItems.getDate().get());
            overdueItems.setPrice(overdueItems.getPrice().get().replaceAll("\\$", ""));
            overdueItems.setPrice(overdueItems.getPrice().get().replaceAll(",", ""));
            fee.setText("$" + df.format(Double.parseDouble(overdueItems.getPrice().get())));
        }else if (overdueItems == null && overdueTabTableRow != null){
            partName.setText(overdueTabTableRow.getPartName().get());
            serialNumber.setText(overdueTabTableRow.getSerialNumber().get());
            dueDate.setText(overdueTabTableRow.getDueDate().get());
            fee.setText("$" + df.format(Double.parseDouble(overdueTabTableRow.getFee().get())));
            Student student = database.selectStudent(Integer.parseInt(overdueTabTableRow.getStudentID().get()));
            nameField.setText(student.getName());
            email.setText(student.getEmail());
        }
    }
}
