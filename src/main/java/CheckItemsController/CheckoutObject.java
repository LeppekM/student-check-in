package CheckItemsController;

public class CheckoutObject {

    private static String studentID, barcode, quantity, extendedCourseName, extendedProfessor, extendedReturnDate, faultyDescription;
    private static boolean isExtended, isFaulty;

    public CheckoutObject(String studentID, String barcode, String quantity, boolean isExtended, boolean isFaulty) {
        this.studentID = studentID;
        this.barcode = barcode;
        this.quantity = quantity;
        this.isExtended = isExtended;
        this.isFaulty = isFaulty;
    }

    public void initExtendedInfo(String extendedCourseName, String extendedProfessor, String extendedReturnDate) {
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

    public String getExtendedReturnDate() {
        return extendedReturnDate;
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