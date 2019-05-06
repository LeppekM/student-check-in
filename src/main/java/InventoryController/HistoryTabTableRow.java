package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the info displayed in a row on the history tab of the inventory
 */
public class HistoryTabTableRow extends RecursiveTreeObject<HistoryTabTableRow> {

    private StringProperty studentName;
    private StringProperty studentEmail;
    private StringProperty partName;
    private StringProperty serialNumber;
    private StringProperty status;
    private StringProperty date;

    public HistoryTabTableRow(String studentName, String studentEmail, String partName, String serialNumber,
                              String status, String date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty getStudentName() {
        return studentName;
    }

    public StringProperty getStudentEmail() {
        return studentEmail;
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
