package InventoryController;

import Database.Database;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

/**
 * Represents all of the info about parts on the total inventory tab
 */
public class TotalTabTableRow extends RecursiveTreeObject<TotalTabTableRow> {

    private StringProperty studentName, studentEmail, partName,  location,
            status, dueDate, className, professorName, serialNumber, fee;

    private IntegerProperty partID;
    private DoubleProperty price;
    private LongProperty barcode;

    private String actionType;

    private String action;

    public TotalTabTableRow(int partID, long barcode, String serialNumber, String location, String partName, double price){
        this.partID = new SimpleIntegerProperty(partID);
        this.barcode = new SimpleLongProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.partName = new SimpleStringProperty(partName);
        this.price = new SimpleDoubleProperty(price);
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

    public IntegerProperty getPartID() {
        return partID;
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