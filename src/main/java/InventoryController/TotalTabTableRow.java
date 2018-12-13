package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty partName, serialNumber, location, barcode, partID;

    private HBox buttons;

    private boolean fault;

    public TotalTabTableRow(String partName, HBox buttons, String serialNumber, String location,
                            String barcode, boolean fault, String partID) {
        this.partName = new SimpleStringProperty(partName);
        this.buttons = buttons;

        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.fault = fault;
        this.partID = new SimpleStringProperty(partID);
    }

    public StringProperty getPartName() {
        return partName;
    }

    public HBox getButtons() {
        return buttons;
    }

    public StringProperty getSerialNumber() {
        return serialNumber;
    }

    public StringProperty getLocation() {
        return location;
    }

    public StringProperty getBarcode() {
        return barcode;
    }

    public boolean getFault() {
        return fault;
    }

    public StringProperty getPartID() {
        return partID;
    }

}