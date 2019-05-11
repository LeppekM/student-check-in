package InventoryController;

import Database.Database;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;

import java.text.DecimalFormat;

/**
 * Sets the info for the view total part pop up and controls the back button
 */
public class ControllerViewTotalPart {

    DecimalFormat df = new DecimalFormat("#,###,##0.00");

    @FXML
    private VBox sceneViewTotalPart;

    @FXML
    private HBox gridContainer;

    @FXML
    private Font x1;

    @FXML
    private GridPane grid, gridCheckedOut, gridFaulty;

    @FXML
    private Label checkoutActionDate, dueDatePrompt, professorNamePrompt, classNamePrompt;

    @FXML
    private JFXTextField studentNameField, studentEmailField, professorNameField, classNameField, partNameField, barcodeField, serialNumberField, partIDField, checkoutActionDateField, dueDateField, priceField;

    @FXML
    private JFXTextArea faultField;

    /**
     * Adds a label and text field value to a specified row in the last transaction
     * column of the view part pop up.
     * @param row the row index to be added to
     * @param prompt the text for the label
     * @param value the text for the text field
     */
    private void addField(int row, String prompt, String value) {
        Label label = new Label(prompt);
        label.setFont(x1);
        JFXTextField field = new JFXTextField(value);
        field.setEditable(false);
        gridCheckedOut.add(label, 0, row);
        gridCheckedOut.add(field, 1, row);
    }

    /**
     * Populates the 3 columns of the total inventory tab view part pop up
     * @param row holds potentially necessary info about part used to populate the row
     */
    public void populate(TotalTabTableRow row) {
        // Sets basic part info
        partNameField.setText(row.getPartName().get());
        barcodeField.setText(row.getBarcode().get());
        serialNumberField.setText(row.getSerialNumber().get());
        partIDField.setText("" + row.getPartID().get());

        // Sets Last Transaction Info if it has ever been checked out
        int rowNum = 0;
        // if the part has been checked out before, the student email associated with it will not be empty ("")
        if (!row.getStudentEmail().get().equals("")) {
            addField(rowNum, "Student Name:", row.getStudentName().get());
            rowNum++;
            addField(rowNum, "Student Email:", row.getStudentEmail().get());
            rowNum++;
            boolean isOverdue = false;
            if (row.getActionType().equals("Check Out")) {
                Database database = new Database();
                String className = row.getClassName().get();
                if (className != null && !className.equals("")) {
                    addField(rowNum, "Class Name:", className);
                    rowNum++;
                    addField(rowNum, "Professor Name:", row.getProfessorName().get());
                    rowNum++;
                }
                isOverdue = database.isOverdue(row.getDueDate().get());
                if (isOverdue) {
                    addField(rowNum, "Fee:", "$" + df.format(Double.parseDouble(row.getFee().get())/100));
                    rowNum++;
                }
            }
            addField(rowNum, row.getActionType() + " Date:", row.getAction());
            rowNum++;
            addField(rowNum, "Due Date:", row.getDueDate().get());
            if (isOverdue) {
                ((Label) gridCheckedOut.getChildren().get(rowNum * 2)).setStyle("-fx-text-fill: red");
            }
            rowNum++;
        } else {
            gridContainer.getChildren().remove(2);
            gridContainer.getChildren().remove(1);
        }

        // Sets faulty info (removes fee info column if not faulty)
        if (row.sFaulty()) {
            priceField.setText("$" + df.format(Double.parseDouble(row.getFee().get())/100));
            faultField.setText(row.getFaultDescription().get());
        } else {
            gridContainer.getChildren().remove(gridContainer.getChildren().size()-1);
            gridContainer.getChildren().remove(gridContainer.getChildren().size()-1);
        }
    }

    /**
     * Returns to the total inventory tab
     */
    public void goBack() {
        sceneViewTotalPart.fireEvent(new WindowEvent(((Node) sceneViewTotalPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}