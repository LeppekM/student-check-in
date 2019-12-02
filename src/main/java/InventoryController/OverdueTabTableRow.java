package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

import java.util.Date;

/**
 * Represents all of the info about parts on the overdue inventory tab
 */
public class OverdueTabTableRow extends RecursiveTreeObject<OverdueTabTableRow> {

    private StringProperty studentName, partName, fee, serialNumber;
    private LongProperty barcode;
    private IntegerProperty studentID;
    private ObjectProperty<Date> dueDate;


    public OverdueTabTableRow(String studentName, int studentID, String partName, long barcode,
                              Date dueDate) {
        this.studentID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.studentName = new SimpleStringProperty(studentName);
        this.barcode = new SimpleLongProperty(barcode);
        this.dueDate = new SimpleObjectProperty<Date>(dueDate);

    }

    public IntegerProperty getStudentID() {
        return studentID;
    }

    public StringProperty getPartName() {
        return partName;
    }

    public LongProperty getBarcode(){return barcode;}

    public StringProperty getStudentName(){return studentName;}

    public ObjectProperty<Date> getDueDate() {
        return dueDate;
    }



}
