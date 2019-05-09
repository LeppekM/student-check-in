package InventoryController;

import Database.Database;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date;

public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty studentName, studentEmail, partName, partID, barcode, serialNumber, location,
            status, dueDate, fee, faultDescription;

    private String actionType;

    private boolean fault;

    private String action;

    public TotalTabTableRow(String studentName, String studentEmail, String partName, String partID,
                            String barcode, String serialNumber, String location, String status,
                            String checkedOutAt, String checkedInAt, String dueDate, boolean fault) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.partID = new SimpleStringProperty(partID);
        this.barcode = new SimpleStringProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.status = new SimpleStringProperty(status);
        if (checkedInAt != null) {
            action = checkedInAt;
            actionType = "Check In";
        } else {
            action = checkedOutAt;
            actionType = "Check Out";
        }
        this.dueDate = new SimpleStringProperty(dueDate);
        this.fault = fault;
        if (this.fault) {
            Database database = new Database();
            this.faultDescription = new SimpleStringProperty(database.getFaultDescription(Integer.parseInt(partID)));
        }
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

    public String getAction() {
        return action;
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

    public boolean sFaulty() {
        return fault;
    }

    public String getActionType() {
        return actionType;
    }
}