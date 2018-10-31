package InventoryController;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class CheckedOutList {
    private final SimpleIntegerProperty partID, quantity;
    private final SimpleStringProperty partName, dueDate;

    public CheckedOutList(int id, String name, int quantityCon, String due) {
        this.partID = new SimpleIntegerProperty(id);
        this.partName = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantityCon);
        this.dueDate = new SimpleStringProperty(due);
    }

    public int getPartID() {
        return partID.get();
    }

    public SimpleIntegerProperty partIDProperty() {
        return partID;
    }

    public void setPartID(int partID) {
        this.partID.set(partID);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getPartName() {
        return partName.get();
    }

    public SimpleStringProperty partNameProperty() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName.set(partName);
    }

    public String getDueDate() {
        return dueDate.get();
    }

    public SimpleStringProperty dueDateProperty() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate.set(dueDate);
    }

}
