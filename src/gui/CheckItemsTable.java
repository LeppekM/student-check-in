package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class CheckItemsTable {
    private final SimpleStringProperty studentID;
    private final SimpleStringProperty barcode;
    private final SimpleStringProperty partName;
    private final SimpleStringProperty quantity;
    private CheckBox checkBox;
    private Button button;

    CheckItemsTable(String studentID, String barcode, String partName, String quantity){
        this.studentID = new SimpleStringProperty(studentID);
        this.barcode = new SimpleStringProperty(barcode);
        this.partName = new SimpleStringProperty(partName);
        this.quantity = new SimpleStringProperty(quantity);
        this.checkBox = new CheckBox();
        this.button = new Button ("Submit");
    }


    public String getStudentID() {
        return studentID.get();
    }

    public SimpleStringProperty studentIDProperty() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID.set(studentID);
    }

    public String getBarcode() {
        return barcode.get();
    }

    public SimpleStringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public String getPartName() {
        return partName.get();
    }

    public SimpleStringProperty partNameProperty() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName.set(partName);
    }

    public String getQuantity() {
        return quantity.get();
    }

    public SimpleStringProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity.set(quantity);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
