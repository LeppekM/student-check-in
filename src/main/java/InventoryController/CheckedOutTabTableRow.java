package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CheckedOutTabTableRow extends RecursiveTreeObject<CheckedOutTabTableRow> {

    private StringProperty studentName;
    private StringProperty partName;
    private StringProperty barcode;
    private StringProperty checkedOutAt;
    private StringProperty dueDate;

    public CheckedOutTabTableRow(String studentName, String partName, String barcode,
                             String checkedOutAt, String dueDate) {
        this.studentName = new SimpleStringProperty(studentName);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleStringProperty(barcode);
        this.checkedOutAt = new SimpleStringProperty(checkedOutAt);
        this.dueDate = new SimpleStringProperty(dueDate);
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

}
