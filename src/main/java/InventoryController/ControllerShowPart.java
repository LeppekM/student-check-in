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
    private TextField nameField, serialField, manufacturerField, priceField, vendorList, locationField, barcodeField,
            quantityField, faultDescriptionField;

    @FXML
    private Label nameLabel, serialLabel, manufacturerLabel, priceLabel, vendorLabel, locationLabel, barcodeLabel,
            quantityLabel, faultDescriptionLabel, typeConfig;

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
        this.nameField.setEditable(false);
        this.serialField.setEditable(false);
        this.manufacturerField.setEditable(false);
        this.quantityField.setEditable(false);
        this.priceField.setEditable(false);
        this.vendorList.setEditable(false);
        this.locationField.setEditable(false);
        this.barcodeField.setEditable(false);
        this.faultDescriptionField.setEditable(false);

        this.nameField.managedProperty().bind(this.nameField.visibleProperty());
        this.serialField.managedProperty().bind(this.serialField.visibleProperty());
        this.manufacturerField.managedProperty().bind(this.manufacturerField.visibleProperty());
        this.quantityField.managedProperty().bind(this.quantityField.visibleProperty());
        this.priceField.managedProperty().bind(this.priceField.visibleProperty());
        this.vendorList.managedProperty().bind(this.vendorList.visibleProperty());
        this.locationField.managedProperty().bind(this.locationField.visibleProperty());
        this.barcodeField.managedProperty().bind(this.barcodeField.visibleProperty());
        this.faultDescriptionField.managedProperty().bind(this.faultDescriptionField.visibleProperty());

        this.nameLabel.managedProperty().bind(this.nameLabel.visibleProperty());
        this.serialLabel.managedProperty().bind(this.serialLabel.visibleProperty());
        this.manufacturerLabel.managedProperty().bind(this.manufacturerLabel.visibleProperty());
        this.quantityLabel.managedProperty().bind(this.quantityLabel.visibleProperty());
        this.priceLabel.managedProperty().bind(this.priceLabel.visibleProperty());
        this.vendorLabel.managedProperty().bind(this.vendorLabel.visibleProperty());
        this.locationLabel.managedProperty().bind(this.locationLabel.visibleProperty());
        this.barcodeLabel.managedProperty().bind(this.barcodeLabel.visibleProperty());
        this.faultDescriptionLabel.managedProperty().bind(this.faultDescriptionLabel.visibleProperty());


        this.type = this.typeConfig.getText();
        System.out.println(this.type);
        determineVisibility();

        if(this.type.equals("none")){
            this.nameLabel.setText("Error: No info found. Please report \nthis to the developers.");
        }
    }

    /**
     * This method is used to pass data into the tab to initialize the text representing the edited part
     * @param part
     */
    public void initPart(Part part, String type) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        if (this.part == null && part != null) {
            this.part = part;
            this.nameField.setText(part.getPartName());
            this.serialField.setText(part.getSerialNumber());
            this.manufacturerField.setText(part.getManufacturer());
            this.quantityField.setText("" + part.getQuantity());

            // Note: price divided by 100, because it is stored in the database as an integer 100 times
            // larger than actual value.
            this.priceField.setText("$" + df.format(part.getPrice()/100));
            this.vendorList.setText(vendorInformation.getVendorFromID(part.getVendor()));
            this.locationField.setText(part.getLocation());
            this.barcodeField.setText(part.getBarcode());
            this.faultDescriptionField.setText(part.getFaultDesc());
        }
        this.typeConfig.setText(type);
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
            this.quantityField.setText("" + part.getQuantity());
        }
        this.typeConfig.setText("checkedOut");
    }

    /**
     * Helper method that sets the visibility of Nodes in the FXML
     */
    private void determineVisibility(){
        switch(this.type){
            case "total":
                this.nameField.setVisible(true);
                this.nameLabel.setVisible(true);
                this.serialField.setVisible(true);
                this.serialLabel.setVisible(true);
                this.manufacturerField.setVisible(true);
                this.manufacturerLabel.setVisible(true);
                this.quantityField.setVisible(true);
                this.quantityLabel.setVisible(true);
                this.priceField.setVisible(true);
                this.priceLabel.setVisible(true);
                this.vendorList.setVisible(true);
                this.vendorLabel.setVisible(true);
                this.locationField.setVisible(true);
                this.locationLabel.setVisible(true);
                this.faultDescriptionField.setVisible(false);
                this.faultDescriptionLabel.setVisible(false);
                this.barcodeField.setVisible(true);
                this.barcodeLabel.setVisible(true);
                break;
            case "fault":
                this.nameField.setVisible(true);
                this.nameLabel.setVisible(true);
                this.serialField.setVisible(true);
                this.serialLabel.setVisible(true);
                this.manufacturerField.setVisible(true);
                this.manufacturerLabel.setVisible(true);
                this.quantityField.setVisible(true);
                this.quantityLabel.setVisible(true);
                this.priceField.setVisible(true);
                this.priceLabel.setVisible(true);
                this.vendorList.setVisible(true);
                this.vendorLabel.setVisible(true);
                this.locationField.setVisible(false);
                this.locationLabel.setVisible(false);
                this.faultDescriptionField.setVisible(false);
                this.faultDescriptionLabel.setVisible(false);
                this.barcodeField.setVisible(true);
                this.barcodeLabel.setVisible(true);
                break;
            case "checkedOut":
                this.nameField.setVisible(true);
                this.nameLabel.setVisible(true);
                this.serialField.setVisible(true);
                this.serialLabel.setVisible(true);
                this.manufacturerField.setVisible(false);
                this.manufacturerLabel.setVisible(false);
                this.quantityField.setVisible(true);
                this.quantityLabel.setVisible(true);
                this.priceField.setVisible(false);
                this.priceLabel.setVisible(false);
                this.vendorList.setVisible(false);
                this.vendorLabel.setVisible(false);
                this.locationField.setVisible(true);
                this.locationLabel.setVisible(true);
                this.faultDescriptionField.setVisible(false);
                this.faultDescriptionLabel.setVisible(false);
                this.barcodeField.setVisible(true);
                this.barcodeLabel.setVisible(true);
                break;
            default:
                this.nameField.setVisible(false);
                this.nameLabel.setVisible(true);
                this.serialField.setVisible(false);
                this.serialLabel.setVisible(false);
                this.manufacturerField.setVisible(false);
                this.manufacturerLabel.setVisible(false);
                this.quantityField.setVisible(false);
                this.quantityLabel.setVisible(false);
                this.priceField.setVisible(false);
                this.priceLabel.setVisible(false);
                this.vendorList.setVisible(false);
                this.vendorLabel.setVisible(false);
                this.locationField.setVisible(false);
                this.locationLabel.setVisible(false);
                this.faultDescriptionField.setVisible(false);
                this.faultDescriptionLabel.setVisible(false);
                this.barcodeField.setVisible(false);
                this.barcodeLabel.setVisible(false);
                this.type = "none";

        }
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
