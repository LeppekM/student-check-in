package InventoryController;

import CheckItemsController.CheckoutObject;
import Database.Database;
import Database.ObjectClasses.Student;
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
    private GridPane gridCheckedOut;

    @FXML
    private JFXTextField partNameField, barcodeField, serialNumberField, partIDField, priceField;

    private Database database = Database.getInstance();

    /**
     * Adds a label and text field value to a specified row in the last transaction
     * column of the view part pop up.
     *
     * @param row    the row index to be added to
     * @param prompt the text for the label
     * @param value  the text for the text field
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
     *
     * @param row holds potentially necessary info about part used to populate the row
     */
    public void populate(TotalTabTableRow row) {

        //Sets basic part info
        partNameField.setText(row.getPartName().getValue());
        barcodeField.setText(row.getBarcode().getValue().toString());
        serialNumberField.setText(row.getSerialNumber().getValue().toString());
        partIDField.setText("" + row.getPartID().get());

        Student student = database.getStudentToLastCheckout(row.getPartID().get());
        CheckoutObject checkoutObject = database.getLastCheckoutOf(row.getPartID().get());
        String type = actionType(checkoutObject);

        // Sets Last Transaction Info if it has ever been checked out
        int rowNum = 0;
        // if the part has been checked out before, the student email associated with it will not be empty ("")
        if (!student.getEmail().equals("")) {
            addField(rowNum, "Student Name:", student.getName());
            rowNum++;
            addField(rowNum, "Student Email:", student.getEmail());
            rowNum++;
            boolean isOverdue = false;
            //If item is checked out

            if (!checkoutObject.getCheckoutAtDate().equals("")) {

                String className = checkoutObject.getExtendedCourseName(); //Gets classname
                if (className != null && !className.equals("")) {
                    addField(rowNum, "Class Name:", className);
                    rowNum++;
                    addField(rowNum, "Professor Name:", checkoutObject.getExtendedProfessor());
                    rowNum++;
                }
                isOverdue = database.isOverdue(checkoutObject.getDueAt()) && checkoutObject.getCheckinAtDate() == null;
                if (isOverdue) {
                    addField(rowNum, "Fee:", "$" + df.format(row.getPrice().get() / 100)); //Gets p rice
                    rowNum++;
                }
            }
            if (type.equals("Check In")) {
                addField(rowNum, type + " Date:", checkoutObject.getCheckinAtDate());
            } else {
                addField(rowNum, type + " Date:", checkoutObject.getCheckoutAtDate());
            }
            rowNum++;
            addField(rowNum, "Due Date:", checkoutObject.getDueAt());
            if (isOverdue) {
                gridCheckedOut.getChildren().get(rowNum * 2).setStyle("-fx-text-fill: red");
            }
        } else {
            gridContainer.getChildren().remove(2);
            gridContainer.getChildren().remove(1);
        }

        // removes fee info column
        gridContainer.getChildren().remove(gridContainer.getChildren().size() - 1);
        gridContainer.getChildren().remove(gridContainer.getChildren().size() - 1);



    }

    /**
     * Populates the 3 columns of the total inventory tab view part pop up
     *
     * @param row holds potentially necessary info about part used to populate the row
     */
    public void populate(CompleteInventoryTable.CIRow row) {

        //Sets basic part info
        partNameField.setText(row.getPartName().getValue());
        barcodeField.setText(row.getBarcode().getValue().toString());
        serialNumberField.setText(row.getSerialNumber().getValue().toString());
        partIDField.setText("" + row.getPartID().get());

        Student student = database.getStudentToLastCheckout(row.getPartID().get());
        CheckoutObject checkoutObject = database.getLastCheckoutOf(row.getPartID().get());
        String type = actionType(checkoutObject);

        // Sets Last Transaction Info if it has ever been checked out
        int rowNum = 0;
        // if the part has been checked out before, the student email associated with it will not be empty ("")
        if (!student.getEmail().equals("")) {
            addField(rowNum, "Student Name:", student.getName());
            rowNum++;
            addField(rowNum, "Student Email:", student.getEmail());
            rowNum++;
            boolean isOverdue = false;
            //If item is checked out

            if (!checkoutObject.getCheckoutAtDate().equals("")) {

                String className = checkoutObject.getExtendedCourseName(); //Gets classname
                if (className != null && !className.equals("")) {
                    addField(rowNum, "Class Name:", className);
                    rowNum++;
                    addField(rowNum, "Professor Name:", checkoutObject.getExtendedProfessor());
                    rowNum++;
                }
                isOverdue = database.isOverdue(checkoutObject.getDueAt()) && checkoutObject.getCheckinAtDate() == null;
                if (isOverdue) {
                    addField(rowNum, "Fee:", "$" + df.format(row.getPrice().get() / 100)); //Gets p rice
                    rowNum++;
                }
            }
            if (type.equals("Check In")) {
                addField(rowNum, type + " Date:", checkoutObject.getCheckinAtDate());
            } else {
                addField(rowNum, type + " Date:", checkoutObject.getCheckoutAtDate());
            }
            rowNum++;
            addField(rowNum, "Due Date:", checkoutObject.getDueAt());
            if (isOverdue) {
                gridCheckedOut.getChildren().get(rowNum * 2).setStyle("-fx-text-fill: red");
            }
        } else {
            gridContainer.getChildren().remove(2);
            gridContainer.getChildren().remove(1);
        }

        // removes fee info column
        gridContainer.getChildren().remove(gridContainer.getChildren().size() - 1);
        gridContainer.getChildren().remove(gridContainer.getChildren().size() - 1);



    }

    /**
     * Returns to the total inventory tab
     */
    public void goBack() {
        sceneViewTotalPart.fireEvent(new WindowEvent(((Node) sceneViewTotalPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private String actionType(CheckoutObject checkout) {
        try {
            if (checkout.getCheckinAtDate().isEmpty()) {
                return "Checkout";
            } else {
                return "Check In";
            }
        } catch (NullPointerException e) {
            return "Checked Out";
        }
    }

}