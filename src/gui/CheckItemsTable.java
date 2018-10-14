package gui;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class CheckItemsTable {
    private TextField studentID;
    private TextField barcode;
    private TextField partName;
    private TextField quantity;
    private CheckBox checkBox;
    private Button button;

    CheckItemsTable(String studentID, String barcode, String partName, String quantity){
        this.studentID = new TextField(studentID);
        this.barcode = new TextField(barcode);
        this.partName = new TextField(partName);
        this.quantity = new TextField(quantity);
        this.checkBox = new CheckBox();
        this.button = new Button ("Submit");

    }

    public TextField getStudentID() {
        return studentID;
    }

    public void setStudentID(TextField studentID) {
        this.studentID = studentID;
    }

    public TextField getBarcode() {
        return barcode;
    }

    public void setBarcode(TextField barcode) {
        this.barcode = barcode;
    }

    public TextField getPartName() {
        return partName;
    }

    public void setPartName(TextField partName) {
        this.partName = partName;
    }

    public TextField getQuantity() {
        return quantity;
    }

    public void setQuantity(TextField quantity) {
        this.quantity = quantity;
    }

    public CheckBox getOvernight() {
        return checkBox;
    }

    public void setOvernight(CheckBox overnight) {
        this.checkBox = overnight;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
