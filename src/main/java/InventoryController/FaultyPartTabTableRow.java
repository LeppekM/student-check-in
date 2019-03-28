package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

public class FaultyPartTabTableRow extends RecursiveTreeObject<FaultyPartTabTableRow> {

    private StringProperty partName, location, faultDescription, barcode;
    private IntegerProperty partID;



    public FaultyPartTabTableRow(int partID, String partName, String location,
                                 String barcode, String faultDescription) {
        this.partID = new SimpleIntegerProperty(partID);
        this.partName = new SimpleStringProperty(partName);

        this.location = new SimpleStringProperty(location);
        this.barcode = new SimpleStringProperty(barcode);
        this.faultDescription = new SimpleStringProperty(faultDescription);

    }

    public StringProperty getPartName() {
        return partName;
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
