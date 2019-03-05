package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FaultyTabTableRow extends RecursiveTreeObject<FaultyTabTableRow> {

    private StringProperty partName, serialNumber, location, barcode, faultDescription;
    private SimpleIntegerProperty partID;

    public FaultyTabTableRow(String partName, String serialNumber, String location,
                              String barcode, String faultDescription, int partID) {
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.faultDescription = new SimpleStringProperty(faultDescription);
        this.partID = new SimpleIntegerProperty(partID);
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

    public IntegerProperty getPartID() {
        return partID;
    }

}
