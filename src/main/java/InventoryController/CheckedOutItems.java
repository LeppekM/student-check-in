package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class allows the tableview for checked out items to be populated
 */
public class CheckedOutItems extends RecursiveTreeObject{
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty partName, dueDate, checkedOutAt, studentName;

    public CheckedOutItems(String sName, String pName, int quantityCon, String checkOutDate, String due) {
        this.studentName = new SimpleStringProperty(sName);
        this.partName = new SimpleStringProperty(pName);
        this.quantity = new SimpleIntegerProperty(quantityCon);
        this.checkedOutAt = new SimpleStringProperty(checkOutDate);
        this.dueDate = new SimpleStringProperty(due);
    }

    public SimpleStringProperty getCheckedOutAt() { return checkedOutAt; }

    public SimpleStringProperty getStudentName() { return studentName; }

    public SimpleIntegerProperty getQuantity() {
        return quantity;
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public SimpleStringProperty getDueDate() {
        return dueDate;
    }

}
