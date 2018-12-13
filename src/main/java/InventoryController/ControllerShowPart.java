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
     * Helper method that sets the part info from the user input
     */
    private Part getPart() {
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
