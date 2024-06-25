package Database.ObjectClasses;

import javafx.beans.property.*;

import java.util.Date;

public class Checkout {
    private StringProperty studentName, studentEmail, partName, action, serialNumber, fee;
    private LongProperty barcode;
    private IntegerProperty checkoutID, studentID, partID;
    private ObjectProperty<Date> date, dueDate, checkedOutDate;

    public Checkout(String studentName, String studentEmail, String partName, long barcode,
                 String action, Date date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleObjectProperty<Date>(date) {
        };
    }

    public Checkout(int checkoutID, String studentName, String studentEmail, int studentID, String partName, long barcode, String serialNumber, int partID, Date checkedOutDate, Date dueDate, String fee) {
        this.checkoutID = new SimpleIntegerProperty(checkoutID);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.studentID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.partID = new SimpleIntegerProperty(partID);
        this.checkedOutDate = new SimpleObjectProperty<>(checkedOutDate);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.fee = new SimpleStringProperty(fee);
    }

    public StringProperty getStudentName() {
        return studentName;
    }

    public StringProperty getStudentEmail() {
        return studentEmail;
    }

    public StringProperty getPartName() {
        return partName;
    }

    public LongProperty getBarcode() {
        return barcode;
    }

    public StringProperty getAction() {
        return action;
    }

    public ObjectProperty<Date> getDate() {
        return date;
    }

    public IntegerProperty getCheckoutID() {
        return checkoutID;
    }

    public StringProperty getSerialNumber() {
        return serialNumber;
    }

    public IntegerProperty getPartID() {
        return partID;
    }

    public ObjectProperty<Date> getCheckedOutDate() {
        return checkedOutDate;
    }

    public ObjectProperty<Date> getDueDate() {
        return dueDate;
    }

    public StringProperty getFee() {
        return fee;
    }
}
