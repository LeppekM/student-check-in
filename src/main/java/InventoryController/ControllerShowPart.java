package InventoryController;

import Database.EditPart;
import Database.Part;
import Database.VendorInformation;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the information page for parts in the inventory
 */
public class ControllerShowPart extends ControllerInventoryPage implements Initializable {
    @FXML
    private VBox sceneShowPart;

    @FXML
    private TextField nameField, serialField, manufacturerField, priceField, locationField, barcodeField,
            quantityField, faultDescriptionField;

    @FXML
    private Label nameLabel, serialLabel, manufacturerLabel, priceLabel, vendorLabel, locationLabel, barcodeLabel,
            quantityLabel, faultDescriptionLabel, typeConfig;

    @FXML
    private ComboBox vendorList;

    private Part part;

    private CheckedOutItems checkedOutPart;

    private String type;

    private VendorInformation vendorInformation = new VendorInformation();

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
    public void initPart(Part part, String type) {
        this.type = type;
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null && part != null) {
            this.part = part;
            this.nameField.setText(part.getPartName());
            this.serialField.setText(part.getSerialNumber());
            this.manufacturerField.setText(part.getManufacturer());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            this.priceField.setText("$" + df.format(part.getPrice()/100));
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
                this.vendorList.getItems().addAll(vendors);
            }
            this.vendorList.setValue(vendorInformation.getVendorFromID(part.getVendor()));
            this.locationField.setText(part.getLocation());
            this.barcodeField.setText(part.getBarcode());
        }
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    public void initPart(CheckedOutItems part) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        this.checkedOutPart = part;
        if (this.part == null && part != null) {
            this.nameField.setText(part.getPartName().toString());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            ArrayList<String> vendors = vendorInformation.getVendorList();
            if (vendors != null) {
                this.vendorList.getItems().addAll(vendors);
            }
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
        part.update(partName, serialNumber, manufacturer, price, vendor, location, barcode);
        return part;
    }

    /**
     * Helper method that sets the part info from the user input
     */
    private Part getPart() {
        return part;
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
        sceneShowPart.getScene().getWindow().hide();
    }


}
