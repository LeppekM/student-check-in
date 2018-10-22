package gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {


    private Date dateOfLastCheckout;
    private String name;
    private String rfid;
    private String email;
    private List<Part> checkedOut;
    private List<Part> overdue;
    private List<Part> saved;

    public Student(String name, String rfid, String email) {
        dateOfLastCheckout = null;
        this.name = name;
        this.rfid = rfid;
        this.email = email;
        this.checkedOut = new ArrayList<>();
        this.overdue = new ArrayList<>();
        this.saved = new ArrayList<>();
    }

    public Date getDateOfLastCheckout() {
        return dateOfLastCheckout;
    }

    public String getName() {
        return name;
    }

    public String getRfid() {
        return rfid;
    }

    public String getEmail() {
        return email;
    }

    public List<Part> getCheckedOut() {
        return checkedOut;
    }

    public List<Part> getOverdue() {
        return overdue;
    }

    public List<Part> getSaved() {
        return saved;
    }

}
