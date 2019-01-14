package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class allows the tableview for checked out items to be populated
 */
public class CheckedOutItems extends RecursiveTreeObject{
    private final SimpleIntegerProperty barcode, checkoutID;
    private final SimpleStringProperty partName, dueDate, checkedOutAt, studentName;

    public CheckedOutItems(String sName, String pName, int barcodeCon, String checkOutDate, String due, int checkoutIDCon) {
        this.studentName = new SimpleStringProperty(sName);
        this.partName = new SimpleStringProperty(pName);
        this.barcode = new SimpleIntegerProperty(barcodeCon);
        this.checkedOutAt = new SimpleStringProperty(checkOutDate);
        this.dueDate = new SimpleStringProperty(due);
        this.checkoutID = new SimpleIntegerProperty(checkoutIDCon);

    }

    public SimpleStringProperty getCheckedOutAt() { return checkedOutAt; }

    public SimpleStringProperty getStudentName() { return studentName; }

    public SimpleIntegerProperty getBarcode() {
        return barcode;
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public SimpleStringProperty getDueDate() {
        return dueDate;
    }

    public SimpleIntegerProperty getCheckID() {return checkoutID; }

}
