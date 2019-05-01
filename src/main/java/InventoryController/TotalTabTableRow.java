package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty studentName, studentEmail, partName, partID, barcode, serialNumber, location,
            status, checkedOutAt, dueDate, fee, faultDescription;

    private boolean fault;

    public TotalTabTableRow(String studentName, String studentEmail, String partName, String partID,
                            String barcode, String serialNumber, String location, String status,
                            String checkedOutAt, String dueDate, boolean fault) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.partID = new SimpleStringProperty(partID);
        this.barcode = new SimpleStringProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.status = new SimpleStringProperty(status);
        this.checkedOutAt = new SimpleStringProperty(checkedOutAt);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.fault = fault;
    }

    public void initFaultDescription(String description) {
        this.faultDescription = new SimpleStringProperty(description);
    }

    public void initFee(String fee) {
        this.fee = new SimpleStringProperty(fee);
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

    public StringProperty getBarcode() {
        return barcode;
    }

    public StringProperty getSerialNumber() {
        return serialNumber;
    }

    public StringProperty getLocation() {
        return location;
    }

    public StringProperty getStatus() {
        return status;
    }

    public StringProperty getCheckedOutAt() {
        return checkedOutAt;
    }

    public StringProperty getDueDate() {
        return dueDate;
    }

    public StringProperty getFee() {
        return fee;
    }

    public StringProperty getFaultDescription() {
        return faultDescription;
    }

    public StringProperty getPartID() {
        return partID;
    }

    public boolean getIsFaulty() {
        return fault;
    }
}