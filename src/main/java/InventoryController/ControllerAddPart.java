package InventoryController;

import Database.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
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
    public TextField nameField;

    @FXML
    public TextField serialField;

    @FXML
    public TextField manufacturerField;

    @FXML
    public TextField quantityField;

    @FXML
    public TextField barcodeField;

    @FXML
    public TextField priceField;

    @FXML
    public JFXComboBox vendorField;

    @FXML
    public TextField locationField;

    @FXML
    public JFXSpinner loadNotification;

    AddPart addPart = new AddPart();

    VendorInformation vendorInformation = new VendorInformation();
    private ArrayList <String> vendors = vendorInformation.getVendorList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showVendors();
    }

    /**
     * Adds the part to database
     */
    public boolean submitItem(){
        if(validateFieldsNotEmpty() && validateQuantityField() && validatePriceField()){
            if(!vendorExists(getVendorName())){
                vendorInformation.createNewVendor(getVendorName());
                vendorInformation();
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
        setPartFields();
        addPart.addItem(setPartFields());
        partAddedSuccess();
        close();
    }

    /**
     * If new vendor is created, this popup asks for vendor information
     * Currently nothing is done with the result of the text
     */
    private void vendorInformation(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Vendor Information");
        dialog.setHeaderText("New vendor created, please enter vendor information");
        dialog.setContentText("Please enter vendor information");
        dialog.showAndWait();
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
        String barcode = barcodeField.getText();
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
        priceField.getText().isEmpty() | locationField.getText().isEmpty()|
        serialField.getText().isEmpty()| barcodeField.getText().isEmpty()| quantityField.getText().isEmpty() | getVendorName().contains("-1")){
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
                    Notifications.create().title("Successful!").text("Part added successfully.").hideAfter(new Duration(5000)).show();
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
        sceneAddPart.fireEvent(new WindowEvent(((Node) sceneAddPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
