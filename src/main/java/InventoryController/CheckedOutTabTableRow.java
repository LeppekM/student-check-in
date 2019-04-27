package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CheckedOutTabTableRow extends RecursiveTreeObject<CheckedOutTabTableRow> {

    private StringProperty studentName, studentEmail, partName, barcode, serialNumber, checkedOutAt, dueDate, fee;
    private IntegerProperty partID;

    public CheckedOutTabTableRow(String studentName, String studentEmail, String partName,
                                 String barcode, String serialNumber, int partID,
                                 String checkedOutAt, String dueDate, String fee) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleStringProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.partID = new SimpleIntegerProperty(partID);
        this.checkedOutAt = new SimpleStringProperty(checkedOutAt);
        this.dueDate = new SimpleStringProperty(dueDate);
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

    public IntegerProperty getPartID() {
        return partID;
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

}
