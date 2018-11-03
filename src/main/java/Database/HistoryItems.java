package Database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HistoryItems {
    private SimpleIntegerProperty quantity;
    private SimpleStringProperty student, partName, serialNumber, location, date;

    public HistoryItems(String student, String partName, String serialNumber, String location, int quantity, String date) {
        this.student = new SimpleStringProperty(student);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.location = new SimpleStringProperty(location);
        this.quantity = new SimpleIntegerProperty(quantity);
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

    public String getLocation() {
        return location.get();
    }

    public SimpleStringProperty getLocationProperty() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location.set(location);
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