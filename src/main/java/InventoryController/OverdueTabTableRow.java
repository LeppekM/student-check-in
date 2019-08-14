package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents all of the info about parts on the overdue inventory tab
 */
public class OverdueTabTableRow extends RecursiveTreeObject<OverdueTabTableRow> {

    private StringProperty studentID;
    private StringProperty partName;
    private IntegerProperty serialNumber;
    private StringProperty dueDate;
    private StringProperty fee;

    public OverdueTabTableRow(String studentID, String partName, int serialNumber,
                              String dueDate, String fee) {
        this.studentID = new SimpleStringProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleIntegerProperty(serialNumber);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.fee = new SimpleStringProperty(fee);
    }

    public StringProperty getStudentID() {
        return studentID;
    }

    public StringProperty getPartName() {
        return partName;
    }

    public IntegerProperty getSerialNumber() {
        return serialNumber;
    }

    public StringProperty getDueDate() {
        return dueDate;
    }

    public StringProperty getFee() {
        return fee;
    }

    public void setFee(String price) {
        this.fee.set(price);
    }

}
