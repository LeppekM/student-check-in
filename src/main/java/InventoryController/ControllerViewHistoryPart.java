package InventoryController;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ControllerViewHistoryPart {

    @FXML
    private VBox sceneViewHistoryPart;

    @FXML
    private JFXTextField studentNameField, studentEmailField, partNameField, serialNumberField, actionField, dateField;

    public void populate(HistoryTabTableRow row) {
        studentNameField.setText(row.getStudentName().get());
        studentEmailField.setText(row.getStudentEmail().get());
        partNameField.setText(row.getPartName().get());
        serialNumberField.setText(row.getSerialNumber().toString());
        actionField.setText(row.getAction().get());
        dateField.setText(row.getDate().get());
    }

    public void goBack() {
        sceneViewHistoryPart.fireEvent(new WindowEvent(((Node) sceneViewHistoryPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}