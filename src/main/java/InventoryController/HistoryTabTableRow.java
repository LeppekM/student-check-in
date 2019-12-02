package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;

/**
 * This class represents the info displayed in a row on the history tab of the inventory
 */
public class HistoryTabTableRow extends RecursiveTreeObject<HistoryTabTableRow>{

    private StringProperty studentName;
    private StringProperty studentEmail;
    private StringProperty partName;
    private LongProperty barcode;

    private StringProperty action;
    private ObjectProperty<Date> date;

    public HistoryTabTableRow(String studentName, String studentEmail, String partName, long barcode,
                              String action, Date date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.barcode = new SimpleLongProperty(barcode);
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleObjectProperty<Date>(date) {
        };
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


}
