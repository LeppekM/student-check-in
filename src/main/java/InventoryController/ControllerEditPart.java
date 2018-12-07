package InventoryController;

import Database.*;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import com.jfoenix.controls.JFXSpinner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
public class ControllerEditPart extends ControllerInventoryPage implements Initializable {
    @FXML
    private VBox sceneEditPart;

    @FXML
    private TextField nameField;

    @FXML
    private TextField serialField;

    @FXML
    private TextField manufacturerField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox vendorList;

    @FXML
    private TextField locationField;

    @FXML
    private TextField barcodeField;

    @FXML
    private TextField quantityField;

    @FXML
    private JFXSpinner loader;

    private Part part;

    private EditPart editPart = new EditPart();

    private VendorInformation vendorInformation = new VendorInformation();

    private int originalQuantity;

    /**
     * This method sets the data in the history page.
     * @param location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        part = null;
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    public void initPart(Part part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null && part != null) {
            this.part = part;
            nameField.setText(part.getPartName());
            serialField.setText(part.getSerialNumber());
            manufacturerField.setText(part.getManufacturer());
            quantityField.setText("" + part.getQuantity());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            priceField.setText("$" + df.format(part.getPrice()/100));
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
                vendorList.getItems().addAll(vendors);
            }
            vendorList.setValue(vendorInformation.getVendorFromID(part.getVendor()));
            locationField.setText(part.getLocation());
            barcodeField.setText(part.getBarcode());
            originalQuantity = part.getQuantity();
        }
    }

    /**
     * Edits the part in database
     */
    public void updateItem(){
        if (validateInput()) {
            loader.setVisible(true);
            editPart.editItem(getPartFromInput());
            partEditedSuccess();
            close();
            Notifications.create().title("Successful!").text("Part edited successfully.").hideAfter(new Duration(5000)).show();
        }
    }

    /**
     * Helper method that sets the part info from the user input
     */
    private Part getPartFromInput() {
        String partName = nameField.getText().trim();
        String serialNumber = serialField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();

        // Note: price multiplied by 100, because it is stored in the database as an integer 100 times
        // larger than actual value.
        double price = 100 * Double.parseDouble(priceField.getText().replaceAll(",", "").trim());
        String vendor = vendorList.getValue().toString();
        String location = locationField.getText().trim();
        String barcode = barcodeField.getText().trim();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        part.update(partName, serialNumber, manufacturer, price, vendor, location, barcode, quantity);
        return part;
    }

    /**
     * This method uses helper methods to ensure that he inputs for the part are valid.
     * @return true if the inputs are valid, false otherwise
     */
    private boolean validateInput() {
        boolean isValid = true;
        if (!validateAllFieldsFilledIn(nameField.getText(), serialField.getText(),
                manufacturerField.getText(),
                priceField.getText().replaceAll(",", ""),
                locationField.getText(),
                barcodeField.getText(), quantityField.getText())) {
            isValid = false;
            fieldErrorAlert();
        } else if (vendorList.getValue() == null) {
            isValid = false;
            nullVendorAlert();
        } else if (!validateNumberInputsContainNumber(priceField.getText()) ||
                !validateNumberInputsContainNumber(quantityField.getText())) {
            isValid = false;
            invalidNumberAlert();
        } else if (!validateNumberInputsWithinRange(priceField.getText(), quantityField.getText())) {
            isValid = false;
            numberOutOfRangeAlert();
        } else if (!validateQuantityIsGreaterThanPreviousValue(quantityField.getText())) {
            isValid = false;
            quantityTooSmallAlert();
        }
        return isValid;
    }

    /**
     * This method ensures that the inputted fields are not empty.
     * @return true if the fields are not empty, false otherwise
     */
    protected boolean validateAllFieldsFilledIn(String partName, String serialNumber,
                                                String manufacturer, String price,
                                                String location, String barcode, String quantity) {
        return partName != null && partName.trim() != ""
                && serialNumber != null && serialNumber.trim() != ""
                && manufacturer != null && manufacturer.trim() != ""
                && price != null && price.trim() != ""
                && location != null && location.trim() != ""
                && barcode != null && barcode.trim() != ""
                && quantity != null && quantity.trim() != "";
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
    protected boolean validateNumberInputsWithinRange(String price, String quantity) {
        // quantity is not checked to be greater than 0, because another test ensures
        // that quantity is greater than the unedited quantity
        return Double.parseDouble(price.replace(",", "")) >= 0
                && Double.parseDouble(price.replace(",", "")) < Double.MAX_VALUE
                && Double.parseDouble(quantity.replace(",", "")) < Double.MAX_VALUE;
    }

    /**
     * This method ensures that the quantity entered is greater than
     * the previous quantity
     * @return true if the quantiy entered is greater than the previous
     * quantity, false otherwise
     */
    protected boolean validateQuantityIsGreaterThanPreviousValue(String quantity) {
        return Double.parseDouble(quantity.replace(",", "")) >= originalQuantity;
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

    private void nullVendorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please select a vendor");
        alert.showAndWait();
    }

    /**
     * Creates an alert informing user to enter digits
     */
    private void invalidNumberAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering numbers into price and quantity fields");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user to enter numbers that are non-negative
     */
    private void numberOutOfRangeAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering numbers into price and quantity fields that are non-negative.");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user to enter a quantity greater than
     * or equal to the previous quantity
     */
    private void quantityTooSmallAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering a quantity that is greater than the previous quantity.");

        alert.showAndWait();
    }

    /**
     * Creates an alert informing user that part was edited successfully
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
        sceneEditPart.fireEvent(new WindowEvent(((Node) sceneEditPart).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
