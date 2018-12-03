package InventoryController;

import Database.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAddPart extends ControllerInventoryPage implements Initializable {
    @FXML
    public VBox sceneAddPart;

    @FXML
    public TextField nameField;

    @FXML
    public TextField serialField;

    @FXML
    public TextField manufacturerField;

    @FXML
    public TextField quantityField;

    @FXML
    public TextField priceField;

    @FXML
    public TextField vendorField;

    @FXML
    public TextField locationField;

    AddPart addPart = new AddPart();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    /**
     * Adds the part to database
     */
    public boolean submitItem(){
        if(validateFieldsNotEmpty() && validateQuantityField() && validatePriceField()){
        setPartFields();
        addPart.addItem(setPartFields());
        Notifications.create().title("Successful!").text("Part added successfully.").hideAfter(new Duration(5000)).show();//.showWarning();
//        partAddedSuccess();
        close();
        return true;
        }
        else {
            errorHandler();
            return false;
        }
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
        String vendor = vendorField.getText();
        String location = locationField.getText();
        String barcode = serialField.getText();
        String quantity = quantityField.getText();
        int isDeleted = 0; //Part won't ever be deleted when adding
        //If the price or quantity isn't filled out, the invalid value -1 is passed instead.
        if(price.isEmpty()){
            price = failedCheck;
        }
        if (quantity.isEmpty()){
            quantity = failedCheck;
        }

        return new Part(partname, serialNumber, manufacturer, priceCheck(price), vendor, location, barcode, quantityCheck(quantity), isDeleted);
    }


    /**
     * Checks if the input entered is a double
     * @param price The double to be returned. Returns -1 if not a double, and the value otherwise
     * @return
     */
    public double priceCheck(String price){
        double positivePriceCheck;
        double failedvalue = -1;
        try {
            positivePriceCheck = Double.valueOf(price); //If price is a valid double
        }
        catch (Exception e){
            return failedvalue;
        }
        if(positivePriceCheck > 0){ //If price is greater than 0
            return positivePriceCheck;
        }
        return failedvalue;
    }

    /**
     * Checks if the input entered is an integer
     * @param quantity The integer to be returned. Returns -1 if not an int, and the value otherwise
     * @return
     */
    public int quantityCheck(String quantity){
        int positiveCheck;
        int failedValue = -1;
        if(quantity.chars().allMatch(Character::isDigit)){ //If quantity is a valid int
             positiveCheck = Integer.parseInt(quantity);
            if (positiveCheck >0){ //If quantity is greater than 0
                return positiveCheck;
            }
        }
        else {
            return failedValue;
        }
        return failedValue;
    }

    /**
     * Determines if the price textfield input is valid or not
     * @return True if the price is valid
     */
    private boolean validatePriceField(){
        if(priceCheck(priceField.getText())==-1){
            return false;
        }
        return true;
    }

    /**
     * Determines if the quantity textfield input is valid or not
     * @return True if the quantity is valid
     */
    private boolean validateQuantityField(){
        if(quantityCheck(quantityField.getText())==-1){
            return false;
        }
        return true;
    }

    /**
     * This checks to see if the textfields are empty
     * @return False if any field is empty
     */
    private boolean validateFieldsNotEmpty(){
        if(nameField.getText().isEmpty() | serialField.getText().isEmpty() | manufacturerField.getText().isEmpty() |
        priceField.getText().isEmpty() | vendorField.getText().isEmpty() | locationField.getText().isEmpty()|
        serialField.getText().isEmpty()| quantityField.getText().isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Helper method to select correct error dialog based on what went wrong
     */
    private void errorHandler(){
        if(!validateFieldsNotEmpty()){
            fieldErrorAlert();
        }
        else if (!validateQuantityField()|| !validatePriceField()){
            invalidNumberAlert();
        }
    }


    /**
     * Creates an alert informing user to fill out all fields
     */
    private void fieldErrorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please fill out all fields before submitting info");

        alert.showAndWait();
    }

    /**
     * Creates alert that informs user invalid input was entered into price or quantity field
     */
    private void invalidNumberAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering numbers into price and quantity fields, and that they are not negative");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user that part was added successfully
     */
    private void partAddedSuccess(){
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Confirmation");
//        alert.setContentText("Part added successfully");
//        alert.showAndWait();
    }

    /**
     * Returns to main inventory page
     */
    public void goBack(){
        close();
    }

    /**
     * Helper method to close platform
     */
    private void close(){
        sceneAddPart.getScene().getWindow().hide();
    }





}
