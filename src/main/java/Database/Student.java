package Database;

import java.util.ArrayList;

public class Student {

    private String name;
    private int ID;
    private String email;
    private ArrayList<Part> checkedOut;
    private ArrayList<OverdueItems> overdueItems;
    private ArrayList<Part> savedItems;

    public Student(String name, int ID, String email, ArrayList<Part> checkedOut, ArrayList<OverdueItems> overdueItems, ArrayList<Part> savedItems){
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

    public ArrayList<Part> getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(ArrayList<Part> checkedOut) {
        this.checkedOut = checkedOut;
    }

    public ArrayList<OverdueItems> getOverdueItems() {
        return overdueItems;
    }

    public void setOverdueItems(ArrayList<OverdueItems> overdueItems) {
        this.overdueItems = overdueItems;
    }

    public ArrayList<Part> getSavedItems() {
        return savedItems;
    }

    public void setSavedItems(ArrayList<Part> savedItems) {
        this.savedItems = savedItems;
    }

}
