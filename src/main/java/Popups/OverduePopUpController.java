package Popups;

import Database.Database;
import Database.ObjectClasses.Student;
import Database.OverdueItem;
import Tables.OverdueInventoryTable;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

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

    private final Database database = Database.getInstance();

    public void populate(OverdueItem overdueItems, OverdueInventoryTable.OIRow overdueTabTableRow) {

        if (overdueTabTableRow == null && overdueItems != null) {
            nameField.setText(overdueItems.getStudentName().get());
            idField.setText(String.valueOf(overdueItems.getID().get()));
            emailField.setText(overdueItems.getEmail().get());
            barcode.setText(String.valueOf(overdueItems.getBarcode().get()));
            partName.setText(overdueItems.getPartName().get());
            dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(overdueItems.getDueDate().get()));
        } else if (overdueItems == null && overdueTabTableRow != null) {
            partName.setText(overdueTabTableRow.getPartName().get());
            barcode.setText(overdueTabTableRow.getBarcode().getValue().toString());
            idField.setText(String.valueOf(overdueTabTableRow.getStudentID().get()));
            dueDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(overdueTabTableRow.getDueDate().get()));
            Student student = database.selectStudent(overdueTabTableRow.getStudentID().get(), null);
            nameField.setText(student.getName());
            emailField.setText(student.getEmail());
        }
    }
}
