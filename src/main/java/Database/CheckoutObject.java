package Database;

import java.util.Date;

/**
 * Stores info about a part that is checked out todo: remove, is effectively just used to check last time a part was checked out
 */
public class CheckoutObject {

    private static String studentID, barcode, extendedCourseName, extendedProfessor;
    private static Date dueAt;

    private final Date checkoutAtDate;
    private final Date checkinAtDate;

    /**
     * Constructor
     */
    public CheckoutObject(String studentID, String barcode, Date checkoutAt, Date checkinAt, Date dueAt) {
        CheckoutObject.studentID = studentID;
        CheckoutObject.barcode = barcode;
        this.checkoutAtDate = checkoutAt;
        this.checkinAtDate = checkinAt;
        CheckoutObject.dueAt = dueAt;
    }

    /*************************
     *
     * Below are methods to get the stored info about the checked out part
     *
     ************************/

    public String getExtendedCourseName() {
        return extendedCourseName;
    }

    public String getExtendedProfessor() {
        return extendedProfessor;
    }

    public Date getCheckoutAtDate() {
        return checkoutAtDate;
    }

    public Date getCheckinAtDate() {
        return checkinAtDate;
    }

    public Date getDueAt() {
        return dueAt;
    }

}