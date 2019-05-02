package InventoryController;

import Database.EditPart;
import Database.ObjectClasses.Part;
import Database.VendorInformation;
import HelperClasses.StageWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import com.jfoenix.controls.JFXSpinner;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
public class ControllerEditPartType extends ControllerEditPart {
    @FXML
    private VBox sceneEditPartType;

    @FXML
    private JFXTextField nameField, serialField, manufacturerField, priceField, vendorField,
            locationField, barcodeField;

    @FXML
    private JFXSpinner loader;

    @FXML
    private JFXButton saveButton;

    private Part part;

    private EditPart editPart = new EditPart();

    private VendorInformation vendorInformation = new VendorInformation();

    StageWrapper stageWrapper = new StageWrapper();

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

    private void disableFields() {
        serialField.setEditable(false);
        manufacturerField.setEditable(false);
        vendorField.setEditable(false);
    }

    private void setFieldValidator() {
        stageWrapper.requiredInputValidator(nameField);
        stageWrapper.acceptIntegerOnly(barcodeField);
        stageWrapper.requiredInputValidator(barcodeField);
        stageWrapper.requiredInputValidator(priceField);
        stageWrapper.requiredInputValidator(locationField);

        priceField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^\\$?[0-9]*\\.?[0-9]{0,2}$")) {
                    priceField.setText(oldValue);
                }
            }
        });
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    @Override
    public void initPart(Part part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null) {
            this.part = part;
            nameField.setText(part.getPartName());
            serialField.setText(part.getSerialNumber());
            manufacturerField.setText(part.getManufacturer());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            priceField.setText("$" + df.format(part.getPrice()/100));
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
                //vendorList.getItems().addAll(vendors);
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
            String originalPartName = part.getPartName();
            long originalBarcode = part.getBarcode();
            Part inputPart = updatePartFromInput();
            if (!database.hasUniqueBarcodes(originalPartName)) {
                if (!barcodeField.getText().equals(originalBarcode)) {
                    editPart.editAllOfType(originalPartName, inputPart);
                } else {
                    uniqueBarcodeError(originalPartName);
                }
            } else {
                editPart.editAllOfTypeCommonBarcode(originalPartName, inputPart);
            }
            close();
            partEditedSuccess();
        }
    }

    /**
     * Helper method that sets the part info from the user input
     */
    private Part updatePartFromInput() {
        String partName = "";
        if (nameField.getText() != null) {
            partName = nameField.getText().trim();
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
            barcode = Long.parseLong(barcodeField.getText().trim());
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
        if (!validateAllFieldsFilledIn(nameField.getText(),
                priceField.getText().replaceAll(",", ""),
                locationField.getText(),
                barcodeField.getText())) {
            isValid = false;
            fieldErrorAlert();
        } else {
            ArrayList<String> partNames = database.getUniquePartNames();
            if (!nameField.getText().equals(part.getPartName()) && partNames.contains(nameField.getText())) {
                isValid = false;
                uniquePartNameError();
            } else if (!barcodeField.getText().equals(part.getBarcode())) {
                if (database.hasUniqueBarcodes(part.getPartName())) {
                    isValid = false;
                    uniqueBarcodeError(part.getPartName());
                } else if (!validateUnusedBarcode(Integer.parseInt(barcodeField.getText()))) {
                    isValid = false;
                    unusedBarcodeError(Integer.parseInt(barcodeField.getText()));
                }
            }
        }
        return isValid;
    }

    /**
     * This method ensures that the inputted fields are not empty.
     * @return true if the fields are not empty, false otherwise
     */
    protected boolean validateAllFieldsFilledIn(String partName, String price,
                                                String location, String barcode) {
        return partName != null && !partName.trim().equals("")
                && price != null && !price.trim().equals("")
                && location != null && !location.trim().equals("")
                && barcode != null && !barcode.trim().equals("");
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

    private boolean validateUnusedBarcode(int barcode) {
        return database.getPartNameFromBarcode(barcode).equals("");
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

    private void uniquePartNameError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that name already exists.");
        alert.showAndWait();
    }

    private void uniqueBarcodeError(String partName) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(partName + " parts have unique barcodes, so you cannot change all of them.");
        alert.showAndWait();
    }

    private void unusedBarcodeError(int barcode) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("A part with that barcode already exists.");
        alert.showAndWait();
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
     * Creates an alert informing user to enter digits
     */
    private void invalidNumberAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Please make sure you are entering a number into the price field");

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
                    Notifications.create().title("Successful!").text("All " + part.getPartName() + " parts edited successfully.").hideAfter(new Duration(5000)).show();
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
        sceneEditPartType.fireEvent(new WindowEvent(((Node) sceneEditPartType).getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
