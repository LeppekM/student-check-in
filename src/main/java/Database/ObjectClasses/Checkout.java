package Database.ObjectClasses;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

/**
 * Object which represents checkout entities in database
 */
public class Checkout extends RecursiveTreeObject {
    private final StringProperty studentName, studentEmail, partName;
    private StringProperty action, serialNumber, fee;
    private final LongProperty barcode;
    private IntegerProperty checkoutID, studentID, partID;
    private ObjectProperty<Date> dueDate, checkedOutDate;

    public Checkout(String studentName, String studentEmail, String partName, long barcode,
                 String action, Date date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.action = new SimpleStringProperty(action);
        this.dueDate = new SimpleObjectProperty<>(date);
    }

    public Checkout(int checkoutID, String studentName, String studentEmail, int studentID, String partName,
                    long barcode, String serialNumber, int partID, Date checkedOutDate, Date dueDate, String fee) {
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

    public IntegerProperty getStudentID() {
        return studentID;
    }
}
