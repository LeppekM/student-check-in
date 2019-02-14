package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Student {

    private String name;
    private int ID;
    private String email;
    private String date;
    private ObservableList<CheckedOutItems> checkedOut;
    private ObservableList<OverdueItem> overdueItems;
    private ObservableList<SavedPart> savedItems;

    public Student(String name, int ID, String email, String date, ObservableList<CheckedOutItems> checkedOut,
                   ObservableList<OverdueItem> overdueItems, ObservableList<SavedPart> savedItems){
        this.name = name;
        this.ID = ID;
        this.email = email;
        this.date = date;
        this.checkedOut = checkedOut;
        this.overdueItems = overdueItems;
        this.savedItems = savedItems;
    }

    public Student(String name, int ID, String email) {
        this.name = name;
        this.ID = ID;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
