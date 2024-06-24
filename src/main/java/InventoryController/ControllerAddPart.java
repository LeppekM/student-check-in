package InventoryController;

import Database.ObjectClasses.Part;
import Database.VendorInformation;
import HelperClasses.StageUtils;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ControllerAddPart extends ControllerInventoryPage implements Initializable {
    @FXML
    public VBox sceneAddPart;

    @FXML
    public JFXTextField nameField, serialField, manufacturerField, quantityField, barcodeField, priceField, locationField, suffixField;

    @FXML
    public JFXComboBox vendorField;

    @FXML
    public JFXSpinner loadNotification;

    @FXML
    public JFXCheckBox differentBarcodes; // hides barcode field when checked

    VendorInformation vendorInformation = new VendorInformation();
    private ArrayList <String> vendors = vendorInformation.getVendorList();

    private StageUtils stageUtils = StageUtils.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showVendors();
        stageUtils.acceptIntegerOnly(barcodeField);
        setFieldValidator();

        // make sure that the price field only accepts a valid price
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^\\$?[0-9]*\\.?[0-9]{0,2}$")) {
                priceField.setText(oldValue);
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
    private void submitTasks(){  // todo: make sure that the validation fields are correct
        long barcode = Long.parseLong(barcodeField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        String partName = nameField.getText();

        if (quantity > 1) {
            if (differentBarcodes.isSelected()) {
                if (database.partNameExists(partName)) {
                    // check that entered/starting serial number isn't the same as an existing for part name
                    if (checkExistingSNAgainstGenerated(quantity, partName)) {
                        addManyPartsWithDifferentBarcodes(quantity, partName);
                        partAddedSuccess(quantity);
                        close();
                    }
                } else {
                    addManyPartsWithDifferentBarcodes(quantity, partName);
                    partAddedSuccess(quantity);
                    close();

                }
            } else {
                List<String> barcodesWithSamePartName = database.getAllBarcodesForPartName(partName).stream().distinct().collect(Collectors.toList());
                if ((barcodesWithSamePartName.size() == 1 && Long.parseLong(barcodesWithSamePartName.get(0)) == barcode)
                        || barcodesWithSamePartName.isEmpty()) {
                    // adds many with same barcodes, and serial num & other fields don't matter
                    for (int i = 0; i < quantity; i++) {
                        database.addPart(new Part(partName, serialField.getText(), manufacturerField.getText(), Double.parseDouble(priceField.getText()), getVendorName(), locationField.getText(), barcode));
                    }
                    partAddedSuccess(quantity);
                    close();
                } else {
                    stageUtils.errorAlert("This part name already has different barcodes for each part");
                }
            }
        } else if (quantity == 1) {
            if (database.partNameExists(partName)) {
                if (database.getAllBarcodesForPartName(partName).stream().distinct().count() == 1) {
                    // Same names, same barcodes, serial does not need checked
                    database.addPart(new Part(partName, serialField.getText(), manufacturerField.getText(), Double.parseDouble(priceField.getText()), getVendorName(), locationField.getText(), barcode));
                    partAddedSuccess(1);
                    close();
                } else {
                    // Same names, different barcodes, serial number should be different
                    if (database.getAllSerialNumbersForPartName(partName).contains(serialField.getText())) {
                        stageUtils.errorAlert("This serial number is already used for part of this name");
                    } else {
                        database.addPart(new Part(partName, serialField.getText(), manufacturerField.getText(), Double.parseDouble(priceField.getText()), getVendorName(), locationField.getText(), barcode));
                        partAddedSuccess(1);
                        close();
                    }
                }
            } else {
                if (database.barcodeExists(barcode)) {
                    stageUtils.errorAlert("Barcode is already being used for a different part name");
                } else {
                    database.addPart(new Part(partName, serialField.getText(), manufacturerField.getText(), Double.parseDouble(priceField.getText()), getVendorName(), locationField.getText(), barcode));
                    partAddedSuccess(1);
                    close();
                }
            }
        }
    }

    /**
     * Adds parts with different serial numbers and barcodes
     * @param quantity number of parts being added
     */
    private void addManyPartsWithDifferentBarcodes(int quantity, String partName) {
        int currentSN = serialField.getText().isEmpty()? 1: Integer.parseInt(serialField.getText());
        long currentBarcode = database.getMaxPartID() + 1;  // gets max part
        String suffix = suffixField.getText();
        ArrayList<String> serialNums = database.getAllSerialNumbersForPartName(partName);
        for (int i = 0; i < quantity; i++) {
            while (serialNums.contains(currentSN + suffix)) {
                currentSN++;
            }
            database.addPart(new Part(partName, currentSN + suffix, manufacturerField.getText(), Double.parseDouble(priceField.getText()), getVendorName(), locationField.getText(), currentBarcode));
            currentBarcode++;
            currentSN++;
        }
    }

    /**
     * This method returns true if no overlapping serial numbers exist indexing from the starting number or if the user
     * allows indexing from next available serial number
     * @return false if overlapping serial numbers are found and the user doesn't allow indexing from nex available sn
     */
    private boolean checkExistingSNAgainstGenerated(int quantity, String partName) {
        int startingSN = serialField.getText().isEmpty()? 1: Integer.parseInt(serialField.getText());
        int currentSN = startingSN;
        String suffix = suffixField.getText();
        ArrayList<String> serialNums = database.getAllSerialNumbersForPartName(partName);
        while (currentSN < quantity + startingSN) {
            if (serialNums.contains(currentSN + suffix)) {
                if (!stageUtils.confirmationAlert("Not enough serial numbers in sequence", "When adding " + quantity +
                        " parts of " + partName + " with the starting serial number of " + serialField.getText() +
                        suffixField.getText() + " the serial number overlaps with existing parts starting with " +
                        currentSN + suffix + "\nIf the operation continues, more serial numbers will be generated " +
                        "from the next available lowest number.")) {
                    return false;
                } else {
                    return true;
                }
            }
            currentSN++;
        }
        return true;
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

        return new Part(partname, serialNumber, manufacturer, priceCheck(price), vendor, location, barcode);
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
        stageUtils.errorAlert("Please fill out all fields before submitting info.");
    }

    /**
     * Creates alert that informs user invalid input was entered into price or quantity field
     */
    private void invalidNumberAlert(){
        stageUtils.errorAlert("Please make sure you are entering non-negative numbers into price and quantity fields");
        StudentCheckIn.logger.error("Please make sure you are entering non-negative numbers into price and quantity fields");
    }

    /**
     * Creates an alert informing user that part was added successfully
     */
    private void partAddedSuccess(int num){
        stageUtils.successAlert(num == 1? "Part added successfully." : num + " parts added successfully.");
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
