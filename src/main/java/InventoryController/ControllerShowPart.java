package InventoryController;

import Database.ObjectClasses.Part;
import Database.VendorInformation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * This class acts as the controller for the information page for parts in the inventory
 */
public class ControllerShowPart extends ControllerInventoryPage implements Initializable {
    @FXML
    private VBox sceneShowPart;

    //TODO Add student to information

    @FXML
    private TextField nameField, serialField, manufacturerField, priceField, vendorList, locationField, barcodeField,
            quantityField;

    @FXML
    private Label nameLabel, serialLabel, manufacturerLabel, priceLabel, vendorLabel, locationLabel, barcodeLabel,
            quantityLabel;


    private Part part;

    private CheckedOutItems checkedOutPart;

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

        this.nameField.managedProperty().bind(this.nameField.visibleProperty());
        this.serialField.managedProperty().bind(this.serialField.visibleProperty());
        this.manufacturerField.managedProperty().bind(this.manufacturerField.visibleProperty());
        this.quantityField.managedProperty().bind(this.quantityField.visibleProperty());
        this.priceField.managedProperty().bind(this.priceField.visibleProperty());
        this.vendorList.managedProperty().bind(this.vendorList.visibleProperty());
        this.locationField.managedProperty().bind(this.locationField.visibleProperty());
        this.barcodeField.managedProperty().bind(this.barcodeField.visibleProperty());

        this.nameLabel.managedProperty().bind(this.nameLabel.visibleProperty());
        this.serialLabel.managedProperty().bind(this.serialLabel.visibleProperty());
        this.manufacturerLabel.managedProperty().bind(this.manufacturerLabel.visibleProperty());
        this.quantityLabel.managedProperty().bind(this.quantityLabel.visibleProperty());
        this.priceLabel.managedProperty().bind(this.priceLabel.visibleProperty());
        this.vendorLabel.managedProperty().bind(this.vendorLabel.visibleProperty());
        this.locationLabel.managedProperty().bind(this.locationLabel.visibleProperty());
        this.barcodeLabel.managedProperty().bind(this.barcodeLabel.visibleProperty());
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
            this.priceField.setText("$" + df.format(part.getPrice()));
            this.locationField.setText(part.getLocation());
            this.barcodeField.setText(part.getBarcode().toString());
        }
        determineVisibility(type);
    }

    /**
     * Helper method that sets the visibility of Nodes in the FXML
     */
    private void determineVisibility(String type){
        StudentCheckIn.logger.info("Showing part information of type: " + type);
        switch(type){
            case "total":
                setTotalElements();
                break;
            case "checkedOut":
                setCheckedOutElements();
                break;
            default:
                setElements("name", false);
                setElements("serial", false);
                setElements("manufacturer", false);
                setElements("quantity", false);
                setElements("price", false);
                setElements("vendor", false);
                setElements("location", false);
                setElements("barcode", false);
                this.nameLabel.setVisible(true);
                this.nameLabel.setText("Error: No info found. Please report \nthis to the developers.");
                StudentCheckIn.logger.error("No part type found when trying to show part info. Part type unknown or must be specified.");
                break;

        }
    }

    /**
     * Helper method to set visibility and position of UI elements based on the total information
     */
    public void setTotalElements(){
        setElements("name", true);
        setElements("serial", true);
        setElements("manufacturer", true);
        setElements("quantity", true);
        setElements("price", true);
        setElements("vendor", true);
        setElements("location", true);
        setElements("barcode", true);
    }

    /**
     * Helper method to set visibility and position of UI elements based on the checked out information
     */
    public void setCheckedOutElements(){
        setElements("name", true);
        setElements("serial", true);
        setElements("manufacturer", false);
        setElements("quantity", 175, 134, true);
        setElements("price", false);
        setElements("vendor", false);
        setElements("location", false);
        setElements("barcode", true);
    }


    /**
     * Helper method to set visibility and position of UI elements
     */
    public void setElements(String element, int x, int y, boolean visible){
        switch(element){
            case "name":
                this.nameField.setLayoutX(x);
                this.nameField.setLayoutY(y);
                this.nameLabel.setLayoutX(x);
                this.nameLabel.setLayoutY(y);
                this.nameField.setVisible(visible);
                this.nameLabel.setVisible(visible);
                break;
            case "serial":
                this.serialField.setLayoutX(x);
                this.serialField.setLayoutY(y);
                this.serialLabel.setLayoutX(x);
                this.serialLabel.setLayoutY(y);
                this.serialField.setVisible(visible);
                this.serialLabel.setVisible(visible);
                break;
            case "manufacturer":
                this.manufacturerLabel.setLayoutX(x);
                this.manufacturerLabel.setLayoutY(y);
                this.manufacturerField.setLayoutX(x);
                this.manufacturerField.setLayoutY(y);
                this.manufacturerField.setVisible(visible);
                this.manufacturerLabel.setVisible(visible);
                break;
            case "quantity":
                this.quantityField.setLayoutX(x);
                this.quantityField.setLayoutY(y);
                this.quantityLabel.setLayoutX(x - 100);
                this.quantityLabel.setLayoutY(y);
                this.quantityField.setVisible(visible);
                this.quantityLabel.setVisible(visible);
                break;
            case "price":
                this.priceLabel.setLayoutX(x - 75);
                this.priceLabel.setLayoutY(y);
                this.priceField.setLayoutX(x);
                this.priceField.setLayoutY(y);
                this.priceField.setVisible(visible);
                this.priceLabel.setVisible(visible);
                break;
            case "vendor":
                this.vendorLabel.setLayoutX(x);
                this.vendorLabel.setLayoutY(y);
                this.vendorList.setLayoutX(x);
                this.vendorList.setLayoutY(y);
                this.vendorList.setVisible(visible);
                this.vendorLabel.setVisible(visible);
                break;
            case "location":
                this.locationLabel.setLayoutX(x);
                this.locationLabel.setLayoutY(y);
                this.locationField.setVisible(visible);
                this.locationLabel.setVisible(visible);
                break;
            case "barcode":
                this.barcodeLabel.setLayoutX(x);
                this.barcodeLabel.setLayoutY(y);
                this.barcodeField.setVisible(visible);
                this.barcodeLabel.setVisible(visible);
                break;
        }
    }

    /**
     * Helper method to set visibility and position of UI elements
     */
    public void setElements(String element, boolean visible){
        switch(element){
            case "name":
                this.nameField.setVisible(visible);
                this.nameLabel.setVisible(visible);
                break;
            case "serial":
                this.serialField.setVisible(visible);
                this.serialLabel.setVisible(visible);
                break;
            case "manufacturer":
                this.manufacturerField.setVisible(visible);
                this.manufacturerLabel.setVisible(visible);
                break;
            case "quantity":
                this.quantityField.setVisible(visible);
                this.quantityLabel.setVisible(visible);
                break;
            case "price":
                this.priceField.setVisible(visible);
                this.priceLabel.setVisible(visible);
                break;
            case "vendor":
                this.vendorList.setVisible(visible);
                this.vendorLabel.setVisible(visible);
                break;
            case "location":
                this.locationField.setVisible(visible);
                this.locationLabel.setVisible(visible);
                break;
            case "barcode":
                this.barcodeField.setVisible(visible);
                this.barcodeLabel.setVisible(visible);
                break;
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
