package Database;

import javafx.beans.property.SimpleStringProperty;

public class HistoryItems {

    private final SimpleStringProperty studentName, studentEmail, partName, serialNumber, status, date;

    public HistoryItems(String studentName, String studentEmail, String partName, String serialNumber, String status, String date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.status = new SimpleStringProperty(status);
        this.date = new SimpleStringProperty(date);
    }

    public String getStudentName() {
        return studentName.get();
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public String getStudentEmail() {
        return studentEmail.get();
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail.set(studentEmail);
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