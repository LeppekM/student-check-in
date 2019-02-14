package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FaultyTabTableRow extends RecursiveTreeObject<FaultyTabTableRow> {

    private StringProperty partName;
    private StringProperty serialNumber;
    private StringProperty location;
    private StringProperty barcode;
    private StringProperty faultDescription;

    public FaultyTabTableRow(String partName, String serialNumber, String location,
                              String barcode, String faultDescription) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.faultDescription = new SimpleStringProperty(faultDescription);
    }

    public StringProperty getPartName() {
        return partName;
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

    public StringProperty getFaultDescription() {
        return faultDescription;
    }

}
