package InventoryController;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;



public class CheckedOutList {
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty partName, dueDate, checkedOutAt, studentName;

    public CheckedOutList(String sName, String pName, int quantityCon, String checkOutDate, String due) {
        this.studentName = new SimpleStringProperty(sName);
        this.partName = new SimpleStringProperty(pName);
        this.quantity = new SimpleIntegerProperty(quantityCon);
        this.checkedOutAt = new SimpleStringProperty(checkOutDate);
        this.dueDate = new SimpleStringProperty(due);
    }

    public String getCheckedOutAt() { return checkedOutAt.get(); }

    public String getStudentName() { return studentName.get(); }

    public int getQuantity() {
        return quantity.get();
    }

    public String getPartName() {
        return partName.get();
    }

    public String getDueDate() {
        return dueDate.get();
    }

}
