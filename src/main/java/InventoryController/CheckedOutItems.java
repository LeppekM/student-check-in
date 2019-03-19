package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class allows the tableview for checked out items to be populated
 */
public class CheckedOutItems extends RecursiveTreeObject{
    private final SimpleIntegerProperty checkoutID, partID;
    private final SimpleLongProperty barcode;
    private final SimpleStringProperty partName, dueDate, checkedOutAt, studentName;

    public CheckedOutItems(String sName, String pName, long barcodeCon, String checkOutDate, String due, int checkoutIDCon, int partID) {
        this.studentName = new SimpleStringProperty(sName);
        this.partName = new SimpleStringProperty(pName);
        this.barcode = new SimpleLongProperty(barcodeCon);
        this.checkedOutAt = new SimpleStringProperty(checkOutDate);
        this.dueDate = new SimpleStringProperty(due);
        this.checkoutID = new SimpleIntegerProperty(checkoutIDCon);
        this.partID = new SimpleIntegerProperty(partID);

    }

    public SimpleStringProperty getCheckedOutAt() { return checkedOutAt; }

    public SimpleStringProperty getStudentName() { return studentName; }

    public SimpleLongProperty getBarcode() {
        return barcode;
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public SimpleStringProperty getDueDate() {
        return dueDate;
    }

    public SimpleIntegerProperty getCheckID() {return checkoutID; }

    public SimpleIntegerProperty getPartID() {return partID; }

}
