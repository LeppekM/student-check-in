package Database;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SavedPart {

    private SimpleStringProperty studentName;
    private SimpleStringProperty partName;
    private SimpleStringProperty checkedOutAt;
    private SimpleIntegerProperty quantity;
    private SimpleStringProperty savedAt;
    private SimpleStringProperty dueAt;

    public SavedPart(String sName, String pName, String coAt, int quantity, String sAt, String dAt){
        studentName = new SimpleStringProperty(sName);
        partName = new SimpleStringProperty(pName);
        checkedOutAt = new SimpleStringProperty(coAt);
        this.quantity = new SimpleIntegerProperty(quantity);
        savedAt = new SimpleStringProperty(sAt);
        dueAt = new SimpleStringProperty(dAt);
    }

    public SimpleStringProperty getStudentName() {
        return studentName;
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public SimpleStringProperty partNameProperty() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName.set(partName);
    }

    public SimpleStringProperty getCheckedOutAt() {
        return checkedOutAt;
    }

    public SimpleStringProperty checkedOutAtProperty() {
        return checkedOutAt;
    }

    public void setCheckedOutAt(String checkedOutAt) {
        this.checkedOutAt.set(checkedOutAt);
    }

    public SimpleIntegerProperty getQuantity() {
        return quantity;
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public SimpleStringProperty getSavedAt() {
        return savedAt;
    }

    public SimpleStringProperty savedAtProperty() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt.set(savedAt);
    }

    public SimpleStringProperty getDueAt() {
        return dueAt;
    }

    public SimpleStringProperty dueAtProperty() {
        return dueAt;
    }

    public void setDueAt(String dueAt) {
        this.dueAt.set(dueAt);
    }
}
