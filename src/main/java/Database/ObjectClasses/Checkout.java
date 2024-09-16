package Database.ObjectClasses;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

/**
 * Object which represents checkout entities in database
 */
public class Checkout extends RecursiveTreeObject {
    private final ObjectProperty<Date> dueDate;
    private StringProperty studentName, studentEmail, partName, action, serialNumber, fee, professor, course;
    private final LongProperty barcode;
    private IntegerProperty checkoutID, partID;
    private LongProperty studentID;
    private ObjectProperty<Date> checkedOutDate, checkedInDate;

    public Checkout(String studentName, String studentEmail, String partName, long barcode,
                 String action, Date date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.action = new SimpleStringProperty(action);
        this.dueDate = new SimpleObjectProperty<>(date);
    }

    public Checkout(int checkoutID, String studentName, String studentEmail, long studentID, String partName,
                    long barcode, String serialNumber, int partID, Date checkedOutDate, Date dueDate, String fee) {
        this.checkoutID = new SimpleIntegerProperty(checkoutID);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.studentID = new SimpleLongProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.partID = new SimpleIntegerProperty(partID);
        this.checkedOutDate = new SimpleObjectProperty<>(checkedOutDate);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.fee = new SimpleStringProperty(fee);
    }

    public Checkout(long studentID, long barcode, Date checkoutAt, Date checkinAt, Date dueAt, String course, String professor) {
        this.studentID = new SimpleLongProperty(studentID);
        this.barcode = new SimpleLongProperty(barcode);
        this.checkedOutDate = new SimpleObjectProperty<>(checkoutAt);
        this.checkedInDate = checkinAt != null ? new SimpleObjectProperty<>(checkinAt) : null;
        this.dueDate = new SimpleObjectProperty<>(dueAt);
        this.professor = professor != null ? new SimpleStringProperty(professor) : null;
        this.course = course != null ? new SimpleStringProperty(course) : null;
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

    public StringProperty getProfessor() {
        return professor;
    }

    public StringProperty getCourse() {
        return course;
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

    public ObjectProperty<Date> getCheckedInDate() {
        return checkedInDate;
    }

    public StringProperty getFee() {
        return fee;
    }

    public LongProperty getStudentID() {
        return studentID;
    }
}
