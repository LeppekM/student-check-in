package InventoryController;

import Database.*;

import Database.Objects.Part;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import com.jfoenix.controls.JFXSpinner;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the history tab of the inventory page
 */
public class ControllerEditOnePart extends ControllerEditPart {

    @FXML
    private VBox sceneEditOnePart;

    @FXML
    private JFXTextField nameField, serialField, manufacturerField, priceField,
            vendorField, locationField, barcodeField;

    @FXML
    private JFXSpinner loader;

    @FXML
    private JFXButton saveButton;

    private Part part;

    private EditPart editPart = new EditPart();

    private VendorInformation vendorInformation = new VendorInformation();

    private StageWrapper stageWrapper = new StageWrapper();

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        setFieldValidator();
        saveButton.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15pt; -fx-border-radius: 15pt; -fx-border-color: #043993; -fx-text-fill: #000000;");
        part = null;
    }

    private void disableFields() {
        nameField.setEditable(false);
        manufacturerField.setEditable(false);
        vendorField.setEditable(false);
        priceField.setEditable(false);
    }

    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(serialField);
        stageWrapper.requiredInputValidator(barcodeField);
        stageWrapper.requiredInputValidator(locationField);
        stageWrapper.acceptIntegerOnly(barcodeField);
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    @Override
    public void initPart(Part part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null && part != null) {
            this.part = part;
            nameField.setText(part.getPartName());
            serialField.setText(part.getSerialNumber());
            manufacturerField.setText(part.getManufacturer());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            priceField.setText("$" + df.format(part.getPrice()/100));
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
//                vendorList.getItems().addAll(vendors);
            }
            vendorField.setText(vendorInformation.getVendorFromID(part.getVendor()));
            locationField.setText(part.getLocation());
            barcodeField.setText(part.getBarcode().toString());
        }
    }

    /**
     * Edits the part in database
     */
    @Override
    public void updateItem(){
        if (validateInput()) {
            loader.setVisible(true);
            editPart.editItem(getPartFromInput());
            close();
            partEditedSuccess();
        }
    }

    /**
     * Helper method that sets the part info from the user input
     */
    private Part getPartFromInput() {
        String partName = "";
        if (nameField.getText() != null) {
            partName=nameField.getText().trim();
        }
        String serialNumber = "";
        if (serialField.getText() != null) {
            serialNumber = serialField.getText().trim();
        }
        String manufacturer = "";
        if (manufacturerField.getText() != null) {
            manufacturer = manufacturerField.getText().trim();
        }
        double price = 0;

        // Note: price multiplied by 100, because it is stored in the database as an integer 100 times
        // larger than actual value.
        if (priceField.getText() != null) {
            price = 100 * Double.parseDouble(priceField.getText().replaceAll(",", "").replace("$", "").trim());
        }
        String vendor = "";
        if (vendorField.getText() != null) {
            vendor = vendorField.getText();
        }
        String location = "";
        if (locationField.getText() != null) {
            location = locationField.getText().trim();
        }
        long barcode = 0;
        if (barcodeField.getText() != null) {
            barcode = Long.parseLong(barcodeField.getText());
        }
        part.update(partName, serialNumber, manufacturer, price, vendor, location, barcode);
        return part;
    }

    /**
     * This method uses helper methods to ensure that he inputs for the part are valid.
     * @return true if the inputs are valid, false otherwise
     */
    private boolean validateInput() {
        boolean isValid = true;
        String originalPartName = part.getPartName();
        long originalBarcode = part.getBarcode();
        String originalSerialNumber = part.getSerialNumber();

        // make sure all fields are filled in
        if (!validateAllFieldsFilledIn(serialField.getText(), barcodeField.getText(), locationField.getText())) {
            isValid = false;
            fieldErrorAlert();
        } else {

            // if parts with the given name do not have a unique barcode
            if (!database.hasUniqueBarcodes(originalPartName)) {

                // if the user tried to edit the parts' barcode
                if (!barcodeField.getText().equals(originalBarcode)) {
                    isValid = false;
                    commonBarcodeError(part.getPartName());
                }

            // if the input barcode is not still unique
            } else if (!validateUniqueBarcode()) {
                isValid = false;
                uniqueBarcodeError(originalPartName);
            }

            // if parts with the given name do not have a unique serial number
            if (!database.hasUniqueSerialNumbers(originalPartName)) {

                // if the user tried to edit the parts' serial number
                if (!serialField.getText().equals(originalSerialNumber)) {
                    isValid = false;
                    commonSerialNumberError(part.getPartName());
                }

            // if the input serial number is not still unique
            } else if (!validateUniqueSerialNumber()) {
                isValid = false;
                uniqueSerialNumberError(originalPartName);
            }
        }
        return isValid;
    }

    private boolean validateUniqueSerialNumber() {
        ArrayList<String> serialNumbers = database.getOtherSerialNumbersForPartName(nameField.getText(),"" + part.getPartID());
        return !serialNumbers.contains(serialField.getText());
    }

    private boolean validateUniqueBarcode() {
        ArrayList<String> barcodes = database.getOtherBarcodesForPartName(nameField.getText(), "" + part.getPartID());
        return !barcodes.contains(barcodeField.getText());
    }

    private void uniquePartNameError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that name already exists. Choose a different one.");
        alert.showAndWait();
    }

    /**
     * This method ensures that the inputted fields are not empty.
     * @return true if the fields are not empty, false otherwise
     */
    protected boolean validateAllFieldsFilledIn(String serialNumber, String barcode, String location) {
        return serialNumber != null && !serialNumber.trim().equals("")
                && barcode != null && !barcode.trim().equals("")
                && location != null && !location.trim().equals("");
    }

    /**
     * This method ensures that the inputs supposed to contain numbers do
     * @return true if the input fields supposed to contain numbers do, false otherwise
     */
    protected boolean validateNumberInputsContainNumber(String number) {
        boolean containsNumber = true;
        try {
            Double.parseDouble(number.replace(",", ""));
        } catch (Exception e) {
            containsNumber = false;
        }
        return containsNumber;
    }

    /**
     * This method ensures that the inputs with numbers are non-negative and
     * less than the max value for Doubles.
     * @return true if the number inputs are within this range, false otherwise
     */
    protected boolean validateNumberInputsWithinRange(String price) {
        return Double.parseDouble(price.replace(",", "").replace("$", "")) >= 0
                && Double.parseDouble(price.replace(",", "").replace("$", "")) < Double.MAX_VALUE;
    }

    /**
     * Creates an alert informing user to fill out all fields
     */
    private void fieldErrorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please fill out all fields");

        alert.showAndWait();
    }

    private void commonBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same barcode, so you cannot change one.");
        alert.showAndWait();
    }

    private void commonSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same serial number, so you cannot change one.");
        alert.showAndWait();
    }

    private void nullVendorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please select a vendor");
        alert.showAndWait();
    }

    private void uniqueSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts must have unique serial numbers.");
        alert.showAndWait();
    }

    private void uniqueBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts must have unique barcodes.");
        alert.showAndWait();
    }

    /**
     * Creates an alert informing user to enter non-negative numbers that are non-negative
     */
    private void numberOutOfRangeAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please enter a non-negative number into the price field.");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user that part was edited successfully
     * @author Matthew Karcz
     */
    private void partEditedSuccess(){
        new Thread(new Runnable() {
            @Override public void run() {
                Platform.runLater(() -> {
                    Stage owner = new Stage(StageStyle.TRANSPARENT);
                    StackPane root = new StackPane();
                    root.setStyle("-fx-background-color: TRANSPARENT");
                    Scene scene = new Scene(root, 1, 1);
                    owner.setScene(scene);
                    owner.setWidth(1);
                    owner.setHeight(1);
                    owner.toBack();
                    owner.show();
                    Notifications.create().title("Successful!").text("Part edited successfully.").hideAfter(new Duration(5000)).show();
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    delay.setOnFinished( event -> owner.close() );
                    delay.play();
                });
            }
        }).start();
    }

    /**
     * Returns to main inventory page
     */
    public void goBack(){
        close();
    }

    /**
     * Helper method to send close request to total tab, which receives the request and
     * repopulates the table.
     */
    private void close(){
        //sceneAddPart.getScene().getWindow().hide();
        sceneEditOnePart.fireEvent(new WindowEvent(((Node) sceneEditOnePart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
