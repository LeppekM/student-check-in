package Database;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HistoryItems {

    private final SimpleStringProperty student, partName, serialNumber, status, date;

    public HistoryItems(String student, String partName, String serialNumber, String status, String date) {
        this.student = new SimpleStringProperty(student);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    public String getStudent() {
        return student.get();
    }

    public void setStudent(String student) {
        this.student.set(student);
    }

    public String getPartName() {
        return partName.get();
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber.set(serialNumber);
    }

    public String getStatus() {
        return status.get();
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

}