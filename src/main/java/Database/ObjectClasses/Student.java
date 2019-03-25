package Database.ObjectClasses;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Database.OverdueItem;

public class Student {

    private String name;
    private int uniqueID;
    private int RFID;
    private String email;
    private String date;
    private ObservableList<CheckedOutItems> checkedOut;
    private ObservableList<OverdueItem> overdueItems;
    private ObservableList<SavedPart> savedItems;

    public Student(String name, int uniqueID, int RFID, String email, String date, ObservableList<CheckedOutItems> checkedOut,
                   ObservableList<OverdueItem> overdueItems, ObservableList<SavedPart> savedItems){
        this.name = name;
        this.uniqueID = uniqueID;
        this.RFID = RFID;
        this.email = email;
        this.date = date;
        this.checkedOut = checkedOut;
        this.overdueItems = overdueItems;
        this.savedItems = savedItems;
    }

    public Student(String name, int RFID, String email) {
        this.name = name;
        this.RFID = RFID;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUniqueID(){
        return uniqueID;
    }

    public int getRFID() {
        return RFID;
    }

    public void setRFID(int RFID) {
        this.RFID = RFID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public ObservableList<CheckedOutItems> getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(ObservableList<CheckedOutItems> checkedOut) {
        this.checkedOut = FXCollections.observableArrayList(checkedOut);
    }

    public ObservableList<OverdueItem> getOverdueItems() {
        return overdueItems;
    }

    public void setOverdueItems(ObservableList<OverdueItem> overdueItems) {
        this.overdueItems = FXCollections.observableArrayList(overdueItems);
    }

    public ObservableList<SavedPart> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(ObservableList<SavedPart> savedItems) {
        this.savedItems = FXCollections.observableArrayList(savedItems);
    }

}
