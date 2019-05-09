package CheckItemsController;

import java.sql.Date;
import java.time.LocalDate;

public class CheckoutObject {

    private static String studentID, barcode, quantity, checkoutAt, dueAt, extendedCourseName, extendedProfessor, faultyDescription;
    private static boolean isExtended, isFaulty;
    private LocalDate extendedReturnDate;

    private String checkoutAtDate, checkinAtDate;

    public CheckoutObject(String studentID, String barcode, String quantity, boolean isExtended, boolean isFaulty) {
        this.studentID = studentID;
        this.barcode = barcode;
        this.quantity = quantity;
        this.isExtended = isExtended;
        this.isFaulty = isFaulty;
    }

    public CheckoutObject(String studentID, String barcode, String checkoutAt, String checkinAt, String dueAt) {
        this.studentID = studentID;
        this.barcode = barcode;
        this.checkoutAtDate = checkoutAt;
        this.checkinAtDate = checkinAt;
        this.dueAt = dueAt;
    }

    public void initExtendedInfo(String extendedCourseName, String extendedProfessor, LocalDate extendedReturnDate) {
        this.extendedCourseName = extendedCourseName;
        this.extendedProfessor = extendedProfessor;
        this.extendedReturnDate = extendedReturnDate;
    }

    public void initFaultyInfo(String faultyDescription) {
        this.faultyDescription = faultyDescription;
    }

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

    public String getFaultyDescription() {
        return faultyDescription;
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

    public boolean isFaulty() {
        return isFaulty;
    }
}