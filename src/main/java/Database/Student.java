package Database;

import InventoryController.CheckedOutItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Student {

    private String name;
    private int ID;
    private String email;
    private ObservableList<CheckedOutItems> checkedOut;
    private ObservableList<OverdueItem> overdueItems;
    private ObservableList<Part> savedItems;

    public Student(String name, int ID, String email, ObservableList<CheckedOutItems> checkedOut,
                   ObservableList<OverdueItem> overdueItems, ObservableList<Part> savedItems){
        this.name = name;
        this.ID = ID;
        this.email = email;
        this.checkedOut = checkedOut;
        this.overdueItems = overdueItems;
        this.savedItems = savedItems;
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

    public ObservableList<Part> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(ObservableList<Part> savedItems) {
        this.savedItems = FXCollections.observableArrayList(savedItems);
    }

}
