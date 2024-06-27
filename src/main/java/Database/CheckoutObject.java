package Database;

import java.time.LocalDate;

/**
 * Stores info about a part that is checked out todo: remove, is effectively just used to check last time a part was checked out
 */
public class CheckoutObject {

    private static String studentID, barcode, quantity, checkoutAt, dueAt, extendedCourseName, extendedProfessor;
    private static boolean isExtended;
    private LocalDate extendedReturnDate;

    private final String checkoutAtDate;
    private final String checkinAtDate;

    /**
     * Constructor
     */
    public CheckoutObject(String studentID, String barcode, String checkoutAt, String checkinAt, String dueAt) {
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

    public String getStudentID() {
        return studentID;
    }

    public String getExtendedCourseName() {
        return extendedCourseName;
    }

    public String getExtendedProfessor() {
        return extendedProfessor;
    }

    public LocalDate getExtendedReturnDate() {
        return extendedReturnDate;
    }

    public String getCheckoutAt() {
        return checkoutAt;
    }

    public String getCheckoutAtDate() {
        return checkoutAtDate;
    }

    public String getCheckinAtDate() {
        return checkinAtDate;
    }

    public String getDueAt() {
        return dueAt;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getQuantity() {
        return quantity;
    }

    public boolean isExtended() {
        return isExtended;
    }
}