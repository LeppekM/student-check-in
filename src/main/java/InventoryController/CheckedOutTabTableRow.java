package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CheckedOutTabTableRow extends RecursiveTreeObject<CheckedOutTabTableRow> {

    private StringProperty studentName, partName, barcode, checkedOutAt, dueDate;
    private IntegerProperty partID;

    public CheckedOutTabTableRow(String studentName, String partName, String barcode,
                             String checkedOutAt, String dueDate, int partID) {
        this.studentName = new SimpleStringProperty(studentName);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleStringProperty(barcode);
        this.checkedOutAt = new SimpleStringProperty(checkedOutAt);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.partID = new SimpleIntegerProperty(partID);
    }

    public StringProperty getStudentName() {
        return studentName;
    }

    public StringProperty getPartName() {
        return partName;
    }

    public StringProperty getBarcode() {
        return barcode;
    }

    public StringProperty getCheckedOutAt() {
        return checkedOutAt;
    }

    public StringProperty getDueDate() {
        return dueDate;
    }

    public IntegerProperty getPartID() {
        return partID;
    }

}
