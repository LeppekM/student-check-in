package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HistoryTabTableRow extends RecursiveTreeObject<HistoryTabTableRow> {

    private StringProperty student;
    private StringProperty partName;
    private StringProperty serialNumber;
    private StringProperty status;
    private StringProperty date;

    public HistoryTabTableRow(String student, String partName, String serialNumber,
                              String status, String date) {
        this.student = new SimpleStringProperty(student);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.status = new SimpleStringProperty(status);
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

    public StringProperty getStatus() {
        return status;
    }

    public StringProperty getDate() {
        return date;
    }
}
