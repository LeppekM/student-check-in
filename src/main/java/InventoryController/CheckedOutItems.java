package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

/**
 * This class allows the tableview for checked out items to be populated
 */
public class CheckedOutItems extends RecursiveTreeObject{
    private final SimpleIntegerProperty checkoutID, studentID, partID;
    private final SimpleStringProperty studentName, studentEmail, partName, barcode, serialNumber, fee;
    private ObjectProperty<Date> dueDate, checkedOutDate;

    public CheckedOutItems(int checkoutID, String studentName, String studentEmail, int studentID, String partName, String barcode, String serialNumber, int partID, Date checkedOutDate, Date dueDate, String fee) {
        this.checkoutID = new SimpleIntegerProperty(checkoutID);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.studentID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleStringProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.partID = new SimpleIntegerProperty(partID);
        this.checkedOutDate = new SimpleObjectProperty<>(checkedOutDate);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.fee = new SimpleStringProperty(fee);
    }

    public SimpleIntegerProperty getCheckoutID() {
        return checkoutID;
    }

    public SimpleStringProperty getStudentName() { return studentName; }

    public SimpleStringProperty getStudentEmail() {
        return studentEmail;
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public SimpleStringProperty getBarcode() {
        return barcode;
    }

    public SimpleStringProperty getSerialNumber() {
        return serialNumber;
    }

    public SimpleIntegerProperty getPartID() {return partID; }

    public ObjectProperty<Date> getCheckedOutDate() { return checkedOutDate; }

    public ObjectProperty<Date> getDueDate() {
        return dueDate;
    }

    public SimpleStringProperty getFee() {
        return fee;
    }

}
