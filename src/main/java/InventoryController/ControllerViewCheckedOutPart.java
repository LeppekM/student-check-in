package InventoryController;

import Database.Database;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;

import java.text.DecimalFormat;

public class ControllerViewCheckedOutPart {

    DecimalFormat df = new DecimalFormat("#,###,##0.00");

    @FXML
    private VBox sceneViewCheckedOutPart;

    @FXML
    private Font x1;

    @FXML
    private GridPane grid;

    @FXML
    private JFXTextField studentNameField, studentEmailField, partNameField, barcodeField, serialNumberField, partIDField, checkedOutDateField, dueDateField, feeField;

    @FXML
    private Label dueDatePrompt;

    public void populate(CheckedOutTabTableRow row) {
        studentNameField.setText(row.getStudentName().get());
        studentEmailField.setText(row.getStudentEmail().get());
        partNameField.setText(row.getPartName().get());
        barcodeField.setText(row.getBarcode().getValue().toString());
        serialNumberField.setText(row.getSerialNumber().get());
        partIDField.setText("" + row.getPartID().get());
        checkedOutDateField.setText(row.getCheckedOutAt().get());
        dueDateField.setText(row.getDueDate().get());
        Database database = new Database();
        if (database.isOverdue(row.getDueDate().get())) {
            dueDatePrompt.setStyle("-fx-text-fill: red");
            Label feeLabel = new Label("Fee:");
            feeLabel.setFont(x1);
            JFXTextField feeField = new JFXTextField("$" + df.format(Long.parseLong(row.getFee().get())/100));
            grid.add(feeLabel, 0, 8);
            grid.add(feeField, 1, 8);
        }
    }

    public void goBack() {
        sceneViewCheckedOutPart.fireEvent(new WindowEvent(((Node) sceneViewCheckedOutPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}