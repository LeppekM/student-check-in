package Database;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class HistoryItems {

    private final SimpleStringProperty studentName, studentEmail, partName, action, date;
    private SimpleLongProperty barcode;

    public HistoryItems(String studentName, String studentEmail, String partName, long barcode, String action, String date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.action = new SimpleStringProperty(action);
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

    public long getBarcode() {
        return barcode.get();
    }


    public String getAction() {
        return action.get();
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

}