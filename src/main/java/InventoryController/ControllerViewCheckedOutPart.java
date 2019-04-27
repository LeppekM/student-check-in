package InventoryController;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ControllerViewCheckedOutPart {

    @FXML
    private VBox sceneViewCheckedOutPart;

    @FXML
    private JFXTextField studentNameField, studentEmailField, partNameField, barcodeField, serialNumberField, partIDField, checkedOutDateField, dueDateField, feeField;

    public void populate(CheckedOutTabTableRow row) {
        studentNameField.setText(row.getStudentName().get());
        studentEmailField.setText(row.getStudentEmail().get());
        partNameField.setText(row.getPartName().get());
        barcodeField.setText(row.getBarcode().get());
        serialNumberField.setText(row.getSerialNumber().get());
        partIDField.setText("" + row.getPartID().get());
        checkedOutDateField.setText(row.getCheckedOutAt().get());
        dueDateField.setText(row.getDueDate().get());
        feeField.setText("$" + Long.parseLong(row.getFee().get())/100); // TODO: FORMAT 2 decimal places
    }

    public void goBack() {
        sceneViewCheckedOutPart.fireEvent(new WindowEvent(((Node) sceneViewCheckedOutPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}