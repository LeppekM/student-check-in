package Database.ObjectClasses;

import Database.OverdueItem;
import javafx.collections.ObservableList;

/**
 * This class is a Java Object that represents a Student
 */
public class Student {

    private int uniqueID, rfid;
    private String name, email, firstName, lastName, date;
    private ObservableList<Checkout> checkedOut;
    private ObservableList<OverdueItem> overdueItems;

    public Student(String name, int uniqueID, int rfid, String email, String date, ObservableList<Checkout> checkedOut,
                   ObservableList<OverdueItem> overdueItems){
        this.name = name;
        this.uniqueID = uniqueID;
        this.rfid = rfid;
        this.email = email;
        this.date = date;
        this.checkedOut = checkedOut;
        this.overdueItems = overdueItems;
    }

    public Student(String name, int rfid, String email) {
        this.name = name;
        this.rfid = rfid;
        this.email = email;
    }

    public Student(String firstName, String lastName, int rfid, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.rfid = rfid;
        this.email = email;
    }

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUniqueID(){
        return uniqueID;
    }

    public int getRFID() {
        return rfid;
    }

    public void setRFID(int RFID) {
        this.rfid = RFID;
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

    public ObservableList<Checkout> getCheckedOut() {
        return checkedOut;
    }

    public ObservableList<OverdueItem> getOverdueItems() {
        return overdueItems;
    }

    /**
     * @return OverdueItem if Student has part checked out and overdue, null otherwise
     */
    public OverdueItem getOverdueItem(String checkInID) {
        OverdueItem overdueItem = null;
        for(OverdueItem part : overdueItems) {
            if (part.getCheckID().get().equals(checkInID)) {
                overdueItem = part;
            }
        }
        return overdueItem;
    }

}
