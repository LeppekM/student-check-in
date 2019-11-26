package InventoryController;

import Database.*;

import Database.ObjectClasses.Part;
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
        part = null;
    }

    /**
     * This method sets some fields to not be editable
     */
    private void disableFields() {
        nameField.setEditable(false);
        manufacturerField.setEditable(false);
        vendorField.setEditable(false);
        priceField.setEditable(false);
    }

    /**
     * This method requires that the editable fields not be editable, and it only allows
     * the user to enter numbers into the barcode field.
     */
    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(serialField);
        stageWrapper.requiredInputValidator(barcodeField);
        stageWrapper.requiredInputValidator(locationField);
        stageWrapper.acceptIntegerOnly(barcodeField);
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part the part being edited
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
            priceField.setText("$" + df.format(part.getPrice()));
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
            price = Double.parseDouble(priceField.getText().replaceAll(",", "").replace("$", "").trim());
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

            // if parts with the given name do not have a unique barcode, error occurs.
            if (originalBarcode!= Long.parseLong(barcodeField.getText())) {
                if (editPart.barcodeUsed(Long.parseLong(barcodeField.getText()))) {
                    isValid = false;
                    barcodeExistsError();
                }
            }


            //If a serial number is changed to one already present, an error occurs.
            if(!originalSerialNumber.equals(serialField.getText()) && validateUniqueSerialNumber()){
                isValid = false;
                uniqueSerialNumberError(originalPartName);
            }
        }
        return isValid;
    }

    /**
     * Ensures that the serial number inputted is not already used
     * @return true if unique; false otherwise
     */
    private boolean validateUniqueSerialNumber() {
        ArrayList<String> serialNumbers = database.getOtherSerialNumbersForPartName(nameField.getText(),"" + part.getPartID());
        for (String x : serialNumbers){
            System.out.println(x);
        }
        return serialNumbers.contains(serialField.getText());
    }

    /**
     * Ensures that the barcode inputted is not already used
     * @return true if unique; false otherwise
     */
    private boolean validateUniqueBarcode() {
        ArrayList<String> barcodes = database.getOtherBarcodesForPartName(nameField.getText(), "" + part.getPartID());
        return !barcodes.contains(barcodeField.getText());
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

    /**
     * This method throws an error that all of the parts that are the same type as the
     * one being edited have the same barcode, so the user should not edit one.
     * @param partName the name of the parts being edited
     */
    private void commonBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same barcode, so you cannot change one.");
        alert.showAndWait();
    }

    private void barcodeExistsError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("The barcode entered is already used by another part. Please use a different number");
        alert.showAndWait();
    }

    /**
     * This method throws an error that all of the parts that are the same type as the
     * one being edited have the same serial number, so the user should not edit one.
     * @param partName the name of the parts being edited
     */
    private void commonSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same serial number, so you cannot change one.");
        alert.showAndWait();
    }

    /**
     * This method throws an error that the edited part has the same serial number as
     * one of the ones with the same type.
     * @param partName the name of the type of part being edited
     */
    private void uniqueSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts must have unique serial numbers.");
        alert.showAndWait();
    }

    /**
     * This method throws an error that the edited part has the same barcode as
     * one of the ones with the same type.
     * @param partName the name of the type of part being edited
     */
    private void uniqueBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts must have unique barcodes.");
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
