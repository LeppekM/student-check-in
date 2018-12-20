package Database;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SavedPart extends RecursiveTreeObject{

    private SimpleStringProperty studentName;
    private SimpleStringProperty partName;
    private SimpleStringProperty checkedOutAt;
    private SimpleIntegerProperty quantity;
    private SimpleStringProperty savedAt;
    private SimpleStringProperty dueAt;
    private SimpleStringProperty checkID;
    private SimpleStringProperty prof;
    private SimpleStringProperty course;
    private SimpleStringProperty reason;

    public SavedPart(String sName, String pName, String coAt, int quantity, String sAt, String dAt, String cID, String prof, String course, String reason){
        studentName = new SimpleStringProperty(sName);
        partName = new SimpleStringProperty(pName);
        checkedOutAt = new SimpleStringProperty(coAt);
        this.quantity = new SimpleIntegerProperty(quantity);
        savedAt = new SimpleStringProperty(sAt);
        dueAt = new SimpleStringProperty(dAt);
        checkID = new SimpleStringProperty(cID);
        this.prof = new SimpleStringProperty(prof);
        this.course = new SimpleStringProperty(course);
        this.reason = new SimpleStringProperty(reason);
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

    public String getCheckID() {
        return checkID.get();
    }

    public SimpleStringProperty checkIDProperty() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID.set(checkID);
    }

    public String getProf() {
        return prof.get();
    }

    public SimpleStringProperty profProperty() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof.set(prof);
    }

    public String getCourse() {
        return course.get();
    }

    public SimpleStringProperty courseProperty() {
        return course;
    }

    public void setCourse(String course) {
        this.course.set(course);
    }

    public String getReason() {
        return reason.get();
    }

    public SimpleStringProperty reasonProperty() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason.set(reason);
    }
}
