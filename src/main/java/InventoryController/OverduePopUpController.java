package InventoryController;

import Database.*;
import Database.ObjectClasses.Student;
import Database.ObjectClasses.Worker;
import Database.OverdueItem;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class OverduePopUpController {

    @FXML
    private JFXTextField nameField;

    @FXML
    private JFXTextField idField;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXTextField partName;

    @FXML
    private JFXTextField barcode;

    @FXML
    private JFXTextField dueDate;

    private Database database = new Database();

    public void populate(OverdueItem overdueItems, OverdueTabTableRow overdueTabTableRow) {

        if (overdueTabTableRow == null && overdueItems != null) {
            nameField.setText(overdueItems.getName().get());
            idField.setText(String.valueOf(overdueItems.getID().get()));
            emailField.setText(overdueItems.getEmail().get());
            barcode.setText(String.valueOf(overdueItems.getBarcode().get()));
            partName.setText(overdueItems.getPart().get());
            dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(overdueItems.getDate().get()));
//            overdueItems.setPrice(overdueItems.getPrice().get().replaceAll("\\$", ""));
//            overdueItems.setPrice(overdueItems.getPrice().get().replaceAll(",", ""));
//            fee.setText("$" + df.format(Double.parseDouble(overdueItems.getPrice().get())));
        } else if (overdueItems == null && overdueTabTableRow != null) {
            partName.setText(overdueTabTableRow.getPartName().get());
            barcode.setText(overdueTabTableRow.getBarcode().getValue().toString());
            idField.setText(String.valueOf(overdueTabTableRow.getStudentID().get()));
            dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(overdueTabTableRow.getDueDate().get()));
//            overdueTabTableRow.setFee(overdueTabTableRow.getFee().get().replaceAll("\\$", ""));
//            overdueTabTableRow.setFee(overdueTabTableRow.getFee().get().replaceAll(",", ""));
//            fee.setText("$" + df.format(Double.parseDouble(overdueTabTableRow.getFee().get())));
            Student student = database.selectStudent(overdueTabTableRow.getStudentID().get(), null);
            nameField.setText(student.getName());
            emailField.setText(student.getEmail());
        }
    }
}
