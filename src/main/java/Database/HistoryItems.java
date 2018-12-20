package Database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HistoryItems {
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty student, partName, serialNumber, status, date;

    public HistoryItems(String student, String partName, String serialNumber, int quantity, String status, String date) {
        this.student = new SimpleStringProperty(student);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    public String getStudent() {
        return student.get();
    }

    public SimpleStringProperty getStudentProperty() {
        return this.student;
    }

    public void setStudent(String student) {
        this.student.set(student);
    }

    public String getPartName() {
        return partName.get();
    }

    public SimpleStringProperty getPartNameProperty() {
        return this.partName;
    }

    public void setpartName(String partName) {
        this.partName.set(partName);
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public SimpleStringProperty getSerialNumberProperty() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber.set(serialNumber);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty getStatusProperty() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty getQuantityProperty() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty getDateProperty() {
        return this.date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

}