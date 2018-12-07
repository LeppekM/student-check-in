package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoryTabTableRow extends RecursiveTreeObject<HistoryTabTableRow> {

    private StringProperty student;
    private StringProperty partName;
    private StringProperty serialNumber;
    private StringProperty location;
    private StringProperty quantity;
    private StringProperty date;

    public HistoryTabTableRow(String student, String partName, String serialNumber,
                              String location, String quantity, String date) {
        this.student = new SimpleStringProperty(student);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.quantity = new SimpleStringProperty(quantity);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getStudent() {
        return student;
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

    public StringProperty getQuantity() {
        return quantity;
    }

    public StringProperty getDate() {
        return date;
    }
}
