package InventoryController;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ControllerViewFaultyPart {

    @FXML
    private VBox sceneViewFaultyPart;

    @FXML
    private JFXTextField studentNameField, studentEmailField, partNameField, barcodeField, descriptionField, priceField, locationField, dateField;

    public void populate(FaultyPartTabTableRow row) {
        studentNameField.setText(row.getStudentName().get());
        studentEmailField.setText(row.getStudentEmail().get());
        partNameField.setText(row.getPartName().get());
        barcodeField.setText(row.getBarcode().getValue().toString());
        descriptionField.setText(row.getDescription().get());
        priceField.setText(row.getPrice().get());
        locationField.setText(row.getLocation().get());
        dateField.setText(row.getDate().get());
    }

    public void goBack() {
        sceneViewFaultyPart.fireEvent(new WindowEvent(((Node) sceneViewFaultyPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
