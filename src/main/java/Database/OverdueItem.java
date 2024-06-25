package Database;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

public class OverdueItem extends RecursiveTreeObject {
    private SimpleIntegerProperty ID;
    private SimpleStringProperty partName;
    private SimpleLongProperty barcode;
    private ObjectProperty<Date> dueDate;
    private SimpleDoubleProperty price;
    private SimpleStringProperty studentName;
    private SimpleStringProperty email;
    private SimpleStringProperty checkID;
    private SimpleStringProperty serialNumber;

    public OverdueItem(int studentID, String studentName, String email, String partName, String serialNumber, long barcodeCon, Date dueDate, String checkID) {
        this.ID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcodeCon);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.studentName = new SimpleStringProperty(studentName);
        this.email = new SimpleStringProperty(email);
        this.checkID = new SimpleStringProperty(checkID);
        this.serialNumber = new SimpleStringProperty(serialNumber);
    }

    public OverdueItem(int studentID, String studentName, String email, String partName, long barcodeCon, Date dueDate, String checkID, double price) {
        this.ID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcodeCon);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.studentName = new SimpleStringProperty(studentName);
        this.email = new SimpleStringProperty(email);
        this.checkID = new SimpleStringProperty(checkID);
        this.price = new SimpleDoubleProperty(price);
    }

    public SimpleIntegerProperty getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public SimpleDoubleProperty getPrice() {
        return price;
    }

    public SimpleStringProperty getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName.set(partName);
    }

    public SimpleLongProperty getBarcode() {
        return barcode;
    }

    public ObjectProperty<Date> getDueDate() {
        return dueDate;
    }

    public SimpleStringProperty getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public SimpleStringProperty getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public SimpleStringProperty getSerialNumber() {
        return serialNumber;
    }

}
