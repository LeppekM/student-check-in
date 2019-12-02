package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

/**
 * Represents all of the info about parts on the checked out parts inventory tab
 */
public class CheckedOutTabTableRow extends RecursiveTreeObject<CheckedOutTabTableRow> {

    private StringProperty studentName, studentEmail, partName, serialNumber, fee;
    private LongProperty barcode;
    private IntegerProperty partID;
    private ObjectProperty<Date> dueDate, checkedOutAt;

    public CheckedOutTabTableRow(String studentName, String studentEmail, String partName,
                                 long barcode, String serialNumber, int partID,
                                 Date checkedOutAt, Date dueDate, String fee) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.partID = new SimpleIntegerProperty(partID);
        this.checkedOutAt = new SimpleObjectProperty<>(checkedOutAt);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
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

    public IntegerProperty getPartID() {
        return partID;
    }


    public ObjectProperty<Date> getCheckedOutAt() {
        return checkedOutAt;
    }

    public ObjectProperty<Date> getDueDate() {
        return dueDate;
    }

    public StringProperty getFee() {
        return fee;
    }

}
