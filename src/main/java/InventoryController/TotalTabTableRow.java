package InventoryController;

import Database.Database;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import javafx.css.SimpleStyleableIntegerProperty;

/**
 * Represents all of the info about parts on the total inventory tab
 */
public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty studentName, studentEmail, partName,  location,
            status, dueDate, faultDescription, className, professorName, serialNumber, fee;

    private IntegerProperty partID;
    private DoubleProperty price;
    private LongProperty barcode;

    private String actionType;

    private boolean fault;

    private String action;

    public TotalTabTableRow(int partID, long barcode, String serialNumber, String location, String partName, double price){
        this.partID = new SimpleIntegerProperty(partID);
        this.barcode = new SimpleLongProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.partName = new SimpleStringProperty(partName);
        this.price = new SimpleDoubleProperty(price);
    }

    public TotalTabTableRow(String studentName, String studentEmail, String partName, int partID,
                            long barcode, String serialNumber, String location, String status,
                            String checkedOutAt, String checkedInAt, String dueDate, String price, boolean fault, String className, String professorName) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.partID = new SimpleIntegerProperty(partID);
        this.barcode = new SimpleLongProperty(barcode);
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
            this.faultDescription = new SimpleStringProperty(database.getFaultDescription(partID));
        }
        this.fee = new SimpleStringProperty(price);
        this.className = new SimpleStringProperty(className);
        this.professorName = new SimpleStringProperty(professorName);
    }

    public DoubleProperty getPrice() {return price;}

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

    public LongProperty getBarcode() {
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

    public IntegerProperty getPartID() {
        return partID;
    }

    public boolean sFaulty() {
        return fault;
    }

    public String getActionType() {
        return actionType;
    }

    public StringProperty getClassName() {
        return className;
    }

    public StringProperty getProfessorName() {
        return professorName;
    }

}