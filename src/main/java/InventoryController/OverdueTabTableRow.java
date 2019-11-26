package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

/**
 * Represents all of the info about parts on the overdue inventory tab
 */
public class OverdueTabTableRow extends RecursiveTreeObject<OverdueTabTableRow> {

    private StringProperty studentName, partName, dueDate, fee, serialNumber;
    private LongProperty barcode;
    private IntegerProperty studentID;


    public OverdueTabTableRow(String studentName, int studentID, String partName, long barcode,
                              String dueDate) {
        this.studentID = new SimpleIntegerProperty(studentID);
        this.partName = new SimpleStringProperty(partName);
        this.studentName = new SimpleStringProperty(studentName);
        this.barcode = new SimpleLongProperty(barcode);
        this.dueDate = new SimpleStringProperty(dueDate);

    }

    public IntegerProperty getStudentID() {
        return studentID;
    }

    public StringProperty getPartName() {
        return partName;
    }

    public LongProperty getBarcode(){return barcode;}

    public StringProperty getStudentName(){return studentName;}

    public StringProperty getDueDate() {
        return dueDate;
    }



}
