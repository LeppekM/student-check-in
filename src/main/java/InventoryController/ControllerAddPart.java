package InventoryController;

import Database.AddPart;
import Database.ObjectClasses.Part;
import Database.VendorInformation;
import HelperClasses.StageUtils;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerAddPart extends ControllerInventoryPage implements Initializable {
    @FXML
    public VBox sceneAddPart;

    @FXML
    public JFXTextField nameField, serialField, manufacturerField, quantityField, barcodeField, priceField, locationField;

    @FXML
    public JFXComboBox vendorField;

    @FXML
    public JFXSpinner loadNotification;

    AddPart addPart = new AddPart();

    VendorInformation vendorInformation = new VendorInformation();
    private ArrayList <String> vendors = vendorInformation.getVendorList();

    private StageUtils stageUtils = StageUtils.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showVendors();
        stageUtils.acceptIntegerOnly(barcodeField);
        setFieldValidator();

        // make sure that the price field only accepts a valid price
        priceField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^\\$?[0-9]*\\.?[0-9]{0,2}$")) {
                    priceField.setText(oldValue);
                }
            }
        });
    }

    private void setFieldValidator() {
        stageUtils.acceptIntegerOnly(barcodeField);
        stageUtils.acceptIntegerOnly(quantityField);
    }

    /**
     * Adds the part to database
     */
    public boolean submitItem(){
        if(validateFieldsNotEmpty() && validateQuantityField() && validatePriceField()){
            if(!vendorExists(getVendorName())){
                vendorInformation.createNewVendor(getVendorName(), vendorInformation());
            }
        submitTasks();
        return true;
        } else {
            errorHandler();
            return false;
        }
    }

    /**
     * Helper method that runs when adding a new part
     */
    private void submitTasks(){
        String partName = nameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        if (database.hasPartName(partName)) {
            Part existing = database.selectPartByPartName(setPartFields().getPartName());
            if (quantity > 1) {
                if (barcodeField.getText().equals(existing.getBarcode())) {
                    mustBeCommonBarcodeError(partName);
                } else if (duplicateBarcode(partName, Integer.parseInt(barcodeField.getText()))) {
                    barcodeAlreadyExistsError();
                } else if (serialField.getText().equals(existing.getSerialNumber())) {
                    mustBeCommonSerialNumberError(partName);
                }
                else {
                    addPart.addCommonItems(setPartFields(), database, quantity);
                    partAddedSuccess();
                    close();
                }
            } else {
                if (!database.hasUniqueBarcodes(partName) && (barcodeField.getText().equals(existing.getBarcode()) || serialField.getText().equals(existing.getSerialNumber()))) {
                    barcodeAndSerialNumberMustBothBeUniqueOrCommonError();
                } else {
                    if (database.countPartsOfType(partName) == 1) {
                        if (barcodeField.getText().equals(existing.getBarcode()) && (
                                !serialField.getText().equals(existing.getSerialNumber()))) {
                            commonBarcodeRequiresCommonSerialNumberError(partName);
                        } else if (serialField.getText().equals(existing.getSerialNumber()) &&
                                !barcodeField.getText().equals(existing.getBarcode())) {
                            commonSerialNumberRequiresCommonBarcodeError(partName);
                        }
                    } else {
                        addPart.addUniqueItems(setPartFields(), database, quantity);
                        partAddedSuccess();
                        close();
                    }
                }
            }
        } else {
            //db does not have a part with this name, add it unless duplicate part
            if (quantity > 1) {
                if (!duplicateBarcode(partName, Integer.parseInt(barcodeField.getText()))) {
                    addPart.addCommonItems(setPartFields(), database, quantity);
                    partAddedSuccess();
                    close();
                } else {
                    barcodeAlreadyExistsError();
                }
            } else {
                if (!duplicateBarcode(partName, Long.parseLong(barcodeField.getText()))) {
                    addPart.addUniqueItems(setPartFields(), database, quantity);
                    partAddedSuccess();
                    close();
                } else {
                    barcodeAlreadyExistsError();
                }
            }

        }
    }

    public boolean duplicateBarcode(String partName, long barcode) {
        return database.getUniqueBarcodesBesidesPart(partName).contains("" + barcode);
    }

    /**
     * If new vendor is created, this popup asks for vendor information
     * Currently nothing is done with the result of the text
     */
    private String vendorInformation(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vendor Information");
        dialog.setHeaderText("New vendor created, please enter vendor information");
        dialog.setContentText("Please enter vendor information");
        dialog.showAndWait();
        return dialog.getResult();
    }

    /**
     * Helper method to get vendor selection
     * @return Vendor name
     */
    private String getVendorName(){
        String failedCheck = "-1";
        if(vendorField.getValue() != null) {
            return vendorField.getValue().toString();
        }
        return failedCheck;
    }

    /**
     * Helper method to show vendors
     */
    private void showVendors(){
        vendorField.getItems().addAll(vendors);
    }

    /**
     * Checks to see if vendor is new
     * @param vendorName Name to be checked against list of vendors
     * @return True if vendor name is new
     */
    private boolean vendorExists(String vendorName){
        return vendors.contains(vendorName);
    }


    /**
     * Helper method that sets all the part information that will be added to the database
     * @return The part to be added to database
     */
    private Part setPartFields(){
        String failedCheck = "-1";

        String partname = nameField.getText();
        String serialNumber = serialField.getText();
        String manufacturer = manufacturerField.getText();
        String price = priceField.getText();
        String vendor = getVendorName();
        String location = locationField.getText();
        long barcode = Long.parseLong(barcodeField.getText());
        String quantity = quantityField.getText();
        //If the price or quantity isn't filled out, the invalid value -1 is passed instead.
        if(price.isEmpty()){
            price = failedCheck;
        }
        if (quantity.isEmpty()){
            quantity = failedCheck;
        }

        return new Part(partname, serialNumber, manufacturer, priceCheck(price), vendor, location, barcode, quantityCheck(quantity));
    }




    /**
     * Checks if the input entered is a double
     * @param price The double to be checked
     * @return the price as a double if it is valid, -1 if it is not a positive double
     */
    public double priceCheck(String price){
        double positivePriceCheck;
        double failedValue = -1;
        try {
            positivePriceCheck = Double.parseDouble(price); //If price is a valid double
        }
        catch (Exception e){
            return failedValue;
        }
        if(positivePriceCheck > 0){ //If price is greater than 0
            return positivePriceCheck;
        }
        return failedValue;
    }

    /**
     * Checks if the input entered is an integer
     * @param quantity The integer to be returned.
     * @return -1 if not an int, and the value otherwise
     */
    public int quantityCheck(String quantity){
        int positiveCheck;
        int failedValue = -1;
        if(quantity.chars().allMatch(Character::isDigit)){ //If quantity is a valid int
             positiveCheck = Integer.parseInt(quantity);
            if (positiveCheck > 0){ //If quantity is greater than 0
                return positiveCheck;
            }
        }
        else {
            return failedValue;
        }
        return failedValue;
    }

    /**
     * Determines if the price textField input is valid or not
     * @return True if the price is valid
     */
    private boolean validatePriceField(){
        return priceCheck(priceField.getText()) != -1;
    }

    /**
     * Determines if the quantity textField input is valid or not
     * @return True if the quantity is valid
     */
    private boolean validateQuantityField(){
        return quantityCheck(quantityField.getText()) != -1;
    }

    /**
     * This checks to see if the textFields are empty
     * @return False if any field is empty
     */
    private boolean validateFieldsNotEmpty(){
        return !(nameField.getText().isEmpty() | serialField.getText().isEmpty() | manufacturerField.getText().isEmpty() |
                priceField.getText().isEmpty() | locationField.getText().isEmpty() |
                serialField.getText().isEmpty() | barcodeField.getText().isEmpty() | quantityField.getText().isEmpty() | getVendorName().contains("-1"));
    }

    /**
     * Helper method to select correct error dialog based on what went wrong
     */
    private void errorHandler(){
        if(!validateFieldsNotEmpty()){
            fieldErrorAlert();
        }
        else if (!validateQuantityField()){
            invalidNumberAlert();
        }
    }


    /**
     * Creates an alert informing user to fill out all fields
     */
    private void fieldErrorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please fill out all fields before submitting info.");

        alert.showAndWait();
    }

    private void barcodeAlreadyExistsError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that barcode already exists.");

        alert.showAndWait();
    }

    private void barcodeAndSerialNumberMustBothBeUniqueOrCommonError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Barcodes and serial numbers for parts must be all the same or all different.");

        alert.showAndWait();
    }

    private void commonBarcodeRequiresCommonSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same barcode, so the serial number must be the same.");

        alert.showAndWait();
    }

    private void commonSerialNumberRequiresCommonBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same serial number, so the barcode must be the same.");

        alert.showAndWait();
    }

    /**
     * Creates alert that informs user invalid input was entered into price or quantity field
     */
    private void invalidNumberAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering numbers into price and quantity fields, and that they are not negative");
        StudentCheckIn.logger.error("Please make sure you are entering numbers into price and quantity fields, and that they are not negative.");
        alert.showAndWait();
    }

    private void mustBeCommonBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("All " + partName + " parts must have the same barcode.");
        StudentCheckIn.logger.error("All " + partName + " parts must have the same barcode.");
        alert.showAndWait();
    }

    private void mustBeCommonSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("All " + partName + " parts must have the same serial number.");
        StudentCheckIn.logger.error("All {} parts must have the same serial number.", partName);
        alert.showAndWait();
    }
    private void mustBeCommonBarcodeAndSerialNumberError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("All " + partName + " parts must have the same barcode and serial number.");
        StudentCheckIn.logger.error("All {} parts must have the same barcode and serial number.", partName);
        alert.showAndWait();
    }
    private void commonFieldsError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have the same barcode and serial number, so their other fields must also be the same.");
        StudentCheckIn.logger.error("{} parts have the same barcode and serial number, so their other fields must also be the same.", partName);
        alert.showAndWait();
    }

    /**
     * Creates an alert informing user that part was added successfully
     */
    private void partAddedSuccess(){
        stageUtils.successAlert("Part added successfully.");
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
        sceneAddPart.fireEvent(new WindowEvent(sceneAddPart.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
