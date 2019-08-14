package InventoryController;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Comparator;

/**
 * This class represents the info displayed in a row on the history tab of the inventory
 */
public class HistoryTabTableRow extends RecursiveTreeObject<HistoryTabTableRow>{

    private StringProperty studentName;
    private StringProperty studentEmail;
    private StringProperty partName;
    private IntegerProperty serialNumber;
    private StringProperty action;
    private StringProperty date;

    public HistoryTabTableRow(String studentName, String studentEmail, String partName, int serialNumber,
                              String action, String date) {
        this.studentName = new SimpleStringProperty(studentName);
        this.studentEmail = new SimpleStringProperty(studentEmail);
        this.partName = new SimpleStringProperty(partName);
        this.serialNumber = new SimpleIntegerProperty(serialNumber);
        this.action = new SimpleStringProperty(action);
        this.date = new SimpleStringProperty(date);
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

    public IntegerProperty getSerialNumber() {
        return serialNumber;
    }

    public StringProperty getAction() {
        return action;
    }

    public StringProperty getDate() {
        return date;
    }


}
